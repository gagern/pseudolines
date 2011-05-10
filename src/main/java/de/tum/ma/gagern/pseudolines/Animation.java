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

import javax.swing.undo.CompoundEdit;

abstract class Animation {

    Arrangement arr;

    Snapshot before;

    Snapshot after;

    long startTime;

    double linear;

    double p0, p1;

    PseudoLineRenderer renderer;

    CompoundEdit undo;

    Snapshot plan(Arrangement arrangement,
                  Layout layout,
                  Snapshot before)
        throws LinearSystemException
    {
        this.arr = arrangement;
        this.before = before;
        undo = new NamedCompoundEdit(getOperationName());
        preProcess();
        try {
            performChange();
            after = arrangement.snapshot(layout);
        }
        catch (LinearSystemException e) {
            undo.undo();
            arrangement.findTriangles();
            undo = null;
            this.after = before;
            throw e;
        }
        this.after = after;
        postProcess();
        return after;
    }

    abstract String getOperationName();

    abstract void performChange() throws LinearSystemException;

    void preProcess() {
    }

    void postProcess() {
    }

    public void start(PseudoLineRenderer renderer) {
        startTime = System.currentTimeMillis();
        this.renderer = renderer;
    }

    public int duration() {
        return 300;
    }

    public boolean animate() {
        linear = (System.currentTimeMillis() - startTime)/(double)duration();
        if (linear > 1.) {
            this.renderer = null;
            return false;
        }
        p1 = paramCurve(linear);
        p0 = 1. - p1;
        renderAnimated();
        return true;
    }

    abstract void renderAnimated();

    public double paramCurve(double linear) {
        return (3. - 2.*linear)*linear*linear;
        // return ((6*linear - 15)*linear + 10)*linear*linear*linear;
    }

}
