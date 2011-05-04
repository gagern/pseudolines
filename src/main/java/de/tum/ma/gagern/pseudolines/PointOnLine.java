package de.tum.ma.gagern.pseudolines;

abstract class PointOnLine {

    int id;

    private PointOnLine[] neighbours;

    PointOnLine(int numPseudolines) {
        neighbours = new PointOnLine[numPseudolines*2];
    }

    abstract LinVec2 getLocation();

    int neighbourIndex(PointOnLine pt) {
        for (int i = 0; i < neighbours.length; ++i)
            if (neighbours[i] == pt)
                return i;
        throw new IllegalArgumentException("Argument is not a neighbour");
    }

    int numNeighbours() {
        return neighbours.length;
    }

    PointOnLine neighbour(int index) {
        return neighbours[index];
    }

    boolean hasNeighbour(PointOnLine pt) {
        for (int i = 0; i < neighbours.length; ++i)
            if (neighbours[i] == pt)
                return true;
        return false;
    }

    PointOnLine relativeNeighbour(PointOnLine reference, int offset) {
        int idx = neighbourIndex(reference) + offset + neighbours.length;
        return neighbours[idx%neighbours.length];
    }

    PointOnLine opposite(PointOnLine incoming) {
        return relativeNeighbour(incoming, neighbours.length/2);
    }

    void addCrossing(PointOnLine prev, PseudoLine line, PointOnLine next) {
        if (prev == null)
            throw new NullPointerException("prev must not be null");
        int idx = neighbourIndex(null);
        assert idx < neighbours.length/2;
        assert neighbours[idx + neighbours.length/2] == null;
        neighbours[idx] = prev;
        neighbours[idx + neighbours.length/2] = next;
    }

}
