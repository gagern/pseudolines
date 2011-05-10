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

import java.awt.Shape;
import java.util.AbstractSequentialList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

class Cell {

    HalfEdge start;

    private int size;

    Cell(HalfEdge start) {
        HalfEdge repr = start;
        size = 0;
        HalfEdge he = start;
        do {
            he = he.connection.prev;
            while (skip(he))
                he = he.prev;
            if (repr.center.id > he.center.id)
                repr = he; // choose normalized starting edge
            ++size;
        } while (he != start);
        this.start = repr;
    }

    boolean skip(HalfEdge he) {
        return he.connection == null
            || he.connection.center instanceof OpenEndPoint
            ;
    }

    boolean isAtRim() {
        for (PointOnLine p: this.corners())
            if (p instanceof RimPoint)
                return true;
        return false;
    }

    public int size() {
        return size;
    }

    @Override public int hashCode() {
        return start.hashCode();
    }

    @Override public boolean equals(Object o) {
        return o instanceof Cell && start.equals(((Cell)o).start);
    }

    List<HalfEdge> edges() {
        return new Edges();
    }

    List<PointOnLine> corners() {
        return new Corners(new Edges());
    }

    static boolean isTriangle(HalfEdge a) {
        // Edges of the triangle are ab, cd and ef, in ccw order
        HalfEdge b = a.connection;
        if (b == null)
            return false;
        // HalfEdge c = b.prev;
        HalfEdge d = b.prev.connection; // = c.connection
        // HalfEdge f = a.next;
        HalfEdge e = a.next.connection; // = f.connection
        if (e == null || e.next != d)
            return false;
        assert d.center == e.center;
        return true;
    }

    //////////////////////////////////////////////////////////////////////
    // List interface, AbstractSequentialList implementation

    class Corners extends TransformingSequentialList<HalfEdge, PointOnLine> {

        Corners(List<? extends HalfEdge> delegate) {
            super(delegate);
        }

        protected PointOnLine transform(HalfEdge he) {
            return he.center;
        }

    }

    class Edges extends AbstractSequentialList<HalfEdge> {

        public int size() {
            return size;
        }
    
        public ListIterator<HalfEdge> listIterator(int index) {
            EdgeIter i = new EdgeIter();
            while (index != 0) {
                i.next();
                --index;
            }
            return i;
        }

    }

    class EdgeIter implements ListIterator<HalfEdge> {

        HalfEdge cur;

        int remaining;

        EdgeIter() {
            cur = start;
            remaining = size;
        }

        public boolean hasNext() {
            return remaining != 0;
        }

        public HalfEdge next() {
            if (!hasNext())
                throw new NoSuchElementException();
            cur = cur.connection.prev;
            while (skip(cur))
                cur = cur.prev;
            --remaining;
            return cur;
        }

        public int nextIndex() {
            return size - remaining;
        }

        public boolean hasPrevious() {
            return cur != start || remaining == 0;
        }

        public HalfEdge previous() {
            if (!hasPrevious())
                throw new NoSuchElementException();
            HalfEdge prev = cur;
            cur = cur.next;
            while (skip(cur))
                cur = cur.next;
            cur = cur.connection;
            ++remaining;
            return prev;
        }

        public int previousIndex() {
            return size - remaining - 1;
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
