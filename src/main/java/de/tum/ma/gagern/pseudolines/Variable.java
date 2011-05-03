package de.tum.ma.gagern.pseudolines;

class Variable extends LinComb {

    int index;

    double value;

    Variable() {
        index = -1;
        value = 0;
    }

    public void addToEquation(double coeff, Equation eq) {
        eq.addToLHS(coeff, this);
    }

    public double getValue() {
        return value;
    }

}
