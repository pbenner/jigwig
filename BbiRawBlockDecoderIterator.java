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

import java.nio.ByteBuffer;

/* -------------------------------------------------------------------------- */

class BbiRawBlockDecoderIterator implements BbiBlockDecoderIterator {

    BbiRawBlockDecoder decoder;
    BbiBlockDecoderType result;
    boolean ok; // if call to Next() was successful
    long     i; // position for reading fixed step data

    public BbiRawBlockDecoderIterator(BbiRawBlockDecoder decoder) {
        this.decoder = decoder;
        this.result  = new BbiBlockDecoderType();
        this.ok      = true;
        this.i       = 0;
    }

    public BbiBlockDecoderType Get() {
        return result;
    }
    public boolean Ok() {
        return ok;
    }
    public void Next() {
        if (decoder.Buffer.remaining() == 0) {
            ok = false;
        } else {
            switch (decoder.Header.Type) {
            case 1:
                decoder.readBedGraph(result);
                break;
            case 2:
                decoder.readVariable(result);
                break;
            case 3:
                decoder.readFixed(result, i);
                i++;
                break;
            }
        }
    }
    
}
