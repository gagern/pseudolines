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

import java.util.Collection;
import java.util.Iterator;

class OpenEndPoint extends EndPoint {

    Collection<PointOnLine> surrounding;

    LinVec2 getLocation() {
        Iterator<PointOnLine> iter = surrounding.iterator();
        LinVec2 loc = iter.next().getLocation();
        while (iter.hasNext())
            loc = loc.add(iter.next().getLocation());
        return loc.scale(1./surrounding.size());
    }

}
