package de.tum.ma.gagern.pseudolines;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestChirotope {

    String UNIFORM_CHIRO10_SIGNS = Catalog.M10_LOGO;

    @Test public void testCreateChirotope() {
        new ByteArrayChirotope(UNIFORM_CHIRO10_SIGNS);
    }

    private void assertLS(Chirotope chi, int line, String expected) {
        LineSequenceElement[][] a =
            LineSequenceElement.getLineSequence(chi, line);
        String sep = "";
        StringBuilder buf = new StringBuilder();
        buf.append(line).append(":");
        for (LineSequenceElement[] b: a) {
            for (LineSequenceElement c: b) {
                buf.append(sep).append(c);
                sep=" = ";
            }
            sep = ",";
        }
        assertEquals(expected, buf.toString());
    }

    private void assertLS(String chiSigns, String... expected) {
        Chirotope chi = new ByteArrayChirotope(chiSigns);
        assertEquals(expected.length, chi.numElements());
        for (int i = 0; i < expected.length; ++i)
            assertLS(chi, i, expected[i]);
    }

    @Test public void testLineSequences() {
        assertLS(UNIFORM_CHIRO10_SIGNS,
                 "0:+1,+2,+3,+4,+5,+6,+7,+8,+9",
                 "1:+0,-2,-9,-8,-3,-7,-4,-5,-6",
                 "2:+0,+1,-9,-8,-3,-7,-6,-4,-5",
                 "3:+0,-9,-8,+1,+2,-7,-6,-4,-5",
                 "4:+0,-9,-8,-7,+1,-6,+2,+3,-5",
                 "5:+0,-8,-7,-9,+1,-6,+2,+3,+4",
                 "6:+0,-8,-7,-9,+1,+5,+4,+2,+3",
                 "7:+0,-8,+6,+5,-9,+4,+1,+2,+3",
                 "8:+0,+7,+6,+5,-9,+4,+3,+1,+2",
                 "9:+0,+6,+5,+7,+8,+4,+3,+1,+2");
    }

}
