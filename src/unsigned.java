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

class unsigned {
    static short getByte(ByteBuffer buffer) {
        return ((short)(buffer.get() & 0xff));
    }
    static int getShort(ByteBuffer buffer) {
        return (buffer.getShort() & 0xffff);
    }
    static long getInt(ByteBuffer buffer) {
        return ((long)buffer.getInt() & 0xffffffffL);
    }
    static long getLong(ByteBuffer buffer) throws IOException {
        long r = buffer.getLong();
        if (r < 0) {
            throw new IOException("integer overflow");
        }
        return r;
    }
}
