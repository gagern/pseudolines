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

import java.awt.geom.Point2D;

class RegularBezierLayout extends BezierLayout {

    LinVec2 direction(Intersection from, HalfEdge he) {
        return from.dir.rot(he.index, from.size());
    }

    LinVec2 direction(RimPoint from, HalfEdge he) {
        PointOnLine to = he.connection.center;
        /* a is the angle of the control point direction, relative to
         * the tangent to the circle.
         */
        double a = Math.PI*2.*he.index/from.size();
        double s = Math.sin(a), c = Math.cos(a);
        /* At the rim point, there is a local coordinate system with
         * the counterclockwise tangent unit vector (-y, x) as first
         * unit vector, and the vector (-x, -y) towards the center as
         * the second unit vector. It is with respect to that
         * coordinate system that we interpret the angle above to get
         * our absolute direction vector for the control point.
         */
        double x = -c*from.yPos -s*from.xPos;
        double y =  c*from.xPos -s*from.yPos;
        if (to instanceof RimPoint) {
            // avoid infinite recursion, as we otherwise call control
            Point2D dir;
            dir = rimDirection(from, (RimPoint)to);
            return new LinVec2(LinComb.constant(dir.getX()),
                               LinComb.constant(dir.getY()));
        }
        LinVec2 cp = control(he.connection).sub(from.getLocation());
        LinComb len = cp.getXTerm().mul(x);
        len = len.add(cp.getYTerm().mul(y));
        /* Now len is the scalar product, i.e. the distance of the
         * next control point, projected onto the control direction
         * vector. We want the center between that projection and the
         * rim.
         */
        len = len.div(2);
        return new LinVec2(len.mul(x), len.mul(y));
    }

    void addEquations(Intersection pt) {
        int n = pt.size();
        LinVec2 eqPos = pt.pos.scale(-n);
        LinVec2 eqDir = pt.dir.scale(-2*n);
        for (HalfEdge he: pt) {
            LinVec2 nc = control(he.connection);
            eqPos = eqPos.add(nc);
            eqDir = eqDir.add(nc.sub(pt.pos).rot(-he.index, n));
        }
        ls.eqZero(eqPos);
        ls.eqZero(eqDir);
    }

}
