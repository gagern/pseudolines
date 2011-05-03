package de.tum.ma.gagern.pseudolines;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import javax.swing.JComponent;

public class PseudoLineView extends JComponent {

    private static final int RIM_SIZE = 20;

    private static final int MIN_TOTAL_SIZE = RIM_SIZE*2 + 10;

    private static final double THIN_WIDTH = 2.;

    private static final double THICK_WIDTH = 5.;

    private Arrangement arr;

    private Snapshot snapshot;

    private Stroke thinStroke;

    private Stroke thickStroke;

    public PseudoLineView() throws LinearSystemException {
        Chirotope chi = Catalog.getCatalog().get(0).getChirotope();
        arr = new Arrangement(chi, 0);
        Layout layout = new RegularBezierLayout();
        snapshot = arr.snapshot(layout);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = getWidth(), h = getHeight();
        if (w < MIN_TOTAL_SIZE || h < MIN_TOTAL_SIZE)
            return;
        Graphics2D g2d = (Graphics2D)g.create();
        g2d.translate(w/2., h/2.);
        double scale = (Math.min(w, h) - 2*RIM_SIZE)/2.;
        g2d.scale(scale, scale);
        thinStroke = new BasicStroke((float)(THIN_WIDTH/scale),
                                     BasicStroke.CAP_ROUND,
                                     BasicStroke.JOIN_MITER);
        thickStroke = new BasicStroke((float)(THICK_WIDTH/scale),
                                      BasicStroke.CAP_ROUND,
                                      BasicStroke.JOIN_ROUND);
        g2d.setStroke(thinStroke);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                             RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING,
                             RenderingHints.VALUE_DITHER_DISABLE);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                             RenderingHints.VALUE_STROKE_PURE);
        snapshot.render(g2d);
        g2d.dispose();
    }

}
