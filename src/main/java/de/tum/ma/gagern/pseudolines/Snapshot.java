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

import java.awt.Color;
import java.awt.Graphics2D;
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

    Color triangleColor;

    Snapshot(Arrangement arr) {
        paths = new ArrayList<PseudoLinePath>(arr.n);
        triangles = new ArrayList<CellShape>(arr.triangles.size());
        triangleColor = Color.RED;
    }

    public void render(Graphics2D g2d) {
        g2d.setColor(triangleColor);
        for (CellShape triangle: triangles) {
            g2d.fill(triangle.getShape());
        }
        for (PseudoLinePath p: paths) {
            g2d.setColor(p.pseudoLine.color);
            g2d.draw(p);
        }
        if (circleLine != null) {
            g2d.setColor(circleLine.color);
            g2d.draw(UNIT_CIRCLE);
        }
    }

}
