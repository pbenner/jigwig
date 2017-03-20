/* Copyright (C) 2017 Philipp Benner
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.lang.UnsupportedOperationException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;

/* -------------------------------------------------------------------------- */

public class BbiFileIterator implements Iterator<BbiFileIteratorType> {

    BbiFile file;
    int idx;
    int from;
    int to;
    int binsize;

    RTreeTraverser traverser;
    BbiBlockDecoderIterator decoderIterator;
    boolean dataIsRaw;

    BbiSummaryRecord result;
    BbiSummaryRecord result_next;
    BbiSummaryRecord result_tmp;

    byte[] uncompressBuf;

    BbiFileIterator(BbiFile file, int idx, int from, int to, int binsize) {
        if (binsize != 0) {
            from = divInt.Down(from, binsize)*binsize;
            to   = divInt.Up  (to,   binsize)*binsize;
        }
        this.file    = file;
        this.idx     = idx;
        this.from    = from;
        this.to      = to;
        this.binsize = binsize;
        // buffer for uncompressing blocks
        uncompressBuf = new byte[(int)file.Header.UncompressBufSize];
        // index of a matching zoom level for the given binsize
        int zoomIdx = -1;
        for (int i = 0; i < file.Header.ZoomLevels; i++) {
            if (binsize >= file.Header.ZoomHeaders[i].ReductionLevel &&
                binsize %  file.Header.ZoomHeaders[i].ReductionLevel == 0) {
                zoomIdx = i;
            }
        }
        if (zoomIdx != -1) {
            traverser = new RTreeTraverser(file.IndexZoom[zoomIdx], idx, from, to);
            dataIsRaw = false;
        } else {
            traverser = new RTreeTraverser(file.Index, idx, from, to);
            dataIsRaw = true;
        }
        result      = new BbiSummaryRecord();
        result_next = new BbiSummaryRecord();
        // initialize result_next
        next();
    }

    public boolean hasNext() {
        return result_next != null;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    public BbiFileIteratorType next() {
        try {
            return new BbiFileIteratorType(next_());
        }
        catch (IOException e) {
            result_next = null;
            return new BbiFileIteratorType(e);
        }
    }

    public BbiSummaryRecord next_() throws IOException {
        BbiSummaryRecord record;
        // calling next is invalid if result_next was already
        // set to null by a previous call
        if (result_next == null) {
            throw new NoSuchElementException();
        }
        // swap result and result_next
        result_tmp  = result;
        result      = result_next;
        result_next = result_tmp;
        // reset result_next so it can carry the new result
        result_next.Reset();

        while (true) {
            // check if we need to get a new block
            if (decoderIterator == null || !decoderIterator.Ok()) {
                traverser.Next();
                if (traverser.Ok()) {
                    RTreeTraverserType r = traverser.Get();
                    ByteBuffer buffer = r.Vertex.ReadBlock(file.Channel, r.Idx, uncompressBuf);
                    buffer.order(file.Header.byteOrder);
                    if (dataIsRaw) {
                        BbiRawBlockDecoder decoder = new BbiRawBlockDecoder(buffer);
                        decoderIterator = decoder.Decode();
                    } else {
                        BbiZoomBlockDecoder decoder = new BbiZoomBlockDecoder(buffer);
                        decoderIterator = decoder.Decode();
                    }
                } else {
                    // no new block found, done
                    break;
                }
            }
            // loop over block
            for (record = decoderIterator.Get(); decoderIterator.Ok(); decoderIterator.Next()) {
                if (record.From < from || record.To > to) {
                    continue;
                }
                if (binsize % (record.To - record.From) != 0) {
                    throw new IOException("invalid bin size");
                }
                // set `from' if this is the first record
                if (result_next.Valid == 0) {
                    result_next.From = record.From;
                }
                // add contents of current record to the resulting record
                result_next.AddRecord(record);
                result_next.To = record.To;
                // stop if current result record is full
                if (result_next.To - result_next.From >= binsize) {
                    break;
                }
            }
        }
        // check if new data was found
        if (result_next.Valid == 0) {
            result_next = null;
        }
        return result;
    }
}
