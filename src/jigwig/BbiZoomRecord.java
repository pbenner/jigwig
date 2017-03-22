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

class BbiZoomRecord {
    long ChromId;
    long Start;
    long End;
    long Valid;
    double Min;
    double Max;
    double Sum;
    double SumSquares;

    void Read(ByteBuffer buffer) {
        ChromId    = unsigned.getInt(buffer);
        Start      = unsigned.getInt(buffer);
        End        = unsigned.getInt(buffer);
        Valid      = unsigned.getInt(buffer);
        Min        = buffer.getFloat();
        Max        = buffer.getFloat();
        Sum        = buffer.getFloat();
        SumSquares = buffer.getFloat();
    }
    void AddValue(double x) {
        if (Min > x) {
            Min = x;
        }
        if (Max < x) {
            Max = x;
        }
        Valid      += 1;
        Sum        += x;
        SumSquares += x*x;
    }
}
