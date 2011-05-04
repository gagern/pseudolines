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

abstract class PointOnLine {

    int id;

    private PointOnLine[] neighbours;

    PointOnLine(int numPseudolines) {
        neighbours = new PointOnLine[numPseudolines*2];
    }

    abstract LinVec2 getLocation();

    int neighbourIndex(PointOnLine pt) {
        for (int i = 0; i < neighbours.length; ++i)
            if (neighbours[i] == pt)
                return i;
        throw new IllegalArgumentException("Argument is not a neighbour");
    }

    int numNeighbours() {
        return neighbours.length;
    }

    PointOnLine neighbour(int index) {
        return neighbours[index];
    }

    boolean hasNeighbour(PointOnLine pt) {
        for (int i = 0; i < neighbours.length; ++i)
            if (neighbours[i] == pt)
                return true;
        return false;
    }

    PointOnLine relativeNeighbour(PointOnLine reference, int offset) {
        int idx = neighbourIndex(reference) + offset + neighbours.length;
        return neighbours[idx%neighbours.length];
    }

    PointOnLine opposite(PointOnLine incoming) {
        return relativeNeighbour(incoming, neighbours.length/2);
    }

    void addCrossing(PointOnLine prev, PseudoLine line, PointOnLine next) {
        if (prev == null)
            throw new NullPointerException("prev must not be null");
        int idx = neighbourIndex(null);
        assert idx < neighbours.length/2;
        assert neighbours[idx + neighbours.length/2] == null;
        neighbours[idx] = prev;
        neighbours[idx + neighbours.length/2] = next;
    }

    void replace(PointOnLine search, PointOnLine replace) {
        neighbours[neighbourIndex(search)] = replace;
    }

    static void swap(PointOnLine b, PointOnLine c) {
        PointOnLine[] nb = b.neighbours, nc = c.neighbours;
        int lb = nb.length, lc = nc.length;
        int ib = b.neighbourIndex(c), ic = c.neighbourIndex(b);
        int jb = (ib + lb/2)%lb, jc = (ic + lc/2)%lc;
        
        // old: a - b - c - d
        // new: a - c - b - d
        PointOnLine a = nb[jb], d = nc[jc];
        if (a == null || d == null)
            throw new IllegalArgumentException("Cannot swap end points");
        a.replace(b,c);
        nc[ic] = a;
        nc[jc] = b;
        nb[jb] = c;
        nb[ib] = d;
        d.replace(c, b);
    }

}
