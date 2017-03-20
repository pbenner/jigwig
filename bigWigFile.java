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
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;

/* -------------------------------------------------------------------------- */

public class bigWigFile extends BbiFile {

    static final long MAGIC = 0x888FFC26L;

    Genome genome;

    public bigWigFile(SeekableByteChannel channel) throws IOException {
        Header = new BbiHeader();
        // parse header
        Header.Read(channel, MAGIC);
        ChromData = new BData();
        Index     = new RTree();
        IndexZoom = new RTree[Header.ZoomLevels];
        // parse chromosome data
        channel.position(Header.CtOffset);
        ChromData.Read(channel, Header.byteOrder);
        // parse index tree (raw data)
        channel.position(Header.IndexOffset);
        Index.Read(channel, Header.byteOrder);
        // parse zoom level indices
        for (int i = 0; i < Header.ZoomLevels; i++) {
            channel.position(Header.ZoomHeaders[i].IndexOffset);
            IndexZoom[i] = new RTree();
            IndexZoom[i].Read(channel, Header.byteOrder);
        }
        // convert BData to Genome
        int n = ChromData.Keys.size();
        String[] seqnames  = new String[n];
        int   [] seqlength = new int[n];
        for (int i = 0; i < n; i++) {
            // parse sequence name
            seqnames[i] = new String(ChromData.Keys.get(i));
            seqnames[i] = seqnames[i].trim();
            // parse sequence length
            ByteBuffer buffer = ByteBuffer.wrap(ChromData.Values.get(i), 0,
                                                ChromData.Values.get(i).length);
            buffer.order(Header.byteOrder);
            seqlength[i] = buffer.getInt();
        }
        genome = new Genome(seqnames, seqlength);
    }

    public BbiFileIterator Query(String seqname, int from, int to, int binsize) throws IOException {
        int idx = genome.GetIdx(seqname);
        if (idx == -1) {
            throw new IOException("sequence not found");
        }
        return new BbiFileIterator(this, idx, from, to, binsize);
    }
}
