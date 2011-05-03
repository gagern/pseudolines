package de.tum.ma.gagern.pseudolines;

class Equation {

    private static final int INITIAL_SIZE = 8;

    double[] coeffs;

    Variable[] vars;

    Variable diagonalVar;

    int len;

    double rhs;

    public Equation() {
        coeffs = new double[INITIAL_SIZE];
        vars = new Variable[INITIAL_SIZE];
        len = 0;
        rhs = 0.;
    }

    public void addToLHS(double coeff, Variable var) {
        if (coeff == 0)
            return;
        for (int i = 0; i < len; ++i) {
            if (vars[i] == var) {
                coeffs[i] += coeff;
                return;
            }
        }
        if (vars.length == len)
            setCapacity(2*len);
        coeffs[len] = coeff;
        vars[len] = var;
        ++len;
    }

    private void setCapacity(int capacity) {
        double[] nc = new double[capacity];
        Variable[] nv = new Variable[capacity];
        System.arraycopy(coeffs, 0, nc, 0, len);
        System.arraycopy(vars, 0, nv, 0, len);
        coeffs = nc;
        vars = nv;
    }

    public void addToRHS(double val) {
        rhs += val;
    }

    int size() {
        return len;
    }

    double coeff(int index) {
        if (index > len)
            throw new IndexOutOfBoundsException();
        return coeffs[index];
    }

    Variable var(int index) {
        if (index > len)
            throw new IndexOutOfBoundsException();
        return vars[index];
    }

    double rhs() {
        return rhs;
    }

    Variable getDiagonalVar() {
        return vars[0];
    }

    void compact() {
        if (len != vars.length)
            setCapacity(len);
    }

}
