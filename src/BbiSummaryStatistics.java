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

public class BbiSummaryStatistics {
    public double Valid;
    public double Min;
    public double Max;
    public double Sum;
    public double SumSquares;

    BbiSummaryStatistics() {
        Reset();
    }

    void Reset() {
        Valid      = 0.0;
        Min        =  Double.POSITIVE_INFINITY;
        Max        = -Double.POSITIVE_INFINITY;
        Sum        = 0.0;
        SumSquares = 0.0;
    }

    void AddRecord(BbiSummaryStatistics x) {
        Valid      += x.Valid;
        Min         = Math.min(Min, x.Min);
        Max         = Math.max(Max, x.Max);
        Sum        += x.Sum;
        SumSquares += x.SumSquares;
    }

}
