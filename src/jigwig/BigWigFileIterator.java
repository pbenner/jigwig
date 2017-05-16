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
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.lang.UnsupportedOperationException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* -------------------------------------------------------------------------- */

public class BigWigFileIterator implements Iterator<BigWigFileIteratorType> {

    BigWigFile file;
    Queue<Integer> chromIds;
    int from;
    int to;
    int binsize;

    BbiFileIterator it;
    BbiFileIterator it_next;

    BigWigFileIterator(BigWigFile file, String chromRegex, int from, int to, int binsize) throws IOException {
        this.file     = file;
        this.from     = from;
        this.to       = to;
        this.binsize  = binsize;
        this.chromIds = new LinkedList<Integer>();

        Pattern p = Pattern.compile("^"+chromRegex+"$");

        // generate a list of chromIds that match the
        // regular expression
        for (int i = 0; i < this.file.genome.Seqnames.length; i++) {
            Matcher m = p.matcher(file.genome.Seqnames[i]);
            if (m.matches()) {
                chromIds.add(i);
            }
        }
        // initialize it_next
        next_();
    }

    public boolean hasNext() {
        return (this.it != null && this.it.hasNext()) || it_next != null;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    public BigWigFileIteratorType next() throws NoSuchElementException {
        try {
            return new BigWigFileIteratorType(next_());
        }
        catch (IOException e) {
            it_next = null;
            return new BigWigFileIteratorType(e);
        }
    }

    void nextIterator() throws NoSuchElementException, IOException {
        this.it = this.it_next;
        if (!this.chromIds.isEmpty()) {
            this.it_next = this.file.query(this.chromIds.remove(), this.from, this.to, this.binsize);
        }
    }

    BbiFileIteratorType next_() throws NoSuchElementException, IOException {
        if (this.it == null || !this.it.hasNext()) {
            nextIterator();
        }
        return this.it.next();
    }
}
