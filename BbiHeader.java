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

    public void Read(SeekableByteChannel channel) throws IOException {
        // set pointers first
        PtrCtOffset          = channel.position() + 1*64/8;
        PtrDataOffset        = channel.position() + 2*64/8;
        PtrIndexOffset       = channel.position() + 3*64/8;
        PtrSqlOffset         = channel.position() + 4*64/8 + 2*16/8;
        PtrSummaryOffset     = channel.position() + 5*64/8 + 2*16/8;
        PtrUncompressBufSize = channel.position() + 6*64/8 + 2*16/8;
        PtrExtensionOffset   = channel.position() + 6*64/8 + 4*16/8;

        ByteBuffer buffer = ByteBuffer.allocate(4*16/8 + 2*32/8 + 6*64/8);
        channel.read(buffer);
        buffer.rewind();

        Magic             = unsigned.getInt  (buffer);
        Version           = unsigned.getShort(buffer);
        ZoomLevels        = unsigned.getShort(buffer);
        CtOffset          = unsigned.getLong (buffer);
        DataOffset        = unsigned.getLong (buffer);
        IndexOffset       = unsigned.getLong (buffer);
        FieldCount        = unsigned.getShort(buffer);
        DefinedFieldCount = unsigned.getShort(buffer);
        SqlOffset         = unsigned.getLong (buffer);
        SummaryOffset     = unsigned.getLong (buffer);
        UncompressBufSize = unsigned.getInt  (buffer);
        ExtensionOffset   = unsigned.getLong (buffer);

        ZoomHeaders = new BbiHeaderZoom[ZoomLevels];
        for (int i = 0; i < ZoomLevels; i++) {
            ZoomHeaders[i] = new BbiHeaderZoom();
            ZoomHeaders[i].Read(channel);
        }

        if (SummaryOffset > 0) {
            buffer = ByteBuffer.allocate(4*16/8 + 2*32/8 + 6*64/8);
            channel.position(SummaryOffset);
            channel.read(buffer);
            buffer.rewind();
            NBasesCovered     = unsigned.getLong(buffer);
            MinVal            = unsigned.getLong(buffer);
            MaxVal            = unsigned.getLong(buffer);
            SumData           = unsigned.getLong(buffer);
            SumSquared        = unsigned.getLong(buffer);
        }
    }

}
