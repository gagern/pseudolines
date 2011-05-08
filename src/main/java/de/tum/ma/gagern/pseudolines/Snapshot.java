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
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

class Snapshot {

    private static final Shape UNIT_CIRCLE =
        new Ellipse2D.Double(-1, -1, 2, 2);

    PseudoLine circleLine;

    List<PseudoLinePath> paths;

    List<CellShape> triangles;

    Snapshot(Arrangement arr) {
        paths = new ArrayList<PseudoLinePath>(arr.n);
        triangles = new ArrayList<CellShape>(arr.triangles.size());
    }

    public void render(PseudoLineRenderer renderer) {
        for (CellShape triangle: triangles) {
            try {
                renderer.renderCell(triangle.cell, triangle.getShape());
            }
            catch (ClassCastException e) {
                e.printStackTrace();
                double[] c = new double[6];
                for (int i = 0; i < triangle.edges.length; ++i) {
                    System.err.println("edges[" + i + "]");
                    java.awt.geom.PathIterator pi =
                        triangle.edges[i].getPathIterator(null);
                    while (!pi.isDone()) {
                        java.util.Arrays.fill(c, -123.);
                        int type = pi.currentSegment(c);
                        int nc;
                        String tn;
                        switch (type) {
                        case java.awt.geom.PathIterator.SEG_MOVETO:
                            tn = " SEG_MOVETO:";
                            nc = 2;
                            break;
                        case java.awt.geom.PathIterator.SEG_CUBICTO:
                            tn = " SEG_CUBICTO:";
                            nc = 6;
                            break;
                        default:
                            tn = " (" + type + "):";
                            nc = 6;
                        }
                        System.err.println(tn);
                        for (int j = 0; j < nc; ++j) {
                            System.err.println("  c[" + j + "]=" + c[j]);
                        }
                        pi.next();
                    }
                }
                System.exit(1);
            }
        }
        for (PseudoLinePath p: paths) {
            renderer.renderLine(p.pseudoLine, p);
        }
        if (circleLine != null) {
            renderer.renderLine(circleLine, UNIT_CIRCLE);
        }
    }

}
