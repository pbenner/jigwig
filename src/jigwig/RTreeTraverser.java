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

import java.util.Stack;

/* -------------------------------------------------------------------------- */


class RTreeTraverser {

    int  chromId;
    long from;
    long to;
    Stack<RTreeTraverserType> stack;
    RTreeTraverserType r;

    RTreeTraverser(RTree tree, int chromId, long from, long to) {
        this.chromId = chromId;
        this.from    = from;
        this.to      = to;
        this.r       = new RTreeTraverserType();
        this.stack   = new Stack<RTreeTraverserType>();
        this.stack.push(new RTreeTraverserType(tree.Root, 0));
        this.Next();
    }

    RTreeTraverserType Get() {
        return r;
    }
    boolean Ok() {
        return !stack.empty();
    }
    void Next() {
        RTreeTraverserType t;
        // reset result
        r.Vertex = null;
        r.Idx    = 0;
        // loop over stack until either it is empty or a new
        // position is found
        L1: while (stack.size() > 0) {
            t = stack.pop();
            L2: for (int i = t.Idx; i < t.Vertex.NChildren; i++) {
                // indices are sorted, hence stop searching if idx is larger than the
                // curent index end
                if (t.Vertex.ChrIdxStart[i] > chromId) {
                    continue L1;
                }
                if (t.Vertex.ChrIdxEnd[i] < chromId) {
                    continue L2;
                }
                // check if this is the correct chromosome
                if (chromId >= t.Vertex.ChrIdxStart[i] && chromId <= t.Vertex.ChrIdxEnd[i]) {
                    if (t.Vertex.ChrIdxStart[i] == t.Vertex.ChrIdxEnd[i]) {
                        // check region on chromosome
                        if (t.Vertex.BaseEnd[i] <= from) {
                            // query region is still ahead
                            continue L2;
                        }
                        if (t.Vertex.BaseStart[i] >= to) {
                            // already past the query region
                            continue L1;
                        }
                    }
                }
                // Push position incremented by one leaf
                stack.push(new RTreeTraverserType(t.Vertex, i+1));
                // found a match
                if (t.Vertex.IsLeaf == 0) {
                    // push child
                    stack.push(new RTreeTraverserType(t.Vertex.Children[i], 0));
                    // continue with processing the child
                    continue L1;
                } else {
                    // save result and exit
                    r.Vertex = t.Vertex;
                    r.Idx    = i;
                    return;
                }
            }
        }
    }
}
