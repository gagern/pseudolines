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
