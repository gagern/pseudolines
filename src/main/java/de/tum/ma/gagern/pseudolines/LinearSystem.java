/*
 * pseudolines - Display pyseudoline arrangements
 * Copyright (C) 2011 Martin von Gagern
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.tum.ma.gagern.pseudolines;

import java.util.ArrayList;
import java.util.List;

class LinearSystem {

    int maxIter = 1024;

    double maxErrThreshold = 1e-4;

    List<Equation> equations;

    public LinearSystem() {
        equations = new ArrayList<Equation>();
    }

    public void solve() throws LinearSystemException {
        int size = equations.size();
        Variable[] vars = new Variable[size];
        int[][] cols = new int[size][];
        double[][] coeffs = new double[size][];
        double[] rhs = new double[size];
        double[] x = new double[size];
        double[] diag = new double[size];
        for (int r = 0; r < size; ++r) {
            Variable v = equations.get(r).getDiagonalVar();
            if (v.index >= 0 && v.index < r && vars[v.index] == v)
                throw new LinearSystemException("Duplicate diagonal variable");
            v.index = r;
            vars[r] = v;
            x[r] = v.value;
            if (Double.isNaN(x[r]) || Double.isInfinite(x[r]))
                x[r] = 0.;
        }
        for (int r = 0; r < size; ++r) {
            Equation eq = equations.get(r);
            int n = eq.size();
            int[] rCols = new int[n - 1];
            double[] rCoeffs = new double[n - 1];
            for (int i = 0, j = 0; i < n; ++i) {
                Variable v = eq.var(i);
                if (vars[v.index] != v)
                    throw new LinearSystemException
                        ("Variable not on any diagonal");
                int idx = v.index;
                double coeff = eq.coeff(i);
                if (Double.isNaN(coeff))
                    throw new LinearSystemException("Coefficient is NaN");
                if (Double.isInfinite(coeff))
                    throw new LinearSystemException("Coefficient is infinite");
                if (idx == r) {
                    if (coeff == 0.)
                        throw new LinearSystemException
                            ("Diagonal coefficient is zero");
                    diag[r] = coeff;
                }
                else {
                    rCols[j] = idx;
                    rCoeffs[j] = coeff;
                    ++j;
                }
            }
            cols[r] = rCols;
            coeffs[r] = rCoeffs;
            rhs[r] = eq.rhs();
        }
        for (int iter = 0; iter < maxIter; ++iter) {
            double maxErr = 0;
            for (int r = 0; r < size; ++r) {
                double nv = rhs[r]; // compute new value for x[r]
                for (int c = 0; c < cols[r].length; ++c) {
                    nv -= coeffs[r][c]*x[cols[r][c]];
                }
                nv /= diag[r];
                double err = nv - x[r];
                maxErr = Math.max(Math.abs(err), maxErr);
                x[r] = nv;
            }
            System.out.printf("Iteration %4d: Error %12.8f%n", iter, maxErr);
            if (maxErr < maxErrThreshold)
                break;
        }
        for (int r = 0; r < size; ++r) {
            vars[r].value = x[r];
        }
    }

    public void clear() {
        equations.clear();
    }

    public void eqZero(Variable diagonalVar, LinComb lc) {
        Equation eq = new Equation();
        eq.diagonalVar = diagonalVar;
        lc.addToEquation(1., eq);
        eq.compact();
        equations.add(eq);
    }

    public void eqZero(LinComb lc) {
        Equation eq = new Equation();
        lc.addToEquation(1., eq);
        eq.compact();
        equations.add(eq);
    }

    public void eqZero(Variable xDiagVar, Variable yDiagVar, LinVec2 v) {
        eqZero(xDiagVar, v.getXTerm());
        eqZero(yDiagVar, v.getYTerm());
    }

    public void eqZero(LinVec2 v) {
        eqZero(v.getXTerm());
        eqZero(v.getYTerm());
    }

}
