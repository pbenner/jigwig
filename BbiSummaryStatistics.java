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

import java.lang.Math;

/* -------------------------------------------------------------------------- */

class BbiSummaryStatistics {
    long   Valid;
    double Min;
    double Max;
    double Sum;
    double SumSquares;

    BbiSummaryStatistics() {
        reset();
    }

    void reset() {
        Valid      = 0;
        Min        =  Double.POSITIVE_INFINITY;
        Max        = -Double.POSITIVE_INFINITY;
        Sum        = 0.0;
        SumSquares = 0.0;
    }

    void addRecord(BbiSummaryStatistics x) {
        Valid      += x.Valid;
        Min         = Math.min(Min, x.Min);
        Max         = Math.max(Max, x.Max);
        Sum        += x.Sum;
        SumSquares += x.SumSquares;
    }

    void addValue(double x) {
        Valid      += 1;
        Min         = Math.min(Min, x);
        Max         = Math.max(Max, x);
        Sum        += x;
        SumSquares += x*x;
    }

}
