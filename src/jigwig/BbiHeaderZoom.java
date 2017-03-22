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

class BbiHeaderZoom {

    long ReductionLevel;
    long Reserved;
    long DataOffset;
    long IndexOffset;
    long PtrDataOffset;
    long PtrIndexOffset;

    void Read(SeekableByteChannel channel, ByteOrder byteOrder) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(2*32/8 + 2*64/8);
        buffer.order(byteOrder);
        // determine offset positions
        PtrDataOffset  = channel.position() + 1*64/8;
        PtrIndexOffset = channel.position() + 2*64/8;
        // read header
        channel.read(buffer);
        buffer.rewind();
        ReductionLevel = Unsigned.getInt (buffer);
        Reserved       = Unsigned.getInt (buffer);
        DataOffset     = Unsigned.getLong(buffer);
        IndexOffset    = Unsigned.getLong(buffer);
    }

}
