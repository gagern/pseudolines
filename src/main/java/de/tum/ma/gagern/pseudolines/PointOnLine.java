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

import java.util.AbstractSequentialList;
import java.util.ConcurrentModificationException;
import java.util.ListIterator;
import java.util.NoSuchElementException;

abstract class PointOnLine extends AbstractSequentialList<HalfEdge> {

    /**
     * Some arbitrary identifier for this point. Unique within a given
     * arrangement. Can be used to choose normal forms.
     */
    int id;

    /**
     * Pointer into the cyclic linked list of half segments.  Forward
     * iteration becomes a bit easier if this is interpreted as the
     * last element of the list, instead of the more obvious choice of
     * first element. As the list is cyclic in any case, it should not
     * really matter except for iteration order.
     */
    private HalfEdge last;

    /**
     * The number of pseudo lines at this point. This is half the
     * number of half segments, as each line has two half segments
     * centered here, one incoming and one outgoing.
     */
    int numLines;

    abstract LinVec2 getLocation();

    double xPos;

    double yPos;

    public HalfEdge add(PseudoLine line) {
        if (last != null)
            return addAfter(line, last.opposite);
        HalfEdge in = new HalfEdge(), out = new HalfEdge();
        in.center = out.center = this;
        in.pseudoLine = out.pseudoLine = line;
        in.index = 0;
        out.index = 1;
        in.opposite = in.prev = in.next = out;
        out.opposite = out.prev = out.next = in;
        last = out;
        assert numLines == 0;
        numLines = 1;
        assert assertSanePair(in);
        return in;
    }

    private HalfEdge addAfter(PseudoLine line, HalfEdge pos) {
        HalfEdge in = new HalfEdge(), out = new HalfEdge();
        in.center = out.center = this;
        in.pseudoLine = out.pseudoLine = line;
        in.opposite = out;
        out.opposite = in;
        in.prev = pos;
        in.next = in.prev.next;
        in.prev.next = in.next.prev = in;
        out.prev = pos.opposite;
        out.next = out.prev.next;
        out.prev.next = out.next.prev = out;
        if (last == out.prev)
            last = out; // rather add to end not beginning
        ++numLines;
        reindex();
        assert assertSanePair(in);
        return in;
    }

    void reindex() {
        int i = 0;
        for (HalfEdge he: this)
            he.index = i++;
        ++modCount;
    }

    HalfEdge firstEdge() {
        return last.next;
    }

    HalfEdge incoming(PseudoLine line) {
        for (HalfEdge he: this)
            if (he.pseudoLine == line)
                return he;
        throw new NoSuchElementException();
    }

    HalfEdge outgoing(PseudoLine line) {
        return incoming(line).opposite;
    }

    boolean assertSanePair(HalfEdge in) {
        HalfEdge out = in.opposite;
        assert in.assertInvariants();
        assert out.assertInvariants();
        assert in.index + numLines == out.index;
        return true;
    }

    //////////////////////////////////////////////////////////////////////
    // List interface, AbstractSequentialList implementation

    public int size() {
        return 2*numLines;
    }

    public ListIterator<HalfEdge> listIterator(int index) {
        Iter i = new Iter();
        while (index != 0) {
            i.next();
            --index;
        }
        return i;
    }

    class Iter implements ListIterator<HalfEdge> {

        HalfEdge cur;

        int remaining;

        int mc;

        Iter() {
            cur = last;
            remaining = size();
            mc = modCount;
        }

        public boolean hasNext() {
            return remaining != 0;
        }

        public HalfEdge next() {
            checkMod();
            if (!hasNext())
                throw new NoSuchElementException();
            assert cur.next.prev == cur;
            cur = cur.next;
            --remaining;
            return cur;
        }

        public int nextIndex() {
            return size() - remaining;
        }

        public boolean hasPrevious() {
            return cur != last || (remaining == 0 && cur != null);
        }

        public HalfEdge previous() {
            checkMod();
            if (!hasPrevious())
                throw new NoSuchElementException();
            assert cur.prev.next == cur;
            cur = cur.prev;
            ++remaining;
            return cur.next;
        }

        public int previousIndex() {
            return size() - remaining - 1;
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

        private void checkMod() {
            if (mc != modCount)
                throw new ConcurrentModificationException();
        }

    }

}
