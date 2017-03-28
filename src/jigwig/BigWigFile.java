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
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;

/* -------------------------------------------------------------------------- */

public class BigWigFile extends BbiFile {

    static final long MAGIC = 0x888FFC26L;

    Genome genome;

    public BigWigFile(SeekableByteChannel channel) throws IOException {
        super(channel, MAGIC);
        // convert BData to Genome
        int n = ChromData.Keys.size();
        String[] seqnames  = new String[n];
        int   [] seqlength = new int[n];
        for (int i = 0; i < n; i++) {
            // parse index
            ByteBuffer buffer = ByteBuffer.wrap(ChromData.Values.get(i), 0,
                                                ChromData.Values.get(i).length);
            buffer.order(Header.byteOrder);
            int idx = (int)Unsigned.getInt(buffer);
            // parse sequence length
            seqlength[idx] = (int)Unsigned.getInt(buffer);
            // parse sequence name
            seqnames[idx] = new String(ChromData.Keys.get(i));
            seqnames[idx] = seqnames[idx].trim();
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
