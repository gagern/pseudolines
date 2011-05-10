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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Snapshot {

    static final Shape UNIT_CIRCLE =
        new Ellipse2D.Double(-1, -1, 2, 2);

    Arrangement arr;

    private List<PseudoLinePath> lines;

    private Map<Cell, CellShape> triangles;

    Snapshot(Arrangement arr, Layout layout) {
        this.arr = arr;
        lines = new ArrayList<PseudoLinePath>(arr.n);
        triangles = new HashMap<Cell, CellShape>(arr.triangles.size());
        for (int i = 0; i < arr.n; ++i) {
            PseudoLine pl = arr.lines.get(i);
            PseudoLinePath pth = layout.getPath(pl);
            pth.pseudoLine = pl;
            lines.add(pth);
        }
        for (Cell triangle: arr.triangles) {
            CellShape cs = layout.getShape(triangle);
            triangles.put(triangle, cs);
        }
    }

    public void render(PseudoLineRenderer renderer) {
        renderer.setAlpha(1.);
        for (CellShape triangle: triangles.values()) {
            renderer.renderCell(triangle.cell, triangle.getShape());
        }
        for (PseudoLinePath p: lines) {
            renderer.renderLine(p.pseudoLine, p);
        }
    }

    CellShape getTriangle(Cell triangle) {
        return triangles.get(triangle);
    }

    PseudoLinePath getLine(int index) {
        return lines.get(index);
    }

    PseudoLinePath getLine(PseudoLine pl) {
        for (PseudoLinePath pth: lines)
            if (pth.pseudoLine == pl)
                return pth;
        return null;
    }

    int size() {
        return lines.size();
    }

    Collection<CellShape> triangles() {
        return triangles.values();
    }

    Collection<PseudoLinePath> lines() {
        return lines;
    }

}
