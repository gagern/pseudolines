package de.tum.ma.gagern.pseudolines;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

class Snapshot {

    private static final Shape UNIT_CIRCLE =
        new Ellipse2D.Double(-1, -1, 2, 2);

    PseudoLine circleLine;

    List<PseudoLinePath> paths;

    Snapshot(Arrangement arr) {
        paths = new ArrayList<PseudoLinePath>(arr.n);
    }

    public void render(Graphics2D g2d) {
        for (PseudoLinePath p: paths) {
            g2d.setColor(p.pseudoLine.color);
            g2d.draw(p);
        }
        if (circleLine != null) {
            g2d.setColor(circleLine.color);
            g2d.draw(UNIT_CIRCLE);
        }
    }

}
