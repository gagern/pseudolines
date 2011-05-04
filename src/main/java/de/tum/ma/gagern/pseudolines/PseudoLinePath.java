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

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

class PseudoLinePath implements Shape {

    private static final int COORDS_PER_POINT = 6;
    private static final int POS_X = 0;
    private static final int POS_Y = POS_X + 1;
    private static final int DIR_X = 2;
    private static final int DIR_Y = DIR_X + 1;
    private static final int LEN_IN = 4;
    private static final int LEN_OUT = 5;

    PseudoLine pseudoLine;

    double[] coords;

    int length;

    PseudoLinePath(int maxPoints) {
        coords = new double[maxPoints*COORDS_PER_POINT];
        length = 0;
    }

    void addPoint(double px, double py,
                  double dx, double dy,
                  double inLength, double outLength) {
        coords[length + POS_X  ] = px;
        coords[length + POS_Y  ] = py;
        coords[length + DIR_X  ] = dx;
        coords[length + DIR_Y  ] = dy;
        coords[length + LEN_IN ] = inLength;
        coords[length + LEN_OUT] = outLength;
        length += COORDS_PER_POINT;
    }

    void addSymmetric(double x, double y, double dx, double dy) {
        addPoint(x, y, dx, dy, 1., 1.);
    }

    // Implementation of Shape

    public boolean contains(double x, double y) {
        return false;
    }

    public boolean contains(Point2D point) {
        return false;
    }

    public boolean contains(double x, double y, double w, double h) {
        return false;
    }

    public boolean contains(Rectangle2D rect) {
        return false;
    }

    public boolean intersects(double left, double top, double w, double h) {
        double right = left+w, bottom = top+h;
        boolean allLeft = true, allRight = true;
        boolean allTop = true, allBottom = true;
        for (int i = 0; i < coords.length; i += 2) {
            double x = coords[i], y = coords[i + 1];
            allLeft &= (x < left);
            allRight &= (x > right);
            allTop &= (y < top);
            allBottom &= (y > bottom);
        }
        return allLeft || allRight || allTop || allBottom;
    }

    public boolean intersects(Rectangle2D rect) {
        return intersects(rect.getX(), rect.getY(),
                          rect.getWidth(), rect.getHeight());
    }

    public Rectangle getBounds() {
        return getBounds2D().getBounds();
    }

    public Rectangle2D getBounds2D() {
        Rectangle2D.Double rect =
            new Rectangle2D.Double(Double.POSITIVE_INFINITY,
                                   Double.POSITIVE_INFINITY,
                                   Double.NEGATIVE_INFINITY,
                                   Double.NEGATIVE_INFINITY);
        for (int i = 0; i < coords.length; i += 2)
            rect.add(coords[i], coords[i + 1]);
        return rect;
    }

    public PathIterator getPathIterator(AffineTransform at) {
        return new PseudoLineIterator(at);
    }

    public PathIterator getPathIterator(AffineTransform at, double flatness) {
        return new FlatteningPathIterator(getPathIterator(at), flatness);
    }
 
    class PseudoLineIterator extends AbstractPathIterator {

        int pos;

        PseudoLineIterator(AffineTransform at) {
            super(at);
            pos = 0;
        }

        protected int rawSegment(double[] c) {
            if (pos != 0) {
                double px1 = coords[pos + (POS_X - COORDS_PER_POINT)];
                double py1 = coords[pos + (POS_Y - COORDS_PER_POINT)];
                double dx1 = coords[pos + (DIR_X - COORDS_PER_POINT)];
                double dy1 = coords[pos + (DIR_Y - COORDS_PER_POINT)];
                double l1 = coords[pos + (LEN_OUT - COORDS_PER_POINT)];
                double px2 = coords[pos + POS_X];
                double py2 = coords[pos + POS_Y];
                double dx2 = coords[pos + DIR_X];
                double dy2 = coords[pos + DIR_Y];
                double l2 = coords[pos + LEN_IN];
                c[0] = px1 + dx1*l1;
                c[1] = py1 + dy1*l1;
                c[2] = px2 - dx2*l2;
                c[3] = py2 - dy2*l2;
                c[4] = px2;
                c[5] = py2;
                return SEG_CUBICTO;
            }
            else {
                c[0] = coords[POS_X];
                c[1] = coords[POS_Y];
                return SEG_MOVETO;
            }
        }

        public boolean isDone() {
            return pos >= length;
        }

        public void next() {
            pos += COORDS_PER_POINT;
        }

    }

}
