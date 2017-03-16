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

class BbiHeaderZoom {

    long ReductionLevel;
    long Reserved;
    long DataOffset;
    long IndexOffset;
    long NBlocks;
    long PtrDataOffset;
    long PtrIndexOffset;

    public void Read(SeekableByteChannel channel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(32+32+64+64+32+64+64);
        // determine offset positions
        PtrDataOffset  = channel.position() + 1*64;
        PtrIndexOffset = channel.position() + 2*64;
        // read header
        channel.read(buffer);
        ReductionLevel = buffer.getInt () & 0xFFFFFFFFL;
        Reserved       = buffer.getInt () & 0xFFFFFFFFL;
        DataOffset     = buffer.getLong() & 0xFFFFFFFFL;
        IndexOffset    = buffer.getLong() & 0xFFFFFFFFL;
        NBlocks        = buffer.getInt () & 0xFFFFFFFFL;
    }

}
