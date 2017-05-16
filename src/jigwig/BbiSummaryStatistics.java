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

import java.lang.Math;

/* -------------------------------------------------------------------------- */

public class BbiSummaryStatistics {
    public double Valid;
    public double Min;
    public double Max;
    public double Sum;
    public double SumSquares;

    BbiSummaryStatistics() {
        reset();
    }
    BbiSummaryStatistics(BbiSummaryStatistics r) {
        this.Valid      = r.Valid;
        this.Min        = r.Min;
        this.Max        = r.Max;
        this.Sum        = r.Sum;
        this.SumSquares = r.SumSquares;
    }

    void reset() {
        Valid      = 0.0;
        Min        =  Double.POSITIVE_INFINITY;
        Max        = -Double.POSITIVE_INFINITY;
        Sum        = 0.0;
        SumSquares = 0.0;
    }

    public double getMax() { return Max; }

    public double getMin() { return Min; }

    public double getSum() { return Sum; }

    public double getMean() { return Sum/Valid; }

    public double getVariance() {
        return SumSquares/Valid - Sum/Valid*Sum/Valid;
    }
}
