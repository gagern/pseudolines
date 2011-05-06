/*
 * pseudolines - Display pyseudoline arrangements
 * Copyright (C) 2011 Martin von Gagern
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

package de.tum.ma.gagern.pseudolines;

import java.awt.Color;
import java.util.AbstractSequentialList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

class PseudoLine {

    HalfEdge start;

    HalfEdge end;

    Color color = Color.BLACK;

    void setEnds(HalfEdge start, HalfEdge end) {
        this.start = start;
        this.end = end;
    }

    List<HalfEdge> allHalfEdges() {
        return new AllHalfEdges();
    }

    class AllHalfEdges extends AbstractSequentialList<HalfEdge> {

        public int size() {
            AllHalfEdgeIter i = new AllHalfEdgeIter();
            int size = 0;
            while (i.hasNext()) {
                i.next();
                ++size;
            }
            return size;
        }

        public ListIterator<HalfEdge> listIterator(int index) {
            AllHalfEdgeIter i = new AllHalfEdgeIter();
            while (index != 0) {
                i.next();
                --index;
            }
            return i;
        }

    }

    class AllHalfEdgeIter implements ListIterator<HalfEdge> {

        HalfEdge cur = start.opposite;

        boolean incompleteSegment = false;

        int pos = 0;

        public boolean hasNext() {
            return cur != end;
        }

        public HalfEdge next() {
            if (!hasNext())
                throw new NoSuchElementException();
            if (incompleteSegment)
                cur = cur.connection;
            else
                cur = cur.opposite;
            incompleteSegment = !incompleteSegment;
            ++pos;
            return cur;
        }

        public int nextIndex() {
            return pos + 1;
        }

        public boolean hasPrevious() {
            return cur != start.opposite;
        }

        public HalfEdge previous() {
            if (!hasPrevious())
                throw new NoSuchElementException();
            HalfEdge prev = cur;
            if (incompleteSegment)
                cur = cur.opposite;
            else
                cur = cur.connection;
            incompleteSegment = !incompleteSegment;
            --pos;
            return prev;
        }

        public int previousIndex() {
            return pos;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public void set(HalfEdge e) {
            throw new UnsupportedOperationException();
        }

        public void add(HalfEdge e) {
            throw new UnsupportedOperationException();
        }

    }

}
