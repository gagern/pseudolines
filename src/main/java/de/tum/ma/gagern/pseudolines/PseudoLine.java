package de.tum.ma.gagern.pseudolines;

import java.awt.Color;

class PseudoLine {

    EndPoint start;

    EndPoint end;

    Color color = Color.BLACK;

    void setEndPoints(EndPoint start, EndPoint end) {
        this.start = start;
        this.end = end;
    }

}
