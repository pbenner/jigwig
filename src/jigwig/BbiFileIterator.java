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

package jigwig;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.lang.UnsupportedOperationException;
import java.nio.ByteBuffer;

/* -------------------------------------------------------------------------- */

public class BbiFileIterator implements Iterator<BbiFileIteratorType> {

    BbiFile file;
    int chromId;
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

    BbiFileIterator(BbiFile file, int chromId, int from, int to, int binsize) throws IOException {
        if (binsize != 0) {
            from = DivInt.Down(from, binsize)*binsize;
            to   = DivInt.Up  (to,   binsize)*binsize;
        }
        this.file    = file;
        this.chromId = chromId;
        this.from    = from;
        this.to      = to;
        this.binsize = binsize;
        // buffer for uncompressing blocks
        uncompressBuf = new byte[(int)file.Header.UncompressBufSize];
        // index of a matching zoom level for the given binsize
        int zoomIdx = -1;
        for (int i = 0; i < file.Header.ZoomLevels; i++) {
            if (binsize >= file.Header.ZoomHeaders[i].ReductionLevel) {
                zoomIdx = i;
            }
        }
        if (zoomIdx != -1) {
            traverser = new RTreeTraverser(file.IndexZoom[zoomIdx], chromId, from, to);
            dataIsRaw = false;
        } else {
            traverser = new RTreeTraverser(file.Index, chromId, from, to);
            dataIsRaw = true;
        }
        result      = new BbiSummaryRecord();
        result_next = new BbiSummaryRecord();
        // initialize result_next
        next_();
    }

    public boolean hasNext() {
        return result_next != null;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    public BbiFileIteratorType next() throws NoSuchElementException {
        try {
            return new BbiFileIteratorType(next_());
        }
        catch (IOException e) {
            result_next = null;
            return new BbiFileIteratorType(e);
        }
    }

    void nextDecoderIterator() throws IOException {
        RTreeTraverserType r = traverser.Get();
        // read block
        ByteBuffer buffer = r.Vertex.ReadBlock(file.Channel, r.Idx, uncompressBuf);
        buffer.order(file.Header.byteOrder);
        if (dataIsRaw) {
            BbiRawBlockDecoder decoder = new BbiRawBlockDecoder(buffer);
            decoderIterator = decoder.Decode();
        } else {
            BbiZoomBlockDecoder decoder = new BbiZoomBlockDecoder(buffer);
            decoderIterator = decoder.Decode();
        }
    }
    BbiSummaryRecord next_() throws NoSuchElementException, IOException {
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
        result_next.reset();

        while (true) {
            // check if we need to get a new block
            if (decoderIterator == null || !decoderIterator.Ok()) {
                if (traverser.Ok()) {
                    nextDecoderIterator();
                    traverser.Next();
                } else {
                    // no new block found, done
                    break;
                }
            }
            // loop over block
            for (record = decoderIterator.Get(); decoderIterator.Ok(); decoderIterator.Next()) {
                if (record.ChromId != chromId) {
                    continue;
                }
                if (record.From < from || record.To > to) {
                    continue;
                }
                // set `from' if this is the first record
                if (result_next.Valid == 0) {
                    result_next.ChromId = record.ChromId;
                    result_next.From    = record.From;
                    result_next.To      = record.From;
                }
                if (result_next.From + binsize < record.From) {
                    return result;
                }
                // add contents of current record to the resulting record
                result_next.addRecord(record);
                // stop if current result record is full
                if (result_next.To - result_next.From >= binsize) {
                    decoderIterator.Next();
                    return result;
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
