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

import java.util.Arrays;

class Spline1DLayout extends SplineLayout {

    double dx, dy;

    void calcParameters() {
        dx = x[n] - x[0];
        dy = y[n] - y[0];
        double h = Math.hypot(dx, dy);
        dx /= h;
        dy /= h;
        for (int i = 0; i <= n; ++i) {
            // Transform such that x is in direction of the pseudoline
            // and y is perpendicular to it.
            double tx = dx*x[i] + dy*y[i];
            double ty = dx*y[i] - dy*x[i];
            x[i] = tx;
            y[i] = ty;
        }
        for (int i = 0; i < n; ++i) {
            l[i] = x[i + 1] - x[i];
        }
        Arrays.fill(sy, 0, n + 1, 0.);
        boundaryCondition = BoundaryCondition.STOP;
        setBoundaryConditions(y);
        spline1D(y, sy);
    }

    void setControls(int i, HalfEdge he1, HalfEdge he2) {
        double tx, ty;
        tx = x[i] + l[i]/3.;
        ty = y[i] + l[i]*sy[i]/3.;
        he1.xCtrl = tx*dx - ty*dy;
        he1.yCtrl = tx*dy + ty*dx;
        tx = x[i + 1] - l[i]/3.;
        ty = y[i + 1] - l[i]*sy[i + 1]/3.;
        he2.xCtrl = tx*dx - ty*dy;
        he2.yCtrl = tx*dy + ty*dx;
    }

}
