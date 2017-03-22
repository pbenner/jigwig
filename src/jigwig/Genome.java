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

import java.lang.String;

/* -------------------------------------------------------------------------- */

class Genome {

    String[] Seqnames;
    int   [] Lengths;

    Genome(String[] seqnames, int[] lengths) {
        Seqnames = seqnames;
        Lengths  = lengths;
    }

    int GetIdx(String seqname) {
        int idx = -1;
        for (int i = 0; i < Seqnames.length; i++) {
            if (seqname.compareTo(Seqnames[i]) == 0) {
                idx = i;
                break;
            }
        }
        return idx;
    }
}
