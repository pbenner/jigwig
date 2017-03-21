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

/* -------------------------------------------------------------------------- */

class BbiRawBlockDecoder implements BbiBlockDecoder {

    BbiDataHeader Header;
    ByteBuffer    Buffer;

    public BbiRawBlockDecoder(ByteBuffer buffer) throws IOException {

        // read the first 24 bytes and store it in
        // a the header object
        Header = new BbiDataHeader(buffer);
        Buffer = buffer;

        switch (Header.Type) {
        default:
            throw new IOException("unsupported block type");
        case 1:
            if (Buffer.remaining() % 12 != 0) {
                throw new IOException("bedGraph data block has invalid length");
            }
        case 2:
            if (Buffer.remaining() % 8 != 0) {
                throw new IOException("variable step data block has invalid length");
            }
        case 3:
            if (Buffer.remaining() % 4 != 0) {
                throw new IOException("fixed step data block has invalid length");
            }
        }
    }

    public BbiBlockDecoderIterator Decode() {
        BbiRawBlockDecoderIterator it = new BbiRawBlockDecoderIterator(this);
        it.Next();
        return it;
    }

    void readFixed(BbiBlockDecoderType r, long i) {
        r.ChromId    = Header.ChromId;
        r.From       = Header.Start + i*Header.Step;
        r.To         = r.From + Header.Span;
        r.Valid      = Header.Span;
        r.Sum        = r.Valid*Buffer.getFloat();
        r.SumSquares = r.Sum*r.Sum;
        r.Min        = r.Sum;
        r.Max        = r.Sum;
    }
    void readVariable(BbiBlockDecoderType r) {
        r.ChromId    = Header.ChromId;
        r.From       = unsigned.getInt(Buffer);
        r.To         = r.From + Header.Span;
        r.Valid      = Header.Span;
        r.Sum        = r.Valid*Buffer.getFloat();
        r.SumSquares = r.Sum*r.Sum;
        r.Min        = r.Sum;
        r.Max        = r.Sum;
    }
    void readBedGraph(BbiBlockDecoderType r) {
        r.ChromId    = Header.ChromId;
        r.From       = unsigned.getInt(Buffer);
        r.To         = unsigned.getInt(Buffer);
        r.Valid      = r.To-r.From;
        r.Sum        = r.Valid*Buffer.getFloat();
        r.SumSquares = r.Sum*r.Sum;
        r.Min        = r.Sum;
        r.Max        = r.Sum;
    }
}
