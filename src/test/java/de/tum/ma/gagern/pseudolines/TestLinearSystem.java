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

import org.junit.Test;

import static org.junit.Assert.*;

public class TestLinearSystem {

    static LinComb cons(double c) {
        return LinComb.constant(c);
    }

    @Test public void testSimpleSystem() throws LinearSystemException {
        /*
          (7 0 1)   (a= 4)   (25)
          (0 5 3) * (b= 2) = ( 1)
          (6 0 8)   (c=-3)   ( 0)
         */
        Variable a = new Variable();
        Variable b = new Variable();
        Variable c = new Variable();
        LinearSystem ls = new LinearSystem();
        ls.eqZero(a.mul(7).add(c).add(cons(-25)));
        ls.eqZero(b.mul(5).add(c.mul(3)).add(cons(-1)));
        ls.eqZero(c.mul(8).add(a.mul(6)));
        ls.solve();
        assertEquals("a", 4., a.value, 1e-3);
        assertEquals("b", 2., b.value, 1e-3);
        assertEquals("c", -3., c.value, 1e-3);
    }

}
