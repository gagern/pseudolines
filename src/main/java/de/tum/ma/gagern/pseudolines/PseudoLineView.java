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
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import javax.swing.JComponent;
import javax.swing.event.MouseInputAdapter;

public class PseudoLineView
    extends JComponent
    implements PseudoLineRenderer
{

    private static final int RIM_SIZE = 20;

    private static final int MIN_TOTAL_SIZE = RIM_SIZE*2 + 10;

    private static final double THIN_WIDTH = 2.;

    private static final double THICK_WIDTH = 5.;

    private AffineTransform unitCircleTransform;

    private Arrangement arr;

    private Layout layout;

    private Snapshot snapshot;

    private Color triangleColor;

    private boolean fillRimCells;

    private Graphics2D g2d;

    private Stroke thinStroke;

    private Stroke thickStroke;

    public PseudoLineView() throws LinearSystemException {
        Chirotope chi = Catalog.getCatalog().get(0).getChirotope();
        arr = new Arrangement(chi, 0);
        layout = new RegularBezierLayout();
        snapshot = arr.snapshot(layout);
        addComponentListener(new ComponentAdapter() {
                @Override public void componentResized(ComponentEvent evnt) {
                    unitCircleTransform = null;
                }
            });
        Mouser mouser = new Mouser();
        addMouseListener(mouser);
        addMouseMotionListener(mouser);
        triangleColor = new Color(0xffaa00);
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
        if (w < MIN_TOTAL_SIZE || h < MIN_TOTAL_SIZE)
            return;
        g2d = (Graphics2D)g.create();
        g2d.translate(w/2., h/2.);
        double scale = (Math.min(w, h) - 2*RIM_SIZE)/2.;
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
        snapshot.render(this);
        g2d.dispose();
        g2d.dispose();
    }

    public void renderLine(PseudoLine pl, Shape shape) {
        g2d.setColor(pl.color);
        g2d.draw(shape);
    }

    public void renderCell(Cell cell, Shape shape) {
        if (cell.corners.size() == 3 &&
            (fillRimCells || !cell.isAtRim())) {
            g2d.setColor(triangleColor);
            g2d.fill(shape);
        }
    }

    void flip(CellShape triangle) {
        arr.flip(triangle.cell);
        try {
            snapshot = arr.snapshot(layout);
            repaint();
        }
        catch (LinearSystemException e) {
            e.printStackTrace();
            arr.flip(triangle.cell);
        }
    }

    private class Mouser extends MouseInputAdapter {

        @Override public void mouseClicked(MouseEvent evnt) {
            Point2D.Double pt = new Point2D.Double(evnt.getX() + .5,
                                                   evnt.getY() + .5);
            toUnitCircle(pt, pt);
            for (CellShape triangle: snapshot.triangles) {
                if (triangle.getShape().contains(pt)) {
                    flip(triangle);
                    return;
                }
            }
        }

    }

}
