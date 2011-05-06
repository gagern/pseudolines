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

class BezierLayout extends Layout {

    LinVec2 control(HalfEdge he) {
        return direction(he).add(he.center.getLocation());
    }

    LinVec2 direction(HalfEdge he) {
        PointOnLine from = he.center;
        if (from instanceof Intersection)
            return direction((Intersection)from, he);
        if (from instanceof RimPoint)
            return direction((RimPoint)from, he);
        return LinVec2.ZERO;
    }

    void layoutInner(PseudoLine pl) {
        for (HalfEdge he: pl.allHalfEdges())
            layoutEdge(he);
    }

    void layoutEdge(HalfEdge he) {
        LinVec2 loc = control(he);
        he.xCtrl = loc.getXTerm().getValue();
        he.yCtrl = loc.getYTerm().getValue();
    }

    LinVec2 dirVars(HalfEdge he) {
        if (he.dir == null)
            he.dir = new LinVec2();
        return he.dir;
    }

    LinVec2 direction(Intersection from, HalfEdge he) {
        if (he.index < he.opposite.index)
            return dirVars(he);
        else
            return dirVars(he.opposite).scale(-1);
    }

    LinVec2 direction(RimPoint from, HalfEdge he) {
        return LinVec2.ZERO;
    }

    void addEquations(Intersection pt) {
        int n = pt.size();
        LinVec2 eqPos = pt.pos.scale(-n);
        for (HalfEdge he: pt) {
            LinVec2 nc = control(he.connection);
            eqPos = eqPos.add(nc);
            if (he.index > he.opposite.index)
                continue;
            /* The use of the factor -3 instead of -4 makes the force
             * pulling the control point towards the center only half
             * as strong as that towards the neighbouring control
             * points. This makes the pseudolines turn less suddenly
             * after an intersection.
             */
            LinVec2 eqDir = dirVars(he).scale(-3);
            eqDir = eqDir.add(nc.sub(pt.pos));
            eqDir = eqDir.add(pt.pos.sub(control(he.opposite)));
            ls.eqZero(eqDir);
        }
        ls.eqZero(eqPos);
    }

}
