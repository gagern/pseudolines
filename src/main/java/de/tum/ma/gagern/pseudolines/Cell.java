package de.tum.ma.gagern.pseudolines;

import java.awt.Shape;
import java.util.Arrays;
import java.util.List;

class Cell {

    List<PointOnLine> corners;

    Shape shape;

    Cell(PointOnLine... corners) {
        this.corners = Arrays.asList(corners);
    }

}
