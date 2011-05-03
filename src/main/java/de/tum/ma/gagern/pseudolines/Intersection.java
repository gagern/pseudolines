package de.tum.ma.gagern.pseudolines;

class Intersection extends PointOnLine {

    Variable xPos, yPos, xDir, yDir;

    LinVec2 pos, dir;

    Intersection(int numPseudolines) {
        super(numPseudolines);
        xPos = new Variable();
        yPos = new Variable();
        xDir = new Variable();
        yDir = new Variable();
        pos = new LinVec2(xPos, yPos);
        dir = new LinVec2(xDir, yDir);
    }

    LinVec2 getLocation() {
        return pos;
    }

}
