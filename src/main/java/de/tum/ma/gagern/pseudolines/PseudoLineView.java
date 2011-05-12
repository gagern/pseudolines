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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Collection;
import javax.swing.JComponent;
import javax.swing.Timer;
import javax.swing.event.MouseInputAdapter;

public class PseudoLineView
    extends JComponent
    implements PseudoLineRenderer
{

    private static final int ANIMATION_DELAY = 20;

    private static final int RIM_SIZE = 20;

    private static final int MIN_TOTAL_SIZE = RIM_SIZE*2 + 10;

    private static final double THIN_WIDTH = 2.;

    private static final double THICK_WIDTH = 5.;

    private AffineTransform unitCircleTransform;

    private Arrangement arr;

    private Layout layout;

    private Animation animation;

    private Timer animTimer;

    private Snapshot snapshot;

    private Color triangleColor;

    private Color mixedTriangleColor;

    private boolean fillRimCells = true;

    private Graphics2D g2d;

    private double scale;

    private Stroke thinStroke;

    private Stroke thickStroke;

    public PseudoLineView() throws LinearSystemException {
        Chirotope chi = Catalog.getCatalog().get(0).getChirotope();
        arr = new Arrangement(chi, 0);
        layout = new SplineLayout();
        snapshot = arr.snapshot(layout);
        addComponentListener(new ComponentAdapter() {
                @Override public void componentResized(ComponentEvent evnt) {
                    unitCircleTransform = null;
                }
            });
        animTimer = new Timer(ANIMATION_DELAY, new ActionListener() {
                public void actionPerformed(ActionEvent evnt) {
                    repaint();
                }
            });
        animTimer.setInitialDelay(ANIMATION_DELAY/4);
        Mouser mouser = new Mouser();
        addMouseListener(mouser);
        addMouseMotionListener(mouser);
        triangleColor = new Color(0xffaa00);
        mixedTriangleColor = triangleColor;
        setBackground(new Color(0xcccccc));
    }

    AffineTransform unitCircleTransform() {
        if (unitCircleTransform != null)
            return unitCircleTransform;
        AffineTransform at = new AffineTransform();
        int w = getWidth(), h = getHeight();
        double scale = 2./(Math.min(w, h) - 2*RIM_SIZE);
        at.scale(scale, -scale);
        at.translate(-w/2., -h/2.);
        unitCircleTransform = at;
        return at;
    }

    Point2D toUnitCircle(Point2D in, Point2D out) {
        return unitCircleTransform().transform(in, out);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = getWidth(), h = getHeight();
        g.setColor(getBackground());
        g.fillRect(0, 0, w, h);
        if (w < MIN_TOTAL_SIZE || h < MIN_TOTAL_SIZE)
            return;
        g2d = (Graphics2D)g.create();
        g2d.translate(w/2., h/2.);
        scale = (Math.min(w, h) - 2*RIM_SIZE)/2.;
        g2d.scale(scale, -scale);
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
        g2d.setColor(Color.WHITE);
        g2d.fill(Snapshot.UNIT_CIRCLE);
        if (animation == null) {
            snapshot.render(this);
            //drawControlPoints(arr.points, Color.RED);
        }
        else if (!animation.animate()) {
            animation = null;
            animTimer.stop();
            snapshot.render(this);
        }
        g2d.dispose();
        g2d = null;
    }

    public void drawControlPoints(Collection<PointOnLine> points, Color color) {
        g2d.setColor(color);
        Ellipse2D circ = new Ellipse2D.Double();
        for (PointOnLine p: points) {
            for (HalfEdge he: p) {
                if (he.connection == null)
                    continue;
                circ.setFrameFromCenter(he.xCtrl, he.yCtrl,
                                        he.xCtrl - 1./scale,
                                        he.yCtrl - 1./scale);
                g2d.fill(circ);
            }
        }
    }

    public void renderLine(PseudoLine pl, Shape shape) {
        g2d.setColor(pl.color);
        g2d.draw(shape);
    }

    public void renderCell(Cell cell, Shape shape) {
        if (cell.size() == 3 &&
            (fillRimCells || !cell.isAtRim())) {
            g2d.setColor(mixedTriangleColor);
            g2d.fill(shape);
        }
    }

    public void setAlpha(double alpha) {
        if (alpha == 1.) {
            mixedTriangleColor = triangleColor;
        }
        else {
            mixedTriangleColor = mixColor(alpha, Color.WHITE, triangleColor);
        }
    }

    Color mixColor(double f, Color color0, Color color1) {
        float[] c0 = new float[4];
        float[] c1 = new float[4];
        color0.getRGBComponents(c0);
        color1.getRGBComponents(c1);
        for (int i = 0; i < 4; ++i)
            c0[i] = (float)((1. - f)*c0[i] + f*c1[i]);
        return new Color(c0[0], c0[1], c0[2], c0[3]);
    }

    void startAnimation(Animation anim) {
        try {
            snapshot = anim.plan(arr, layout, snapshot);
        }
        catch (LinearSystemException e) {
            e.printStackTrace();
            return;
        }
        animation = anim;
        anim.start(this);
        animTimer.start();
    }

    void flip(Cell triangle) {
        if (FlipAnimation.canFlip(triangle))
            startAnimation(new FlipAnimation(triangle));
    }

    private class Mouser extends MouseInputAdapter {

        @Override public void mouseClicked(MouseEvent evnt) {
            if (animation != null) {
                // cancel running animation
                animation = null;
                repaint();
                return;
            }
            Point2D.Double pt = new Point2D.Double(evnt.getX() + .5,
                                                   evnt.getY() + .5);
            toUnitCircle(pt, pt);
            for (CellShape triangle: snapshot.triangles()) {
                if (triangle.getShape().contains(pt)) {
                    flip(triangle.cell);
                    return;
                }
            }
        }

    }

}
