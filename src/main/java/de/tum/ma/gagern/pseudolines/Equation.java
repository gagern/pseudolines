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
