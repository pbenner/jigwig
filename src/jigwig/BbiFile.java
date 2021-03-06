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
import java.nio.channels.SeekableByteChannel;

/* -------------------------------------------------------------------------- */

class BbiFile {
    BbiHeader Header;
    BData     ChromData;
    RTree     Index;
    RTree[]   IndexZoom;

    SeekableByteChannel Channel;

    BbiFile(SeekableByteChannel channel, long magic) throws IOException {
        Header = new BbiHeader();
        // parse header
        Header.Read(channel, magic);
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
        Channel = channel;
    }

    BbiFileIterator query(int idx, int from, int to, int binsize) throws IOException {
        return new BbiFileIterator(this, idx, from, to, binsize);
    }

    /**
     * BinSizes for different zoom leves as integer
     * Perform overflow checks, when casting from long to int
     * @return
     */
    public int[] getZoomLevelBinSizes() {

        int[] binSizes = new int[Header.ZoomHeaders.length];

        for(int i = 0; i < Header.ZoomHeaders.length; i++) {

            long bs = Header.ZoomHeaders[i].ReductionLevel;
            if(bs > Integer.MAX_VALUE) throw new AssertionError();
            if(bs < 1) throw new AssertionError();
            binSizes[i] = (int) bs;
        }
        return binSizes;
    }

}
