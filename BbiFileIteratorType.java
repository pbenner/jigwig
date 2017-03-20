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

/* -------------------------------------------------------------------------- */

public class BbiFileIteratorType {
    BbiSummaryRecord summary;
    IOException exception;

    BbiFileIteratorType(BbiSummaryRecord summary) {
        this.summary = summary;
    }
    BbiFileIteratorType(IOException exception) {
        this.exception = exception;
    }

    public BbiSummaryRecord GetSummary() throws IOException {
        if (exception != null) {
            throw exception;
        }
        return summary;
    }
}
