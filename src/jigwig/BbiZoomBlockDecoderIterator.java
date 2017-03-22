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

/* -------------------------------------------------------------------------- */

class BbiZoomBlockDecoderIterator implements BbiBlockDecoderIterator {

    BbiZoomBlockDecoder decoder;
    BbiBlockDecoderType result;
    BbiZoomRecord t;
    boolean ok; // if call to Next() was successful

    public BbiZoomBlockDecoderIterator(BbiZoomBlockDecoder decoder) {
        this.decoder = decoder;
        this.result  = new BbiBlockDecoderType();
        this.t       = new BbiZoomRecord();
        this.ok      = true;
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
            t.Read(decoder.Buffer);
            result.ChromId    = t.ChromId;
            result.From       = t.Start;
            result.To         = t.End;
            result.Valid      = t.Valid;
            result.Min        = t.Min;
            result.Max        = t.Max;
            result.Sum        = t.Sum;
            result.SumSquares = t.SumSquares;
        }
    }
}
