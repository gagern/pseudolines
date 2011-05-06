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
import java.awt.geom.Area;
import java.awt.geom.Path2D;

class CellShape {

    PseudoLinePath[] edges;

    Cell cell;

    Shape cached;

    CellShape(Cell cell) {
        this.cell = cell;
        edges = new PseudoLinePath[cell.size()];
    }

    Shape getShape() {
        if (cached == null)
            cached = calculateShape();
        return cached;
    }

    Shape calculateShape() {
        Path2D.Double res =
            new Path2D.Double(Path2D.WIND_NON_ZERO, 2*edges.length + 1);
        for (PseudoLinePath edge: edges)
            res.append(edge, true);
        res.closePath();
        return new Area(res);
    }

}
