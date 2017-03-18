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

import java.util.ArrayList;

/* -------------------------------------------------------------------------- */

class BData {
    static final long MAGIC = 0x78ca8c91;

    long Magic;
    long KeySize;
    long ValueSize;
    long ItemsPerBlock;
    long ItemCount;

    ArrayList<byte[]> Keys;
    ArrayList<byte[]> Values;

    void readVertexLeaf(SeekableByteChannel channel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(16/8);
        ByteBuffer bufKey = ByteBuffer.allocate((int)KeySize);
        ByteBuffer bufVal = ByteBuffer.allocate((int)ValueSize);
        channel.read(buffer); buffer.rewind();
        int nVals = unsigned.getShort(buffer);
        for (int i = 0; i < nVals; i++) {
            bufKey.rewind(); channel.read(bufKey); bufKey.rewind();
            bufKey.rewind(); channel.read(bufVal); bufKey.rewind();
            Keys  .add(bufKey.array().clone());
            Values.add(bufKey.array().clone());
        }
    }

    void readVertexIndex(SeekableByteChannel channel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(16/8);
        ByteBuffer bufKey = ByteBuffer.allocate((int)KeySize);
        ByteBuffer bufVal = ByteBuffer.allocate(64/8);
        channel.read(buffer); buffer.rewind();
        int  nVals = unsigned.getShort(buffer);
        long position, currentPosition;
        for (int i = 0; i < nVals; i++) {
            bufKey.rewind(); channel.read(bufKey); bufKey.rewind();
            bufKey.rewind(); channel.read(bufVal); bufKey.rewind();
            position = unsigned.getLong(bufVal);
            // save current position and jump to child vertex
            currentPosition = channel.position();
            channel.position(position);
            readVertex(channel);
            // continue with current vertex
            channel.position(currentPosition);
        }
    }

    void readVertex(SeekableByteChannel channel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(2*32/8);
        channel.read(buffer); buffer.rewind();
        byte isLeaf = buffer.get();
        // padding
        buffer.get();
        if (isLeaf != 0) {
            readVertexLeaf(channel);
        }
        else {
            readVertexIndex(channel);
        }
    }

    void Read(SeekableByteChannel channel) throws IOException {

        ByteBuffer buffer = ByteBuffer.allocate(6*32/8 + 1*64/8);
        // read header
        channel.read(buffer);
        Magic         = unsigned.getInt (buffer);
        ItemsPerBlock = unsigned.getInt (buffer);
        KeySize       = unsigned.getInt (buffer);
        ValueSize     = unsigned.getInt (buffer);
        ItemCount     = unsigned.getLong(buffer);
        // padding
        buffer.getLong();
        // check magic number
        if (Magic != MAGIC) {
            throw new IOException("BData has invalid magic numner");
        }
        Keys   = new ArrayList<byte[]>();
        Values = new ArrayList<byte[]>();
        // start traversing the tree
        readVertex(channel);
    }

}
