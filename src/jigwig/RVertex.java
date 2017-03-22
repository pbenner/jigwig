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

import java.util.zip.Inflater;
import java.util.zip.DataFormatException;

/* -------------------------------------------------------------------------- */

class RVertex {
    byte   IsLeaf;
    int    NChildren;
    long[] ChrIdxStart;
    long[] BaseStart;
    long[] ChrIdxEnd;
    long[] BaseEnd;
    long[] DataOffset;
    long[] Sizes;
    // positions of DataOffset and Sizes values in file
    long[] PtrDataOffset;
    long[] PtrSizes;

    RVertex[] Children;

    ByteBuffer ReadBlock(SeekableByteChannel channel, int i, byte[] uncompressBuf) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate((int)Sizes[i]);
        if (channel.read(buffer) != Sizes[i]) {
            throw new IOException("unexpected end of file");
        }
        if (uncompressBuf.length > 0) {
            try {
                int length;
                Inflater decompresser = new Inflater();
                decompresser.setInput(buffer.array(), 0, (int)Sizes[i]);
                length = decompresser.inflate(uncompressBuf);
                decompresser.end();
                return ByteBuffer.wrap(uncompressBuf, 0, length);
            }
            catch (DataFormatException e) {
                throw new IOException("block uncompression failed");
            }
        } else {
            buffer.rewind();
            return buffer;
        }
    }

    void Read(SeekableByteChannel channel, ByteOrder byteOrder) throws IOException {

        ByteBuffer buffer = ByteBuffer.allocate(2*8/8 + 1*16/8);
        buffer.order(byteOrder);
        // read header
        channel.read(buffer);
        buffer.rewind();

        IsLeaf = buffer.get();
        // padding
        buffer.get();
        NChildren = unsigned.getShort(buffer);
        // allocate memory
        ChrIdxStart   = new long[NChildren];
        BaseStart     = new long[NChildren];
        ChrIdxEnd     = new long[NChildren];
        BaseEnd       = new long[NChildren];
        DataOffset    = new long[NChildren];
        PtrDataOffset = new long[NChildren];
        if (IsLeaf != 0) {
            Sizes    = new long[NChildren];
            PtrSizes = new long[NChildren];
            // get new buffer
            buffer = ByteBuffer.allocate(4*32/8 + 2*64/8);
            buffer.order(byteOrder);
        }
        else {
            Children = new RVertex[NChildren];
            // get new buffer
            buffer = ByteBuffer.allocate(4*32/8 + 1*64/8);
            buffer.order(byteOrder);
        }
        for (int i = 0; i < NChildren; i++) {
            PtrDataOffset[i] = channel.position() + 4*32/8;
            if (IsLeaf != 0) {
                PtrSizes[i]  = channel.position() + 4*32/8 + 64/8;
            }
            buffer.rewind(); channel.read(buffer);
            buffer.rewind();
            ChrIdxStart[i] = unsigned.getInt (buffer);
            BaseStart  [i] = unsigned.getInt (buffer);
            ChrIdxEnd  [i] = unsigned.getInt (buffer);
            BaseEnd    [i] = unsigned.getInt (buffer);
            DataOffset [i] = unsigned.getLong(buffer);
            if (IsLeaf != 0) {
                Sizes  [i] = unsigned.getLong(buffer);
            }
        }
        if (IsLeaf == 0) {
            for (int i = 0; i < NChildren; i++) {
                channel.position(DataOffset[i]);
                Children[i] = new RVertex();
                Children[i].Read(channel, byteOrder);
            }
        }
    }
}
