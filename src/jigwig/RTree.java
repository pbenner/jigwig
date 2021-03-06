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
import java.nio.ByteOrder;
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

    void Read(SeekableByteChannel channel, ByteOrder byteOrder) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(8*32/8 + 2*64/8);
        buffer.order(byteOrder);
        // read header
        channel.read(buffer);
        buffer.rewind();

        Magic         = Unsigned.getInt (buffer);
        // check magic number
        if (Magic != MAGIC) {
            throw new IOException("RTree has invalid magic number");
        }
        BlockSize     = Unsigned.getInt (buffer);
        NItems        = Unsigned.getLong(buffer);
        ChrIdxStart   = Unsigned.getInt (buffer);
        BaseStart     = Unsigned.getInt (buffer);
        ChrIdxEnd     = Unsigned.getInt (buffer);
        BaseEnd       = Unsigned.getInt (buffer);
        IdxSize       = Unsigned.getLong(buffer);
        NItemsPerSlot = Unsigned.getInt (buffer);
        // padding
        buffer.getInt();
        // parse tree
        Root = new RVertex();
        Root.Read(channel, byteOrder);
    }
}
