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

import java.nio.ByteBuffer;

/* -------------------------------------------------------------------------- */

class BbiDataHeader {
    long ChromId;
    long Start;
    long End;
    long Step;
    long Span;
    byte Type;
    byte Reserved;
    int  ItemCount;

    BbiDataHeader(ByteBuffer buffer) {
        read(buffer);
    }

    void read(ByteBuffer buffer) {

        ChromId   = unsigned.getInt(buffer);
        Start     = unsigned.getInt(buffer);
        End       = unsigned.getInt(buffer);
        Step      = unsigned.getInt(buffer);
        Span      = unsigned.getInt(buffer);
        Type      = buffer.get();
        Reserved  = buffer.get();
        ItemCount = unsigned.getShort(buffer);

      }

}
