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

class Intersection extends PointOnLine {

    Variable xPos, yPos, xDir, yDir;

    LinVec2 pos, dir;

    Intersection(int numPseudolines) {
        super(numPseudolines);
        xPos = new Variable();
        yPos = new Variable();
        xDir = new Variable();
        yDir = new Variable();
        pos = new LinVec2(xPos, yPos);
        dir = new LinVec2(xDir, yDir);
    }

    LinVec2 getLocation() {
        return pos;
    }

}
