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

public class BbiSummaryRecord extends BbiSummaryStatistics {

    public long ChromId;
    public long From;
    public long To;

    /**
     * Getter for From attribute, performs overflow checks if form value exceeds Integer range
     * @return From
     */
    public int getFrom() {
        if(From > Integer.MAX_VALUE) throw new AssertionError();
        if(From < Integer.MIN_VALUE) throw new AssertionError();
        return (int) From;
    }

    /**
     * Getter for To attribute, performs overflow checks if form value exceeds Integer range
     * @return To
     */
    public int getTo() {
        if(To > Integer.MAX_VALUE) throw new AssertionError();
        if(To < Integer.MIN_VALUE) throw new AssertionError();
        return (int) To;
    }

}
