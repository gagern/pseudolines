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

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CompoundEdit;

class HalfEdge {

    /**
     * The other half of this edge. This is the one originating from
     * the neighbouring point and directed towards our own center
     * point. If there is no neighbour in that direction, this value
     * will be <code>null</code>, and neither half of the segment
     * should be rendered under usual circumstances.
     */
    HalfEdge connection;

    /**
     * The segment in the opposite direction. This is a segment
     * originating at the same center, but pointing towards the
     * opposite neighbour. It forms the continuation of a pseudo
     * line. Must not be <code>null</code>.
     */
    HalfEdge opposite;

    /**
     * The next segment around the current center. The direction of
     * iteration is usually interpreted in a mathematical sense, so
     * this would be the next segment in counter-clockwise
     * direction. Forms a double-linked list together with
     * <code>prev</code>. Must not be <code>null</code>.
     * @see #prev
     */
    HalfEdge next;

    /**
     * The previous segment around the current center. The direction
     * of iteration is usually interpreted in a mathematical sense, so
     * this would be the next segment in clockwise direction. Forms a
     * double-linked list together with <code>next</code>. Must not be
     * <code>null</code>.
     * @see #next
     */
    HalfEdge prev;

    /**
     * The point from which this half segment originates. Must not be
     * <code>null</code>.
     */
    PointOnLine center;

    /**
     * The pseudo line this segment belongs to.
     */
    PseudoLine pseudoLine;

    /**
     * Sign indicating the direction of the edge on its pseudo line.
     * This will be +1 if the edge is a forward edge (i.e. leads away
     * from an intersection in the positive direction of the pseudo
     * line) or -1 otherwise.
     */
    byte directionSign;

    /**
     * Variable vector for the direction of the control point. Only
     * used by some layouts, so it may be <code>null</code>.
     */
    LinVec2 dir;

    /**
     * The x coordinate of the control point. This point will control
     * the cubic bezier curve for this segment.
     */
    double xCtrl;

    /**
     * The y coordinate of the control point. This point will control
     * the cubic bezier curve for this segment.
     */
    double yCtrl;

    /**
     * The index of this segment in the list of segments originating
     * at the center.
     */
    int index;

    boolean assertInvariants() {
        assert connection == null || connection.connection == this;
        assert opposite.opposite == this;
        assert next.prev == this;
        assert prev.next == this;
        assert center != null;
        assert prev.center == center;
        assert next.center == center;
        assert pseudoLine != null;
        assert opposite.pseudoLine == pseudoLine;
        assert index >= 0;
        assert prev.index == index - 1 || index == 0;
        assert next.index == index + 1 || next.index == 0;
        return true;
    }

    void disconnect() {
        if (connection != null) {
            assert connection.connection == this;
            connection.connection = null;
            connection = null;
        }
    }

    void connect(HalfEdge that) {
        this.disconnect();
        this.connection = that;
        if (that != null) {
            that.disconnect();
            that.connection = this;
            assert that.assertInvariants();
        }
        assert this.assertInvariants();
    }

    void connect(HalfEdge that, CompoundEdit edit) {
        edit.addEdit(new ConnectEdit(that));
        connect(that);
    }

    class ConnectEdit extends AbstractUndoableEdit {

        HalfEdge thisConnection;

        HalfEdge that;

        HalfEdge thatConnection;

        ConnectEdit(HalfEdge that) {
            thisConnection = connection;
            this.that = that;
            if (that == null)
                thatConnection = null;
            else
                thatConnection = that.connection;
        }

        @Override public void undo() {
            super.undo();
            connect(thisConnection);
            if (that != null)
                that.connect(thatConnection);
        }

        @Override public void redo() {
            super.redo();
            connect(that);
        }

        @Override public String getPresentationName() {
            return "Connection";
        }

    }

}
