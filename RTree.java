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

class RTree {
    static final int MAGIC = 0x2468ace0;

    long Magic;
    long BlockSize;
    long NItems;
    long ChrIdxStart;
    long BaseStart;
    long ChrIdxEnd;
    long BaseEnd;
    long IdxSize;
    long NItemsPerSlot;
    long PtrIdxSize;
    RVertex Root;

    RTree() {
        BlockSize     = 256;
        NItemsPerSlot = 1024;
    }

    void Read(SeekableByteChannel channel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(8*32 + 2*64);
        // read header
        channel.read(buffer);

        Magic         = unsigned.getInt (buffer);
        BlockSize     = unsigned.getInt (buffer);
        NItems        = unsigned.getLong(buffer);
        ChrIdxStart   = unsigned.getInt (buffer);
        BaseStart     = unsigned.getInt (buffer);
        ChrIdxEnd     = unsigned.getInt (buffer);
        BaseEnd       = unsigned.getInt (buffer);
        IdxSize       = unsigned.getLong(buffer);
        NItemsPerSlot = unsigned.getInt (buffer);
        // padding
        buffer.getInt();
        // check magic number
        if (Magic != MAGIC) {
            throw new IOException("RTree has invalid magic numner");
        }
        // parse tree
        Root = new RVertex();
        Root.Read(channel);
    }
}
