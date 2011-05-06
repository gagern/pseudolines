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
import java.util.List;
import java.util.ListIterator;

abstract class TransformingSequentialList<From, To>
    extends AbstractSequentialList<To>
{

    private final List<? extends From> delegate;

    public TransformingSequentialList(List<? extends From> delegate) {
        this.delegate = delegate;
    }

    protected abstract To transform(From from);

    public int size() {
        return delegate.size();
    }
    
    public ListIterator<To> listIterator(int index) {
        return new Iter(delegate.listIterator(index));
    }


    class Iter implements ListIterator<To> {

        private final ListIterator<? extends From> iter;

        Iter(ListIterator<? extends From> iter) {
            this.iter = iter;
        }

        public boolean hasNext() {
            return iter.hasNext();
        }

        public To next() {
            return transform(iter.next());
        }

        public int nextIndex() {
            return iter.nextIndex();
        }

        public boolean hasPrevious() {
            return iter.hasPrevious();
        }

        public To previous() {
            return transform(iter.previous());
        }

        public int previousIndex() {
            return iter.previousIndex();
        }

        public void remove() {
            iter.remove();
        }

        public void set(To e) {
            throw new UnsupportedOperationException();
        }

        public void add(To e) {
            throw new UnsupportedOperationException();
        }

    }

}
