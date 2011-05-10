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
import java.awt.geom.Path2D;

class AnimationHalfPlane {

    PseudoLinePath before;

    PseudoLinePath after;

    byte dir;

    Shape interpolate(double p0, double p1, CubicPath cp, Path2D p2d) {
        assert dir == -1 || dir == 1;
        cp.interpolate(p0, before, p1, after);
        if (cp.closed)
            return cp;
        double x1 = cp.firstX(), y1 = cp.firstY();
        double x2 = cp.lastX(), y2 = cp.lastY();
        double dx = x2 - x1, dy = y2 - y1;
        double f = 2./Math.hypot(dx, dy);
        dx *= f;
        dy *= f;
        p2d.reset();
        p2d.append(cp, false);
        p2d.lineTo(x2 + dx, y2 + dy);
        // positive is left of line
        p2d.lineTo(x2 + dx - dir*dy, y2 + dy + dir*dx);
        p2d.lineTo(x1 - dx - dir*dy, y1 - dy + dir*dx);
        p2d.lineTo(x1 - dx, y1 - dy);
        p2d.closePath();
        return p2d;
    }

}
