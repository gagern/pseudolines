package de.tum.ma.gagern.pseudolines;

import java.util.ArrayList;
import java.util.List;

class Intersection extends PointOnLine {

    Variable xPos, yPos, xDir, yDir;

    LinVec2 pos, dir;

    List<PointOnLine> neighbours;

    Intersection(int numPseudolines) {
        xPos = new Variable();
        yPos = new Variable();
        xDir = new Variable();
        yDir = new Variable();
        pos = new LinVec2(xPos, yPos);
        dir = new LinVec2(xDir, yDir);
        neighbours = new ArrayList<PointOnLine>(2*numPseudolines);
        for (int i = 0; i < 2*numPseudolines; ++i)
            neighbours.add(null);
    }

    void addCrossing(PointOnLine prev, PseudoLine line, PointOnLine next) {
        int idx = neighbourIndex(null);
        assert idx < neighbours.size()/2;
        assert neighbours.get(idx + neighbours.size()/2) == null;
        neighbours.set(idx, prev);
        neighbours.set(idx + neighbours.size()/2, prev);
    }

    private int neighbourIndex(PointOnLine neighbour) {
        int idx = neighbours.indexOf(neighbour);
        if (idx == -1)
            throw notANeighbour();
        return idx;
    }

    LinVec2 getLocation() {
        return pos;
    }

    LinVec2 getControlTowards(PointOnLine neighbour) {
        return dir.rot(neighbourIndex(neighbour), neighbours.size()).add(pos);
    }

    @Override void addEquations(LinearSystem ls) {
        int n = neighbours.size();
        LinVec2 zPos = pos.scale(-n);
        LinVec2 zDir = dir.scale(-2*n);
        for (int i = 0; i < n; ++i) {
            LinVec2 nc = neighbours.get(i).getControlTowards(this);
            zPos = zPos.add(nc);
            zDir = zDir.add(nc.sub(pos).rot(-i, n));
        }
        ls.eqZero(zPos);
        ls.eqZero(zDir);
    }

}
