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
        edges = new PseudoLinePath[cell.corners.size()];
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
