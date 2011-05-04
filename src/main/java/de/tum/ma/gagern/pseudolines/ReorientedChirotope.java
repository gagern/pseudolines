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

class ReorientedChirotope implements Chirotope {

    final Chirotope delegate;

    final int[] relabeling;

    final int[] reorientation;

    public ReorientedChirotope(Chirotope delegate,
                               int[] relabeling, int[] reorientation) {
        this.delegate = delegate;
        this.relabeling = relabeling;
        this.reorientation = reorientation;
    }

    public int numElements() {
        return delegate.numElements();
    }

    public int chi(int a, int b, int c) {
        return delegate.chi(relabeling[a], relabeling[b], relabeling[c])
            * reorientation[a] * reorientation[b] * reorientation[c];
    }

    public boolean isUniform() {
        return delegate.isUniform();
    }

}
