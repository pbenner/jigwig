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

        Magic         = buffer.getInt () & 0xFFFFFFFFL;
        BlockSize     = buffer.getInt () & 0xFFFFFFFFL;
        NItems        = buffer.getLong() & 0xFFFFFFFFL;
        ChrIdxStart   = buffer.getInt () & 0xFFFFFFFFL;
        BaseStart     = buffer.getInt () & 0xFFFFFFFFL;
        ChrIdxEnd     = buffer.getInt () & 0xFFFFFFFFL;
        BaseEnd       = buffer.getInt () & 0xFFFFFFFFL;
        IdxSize       = buffer.getLong() & 0xFFFFFFFFL;
        NItemsPerSlot = buffer.getInt () & 0xFFFFFFFFL;
        // padding
        buffer.getInt();
        // parse tree
        Root = new RVertex();
        Root.Read(channel);
    }
}
