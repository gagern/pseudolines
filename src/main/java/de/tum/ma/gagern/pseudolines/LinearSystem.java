package de.tum.ma.gagern.pseudolines;

import java.util.ArrayList;
import java.util.List;

class LinearSystem {

    private List<Variable> vars;

    private int[][] cols;

    private double[][] vals;

    private double[] rhs;

    private int nRows;

    public LinearSystem(int initialSize) {
        vars = new ArrayList<Variable>(initialSize);
        cols = new int[initialSize][];
        vals = new double[initialSize][];
        rhs = new double[initialSize];
        nRows = 0;
    }

    private int index(Variable var) {
        int n = vars.size();
        int vi = var.index;
        if (vi >= 0 && vi < n && vars.get(vi) == var)
            return vi;
        var.index = n;
        vars.add(var);
        return n;
    }

    private void add(Equation eq) {
        int n = eq.size();
        int[] rCols = new int[n];
        double[] rVals = new double[n];
        for (int i = 0; i < n; ++i) {
            rCols[i] = index(eq.var(i));
            rVals[i] = eq.coeff(i);
        }
        if (nRows == rhs.length) {
            int[][] c = new int[2*nRows][];
            double[][] v = new double[2*nRows][];
            double[] r = new double[2*nRows];
            System.arraycopy(cols, 0, c, 0, nRows);
            System.arraycopy(vals, 0, v, 0, nRows);
            System.arraycopy(rhs, 0, r, 0, nRows);
            cols = c;
            vals = v;
            rhs = r;
        }
        cols[nRows] = rCols;
        vals[nRows] = rVals;
        rhs[nRows] = eq.rhs();
        ++nRows;
    }

    public void eqZero(LinComb lc) {
        Equation eq = new Equation();
        lc.addToEquation(1., eq);
        add(eq);
    }

    public void eqZero(LinVec2 v) {
        eqZero(v.getXTerm());
        eqZero(v.getYTerm());
    }

}
