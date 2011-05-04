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

import java.util.Arrays;
import java.util.List;

class Catalog {

    static final String M10_LOGO

        // 000000000000000000000000000000000000
        // 111111112222222333333444445555666778
        // 234567893456789456789567896789789899
        = "++++++++++++++++++++++++++++++++++++"

        // 1111111111111111111111111111
        // 2222222333333444445555666778
        // 3456789456789567896789789899
        + "+++++++++++--++---+---------"

        // 222222222222222222222
        // 333333444445555666778
        // 456789567896789789899
        + "++++--+--------------"

        // 333333333333333
        // 444445555666778
        // 567896789789899
        + "+--------------"

        // 4444444444
        // 5555666778
        // 6789789899
        + "----------"

        // 555555
        // 666778
        // 789899
        + "----++"

        // 666
        // 778
        // 899
        + "-++"

        // 7
        // 8
        // 9
        + "+";

    String name;

    String signs;

    Catalog(String name, String signs) {
        this.name = name;
        this.signs = signs;
    }

    @Override public String toString() {
        return name;
    }

    public Chirotope getChirotope() {
        return new ByteArrayChirotope(signs);
    }

    public static List<Catalog> getCatalog() {
        Catalog[] cat = {
            new Catalog("Logo M10", M10_LOGO),
        };
        return Arrays.asList(cat);
    }

}
