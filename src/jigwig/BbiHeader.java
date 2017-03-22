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

class BbiHeader {

    long Magic;
    int  Version;
    int  ZoomLevels;
    long CtOffset;
    long DataOffset;
    long IndexOffset;
    int  FieldCount;
    int  DefinedFieldCount;
    long SqlOffset;
    long SummaryOffset;
    long UncompressBufSize;
    long ExtensionOffset;
    long NBasesCovered;
    long MinVal;
    long MaxVal;
    long SumData;
    long SumSquared;
    BbiHeaderZoom[] ZoomHeaders;
    long NBlocks;
    // offset positions
    long PtrCtOffset;
    long PtrDataOffset;
    long PtrIndexOffset;
    long PtrSqlOffset;
    long PtrSummaryOffset;
    long PtrUncompressBufSize;
    long PtrExtensionOffset;

    ByteOrder byteOrder;

    boolean readMagic(SeekableByteChannel channel, long magic, ByteOrder byteOrder) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(32/8);
        buffer.order(byteOrder);
        channel.read(buffer);
        buffer.rewind();
        Magic = Unsigned.getInt(buffer);
        return Magic == magic;
    }

    void Read(SeekableByteChannel channel, long magic) throws IOException {
        long position = channel.position();

        // check magic number and determine byte order,
        // test little endian first
        if (readMagic(channel, magic, ByteOrder.LITTLE_ENDIAN)) {
            byteOrder = ByteOrder.LITTLE_ENDIAN;
        } else {
            // try again with big endian
            channel.position(position);
            if (readMagic(channel, magic, ByteOrder.BIG_ENDIAN)) {
                byteOrder = ByteOrder.BIG_ENDIAN;
            } else {
                throw new IOException("invalid magic number");
            }
        }
        // read header
        ByteBuffer buffer = ByteBuffer.allocate(4*16/8 + 1*32/8 + 6*64/8);
        buffer.order(byteOrder);
        channel.read(buffer);
        buffer.rewind();

        Version           = Unsigned.getShort(buffer);
        ZoomLevels        = Unsigned.getShort(buffer);
        CtOffset          = Unsigned.getLong (buffer);
        DataOffset        = Unsigned.getLong (buffer);
        IndexOffset       = Unsigned.getLong (buffer);
        FieldCount        = Unsigned.getShort(buffer);
        DefinedFieldCount = Unsigned.getShort(buffer);
        SqlOffset         = Unsigned.getLong (buffer);
        SummaryOffset     = Unsigned.getLong (buffer);
        UncompressBufSize = Unsigned.getInt  (buffer);
        ExtensionOffset   = Unsigned.getLong (buffer);

        ZoomHeaders = new BbiHeaderZoom[ZoomLevels];
        for (int i = 0; i < ZoomLevels; i++) {
            ZoomHeaders[i] = new BbiHeaderZoom();
            ZoomHeaders[i].Read(channel, byteOrder);
        }
        if (SummaryOffset > 0) {
            buffer = ByteBuffer.allocate(4*16/8 + 2*32/8 + 6*64/8);
            buffer.order(byteOrder);
            channel.position(SummaryOffset);
            channel.read(buffer);
            buffer.rewind();
            NBasesCovered     = Unsigned.getLong(buffer);
            MinVal            = Unsigned.getLong(buffer);
            MaxVal            = Unsigned.getLong(buffer);
            SumData           = Unsigned.getLong(buffer);
            SumSquared        = Unsigned.getLong(buffer);
        }
        // set pointers
        PtrCtOffset          = position + 1*64/8;
        PtrDataOffset        = position + 2*64/8;
        PtrIndexOffset       = position + 3*64/8;
        PtrSqlOffset         = position + 4*64/8 + 2*16/8;
        PtrSummaryOffset     = position + 5*64/8 + 2*16/8;
        PtrUncompressBufSize = position + 6*64/8 + 2*16/8;
        PtrExtensionOffset   = position + 6*64/8 + 4*16/8;
    }

}
