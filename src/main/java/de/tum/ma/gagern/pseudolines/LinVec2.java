package de.tum.ma.gagern.pseudolines;

class LinVec2 {

    private final LinComb x;

    private final LinComb y;

    public LinVec2(LinComb x, LinComb y) {
        this.x = x;
        this.y = y;
    }

    public LinVec2 add(LinVec2 that) {
        return new LinVec2(x.add(that.x), y.add(that.y));
    }

    public LinVec2 sub(LinVec2 that) {
        return new LinVec2(x.sub(that.x), y.sub(that.y));
    }

    public LinVec2 scale(double f) {
        return new LinVec2(x.mul(f), y.mul(f));
    }

    public LinVec2 rot(double angle) {
        double s = Math.sin(angle), c = Math.cos(angle);
        return new LinVec2(x.mul(c).add(y.mul(-s)),
                           x.mul(s).add(y.mul(c)));
    }

    public LinVec2 rot(int part, int fullTurn) {
        return rot(Math.PI*2.*part/fullTurn);
    }

    public LinComb getXTerm() {
        return x;
    }

    public LinComb getYTerm() {
        return y;
    }

}
