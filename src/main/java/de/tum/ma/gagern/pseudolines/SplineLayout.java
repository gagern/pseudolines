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
import java.util.Arrays;
import java.util.Iterator;

class SplineLayout extends Layout {

    int lengthAdjustments = 1;

    int maxIter = 1000;

    double maxErrThreshold = 1e-5;

    int n;

    double[] x;

    double[] y;

    double[] l;

    double[] sx;

    double[] sy;

    double[] c;

    void performLayout(Arrangement arr) throws LinearSystemException {
        if (x == null || x.length < arr.n) {
            x = new double[arr.n];
            y = new double[arr.n];
            l = new double[arr.n];
            sx = new double[arr.n + 1];
            sy = new double[arr.n + 1];
            c = new double[arr.n*3];
        }
        super.performLayout(arr);
    }

    void layoutInner(PseudoLine pl) {
        n = -1;
        for (PointOnLine p: pl.points()) {
            ++n;
            x[n] = p.xPos;
            y[n] = p.yPos;
        }
        assert n <= x.length;
        calcParameters();
        Iterator<HalfEdge> iter = pl.allHalfEdges().iterator();
        int i = 0;
        while (iter.hasNext()) {
            HalfEdge he1 = iter.next();
            HalfEdge he2 = iter.next();
            setControls(i, he1, he2);
            ++i;
            assert !Double.isNaN(he1.xCtrl);
            assert !Double.isNaN(he1.yCtrl);
            assert !Double.isInfinite(he1.xCtrl);
            assert !Double.isInfinite(he1.yCtrl);
            assert !Double.isNaN(he2.xCtrl);
            assert !Double.isNaN(he2.yCtrl);
            assert !Double.isInfinite(he2.xCtrl);
            assert !Double.isInfinite(he2.yCtrl);
        }
    }

    void setControls(int i, HalfEdge he1, HalfEdge he2) {
        he1.xCtrl = x[i] + l[i]*sx[i]/3.;
        he1.yCtrl = y[i] + l[i]*sy[i]/3.;
        he2.xCtrl = x[i + 1] - l[i]*sx[i + 1]/3.;
        he2.yCtrl = y[i + 1] - l[i]*sy[i + 1]/3.;
    }

    void calcParameters() {
        initLengths();
        Arrays.fill(sx, 0, n + 1, 0.);
        Arrays.fill(sy, 0, n + 1, 0.);
        setBoundaryConditions(x);
        spline1D(x, sx);
        setBoundaryConditions(y);
        spline1D(y, sy);
        for (int recalc = 1; recalc < lengthAdjustments; ++recalc) {
            recalcLengths();
            setBoundaryConditions(x);
            spline1D(x, sx);
            setBoundaryConditions(y);
            spline1D(y, sy);
        }
    }

    void initLengths() {
        if (lengthAdjustments <= 0)
            Arrays.fill(l, 0, n, 1.);
        else
            for (int i = 0; i < n; ++i)
                l[i] = Math.hypot(x[i+1] - x[i], y[i+1] - y[i]);
    }

    void spline1D(double[] y, double[] s) {
        /* Inspired by:
         * http://de.wikipedia.org/wiki/Spline-Interpolation
         *
         * For the 1D case, consider (n+1) points at argument values
         * x[i] and function result y[i], with i = 0..n. We want to
         * find n cubic segments, where seg[i] describes the argument
         * range from x[i] through x[i+1]. Therefore segments are
         * numbered from 0 to (n-1). In the code below, we will never
         * actually use those argument values x[i], but only the
         * differences. There are n differences l[i] = x[i+1] - x[i],
         * denoting the lengths of the segments in the argument
         * domain.
         *
         * In each segment seg[i], we can use homogenous coordinates:
         * u(x) = (x - x[i])/l[i] and v(x) = (x[i+1] - x)/l[i]
         * At the beginning of the segment, we have u=0 and v=1,
         * whereas at the end we have u=1 and v=0.
         *
         * The homogenous formula for the segment is this:
         * seg[i](x) = y[i+1]u³ + p[i]u²v + q[i]uv² + y[i]v³
         *
         * This already ensures that the function passes through the
         * required y values. The parameters p[i] and q[i] have yet to
         * be determined. We also want to match first and second
         * derivatives. So we calculate these derivatives first.
         *
         * du/dx = +1/l[i]
         * dv/dx = -1/l[i]
         * d/dx seg[i] = (y[i+1](3u²)    +
         *                p[i](2uv - u²) +
         *                q[i](v² - 2uv) +
         *                y[i](-3v²)
         *               )/l[i]
         *             = ( (3y[i+1] -  p[i])u² +
         *                2( p[i]   -  q[i])uv +
         *                 ( q[i]   - 3y[i])v²
         *               )/l[i]
         * d²/dx² seg[i] = ( (3y[i+1] -  p[i])(2u)    +
         *                  2( p[i]   -  q[i])(v - u) +
         *                   ( q[i]   - 3y[i])(-2v)
         *                 )/l[i]²
         *               = ((6y[i+1] - 4p[i] + 2q[i])u +
         *                  (2p[i]   - 4q[i] + 6y[i])v
         *                 )/l[i]²
         *
         * Now we take the slopes as variables. Let s[i] be the slope
         * at point x[i], at the end of segment seg[i-1] and the
         * beginning of segment seg[i]. We have
         *
         * s[i] = d/dx seg[ i ](x[i]) = ( q[i] - 3y[ i ])/l[ i ]
         * s[i] = d/dx seg[i-1](x[i]) = (3y[i] -  p[i-1])/l[i-1]
         *
         * We cann now define p[i] and q[i] in terms of s[i]:
         * q[i] = 3y[ i ] + s[ i ]*l[i]
         * p[i] = 3y[i+1] - s[i+1]*l[i]
         *
         * Observe that these are closely related to the Bézier
         * control points, which have coordinates q[i]/3 and p[i]/3.
         *
         * With this definition, the second derivatives at the ends of
         * the segment are:
         *
         * d²/dx² seg[i](x[i])
         *  = (2p[i]   - 4q[i] + 6y[i])/l[i]²
         *  = (   6 y[i+1]
         *     -  2 s[i+1]*l[i]
         *     - 12 y[ i ]
         *     -  4 s[ i ]*l[i]
         *     +  6 y[ i ]
         *    )/l[i]²
         *  = (   6 y[i+1]/l[i]
         *     -  2 s[i+1]
         *     -  6 y[ i ]/l[i]
         *     -  4 s[ i ]
         *    )/l[i]
         * d²/dx² seg[i-1](x[i])
         *  = (6y[i] - 4p[i-1] + 2q[i-1])/l[i-1]²
         *  = (   6 y[ i ]
         *     - 12 y[ i ]
         *     +  4 s[ i ]*l[i-1]
         *     +  6 y[i-1]
         *     +  2 s[i-1]*l[i-1]
         *    )/l[i-1]²
         *  = (-  6 y[ i ]/l[i-1]
         *     +  4 s[ i ]
         *     +  6 y[i-1]/l[i-1]
         *     +  2 s[i-1]
         *    )/l[i-1]
         *
         * We now want these two to be equal as well for segment
         * boundaries:
         *   ( 3y[i+1]/l[ i ] -  s[i+1] - 3y[ i ]/l[ i ] - 2s[ i ])*l[i-1]
         * = (-3y[ i ]/l[i-1] + 2s[ i ] + 3y[i-1]/l[i-1] +  s[i-1])*l[ i ]
         * which we solve for s[i]
         * 2(l[i-1]+l[i])s[i]
         *  =   3(y[i+1] - y[ i ])*l[i-1]/l[ i ]
         *    + 3(y[ i ] - y[i-1])*l[ i ]/l[i-1]
         *    - s[i-1]*l[ i ]
         *    - s[i+1]*l[i-1]
         * s[i] = 3/2(l[i-1]+l[i]) * (  (y[i+1] - y[ i ])*l[i-1]/l[ i ]
         *                            + (y[ i ] - y[i-1])*l[ i ]/l[i-1])
         *        - l[ i ]/2(l[i-1]+l[i]) * s[i-1]
         *        - l[i-1]/2(l[i-1]+l[i]) * s[i+1]
         *
         * So s[i] depends on a constant and two adjacent variables
         * multiplied by constant coefficients. We calculate these
         * constants only once. More precisely, for each s[i] we
         * calculate three constants, c[3*i] the coefficient to
         * multiply s[i-1] by, c[3*i + 1] the constant to add, and
         * c[3*i + 2] the coefficient to multiply s[i+1] by.
         */
        for (int i = 1, j = 0; i < n; ++i) {
            j += 3;
            double f = 1./(2.*(l[i - 1] + l[i]));
            c[ j ] = -l[ i ]*f;
            c[j+1] = 3*f*(  (y[i+1] - y[ i ])*l[i-1]/l[ i ]
                          + (y[ i ] - y[i-1])*l[ i ]/l[i-1]);
            c[j+2] = -l[i-1]*f;
        }
        for (int iter = 0; iter < maxIter; ++iter) {
            double maxErr = 0;
            double si = s[n]; // for s[0] = s[n] if needed
            s[n + 1] = s[1]; // for cyclic calculations if needed
            int j = 0;
            for (int i = 0; i <= n; ++i) {
                si = c[j]*si + c[j+1] + c[j+2]*s[i+1];
                double err = s[i] - si;
                maxErr = Math.max(Math.abs(err), maxErr);
                s[i] = si;
                j += 3;
            }
            if (maxErr < maxErrThreshold)
                break;
        }
    }

    enum BoundaryCondition {

        FREE,

        STOP,

    }

    BoundaryCondition boundaryCondition = BoundaryCondition.FREE;

    void setBoundaryConditions(double[] y) {
        switch (boundaryCondition) {
        case FREE:
            /* We want the second derivative to be zero at the
             * ends. For seg[0] this means:
             * 3(y[1] - y[0])/l[0] - 2s[0] - s[1] = 0
             * s[0] = 3(y[1] - y[0])/2l[0] - s[1]/2
             * and similarly for seg[n-1]:
             * 3(y[n] - y[n-1])/l[n-1] - 2s[n] - s[n-1] = 0
             * s[n] = 3(y[n] - y[n-1])/2l[n-1] - s[n-1]/2
             */
            c[0] = 0.;
            c[1] = 1.5*(y[1] - y[0])/l[0];
            c[2] = -0.5;
            c[3*n    ] = -0.5;
            c[3*n + 1] = 1.5*(y[n] - y[n-1])/l[n-1];
            c[3*n + 2] = 0;
            break;
        case STOP:
            /* Here we simply set the first derivative to zero. This
             * means that the end of the parameter domain will result
             * in little motion in the plane, hence the name.
             */
            c[0] = c[1] = c[2] = c[3*n] = c[3*n + 1] = c[3*n + 2] = 0.;
            break;
        default:
            throw new Error("Unknown boundary condition: " + boundaryCondition);
        }
    }

    void recalcLengths() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
