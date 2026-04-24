/*
 * Copyright (c) 2017, 2022, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
package sun.java2d.marlin;

final class PathSimplifier implements DPathConsumer2D {

    // distance threshold in pixels (device)
    private static final double PIX_THRESHOLD = MarlinProperties.getPathSimplifierPixelTolerance();
    // squared tolerance in pixels
    private static final double SQUARE_TOLERANCE = PIX_THRESHOLD * PIX_THRESHOLD;

    // members:
    private DPathConsumer2D delegate;
    // current reference point
    private double cx, cy;
    // flag indicating if the given point was skipped
    private boolean skipped;
    // last skipped point
    private double sx, sy;

    PathSimplifier() {
    }

    PathSimplifier init(final DPathConsumer2D delegate) {
        this.delegate = delegate;
        skipped = false;
        return this; // fluent API
    }

    private void finishPath() {
        if (skipped) {
            _lineTo(sx, sy);
        }
    }

    @Override
    public void pathDone() {
        finishPath();
        delegate.pathDone();
    }

    @Override
    public void closePath() {
        finishPath();
        delegate.closePath();
    }

    @Override
    public void moveTo(final double xe, final double ye) {
        finishPath();
        delegate.moveTo(xe, ye);
        cx = xe;
        cy = ye;
    }

    @Override
    public void lineTo(final double xe, final double ye) {
        // Test if segment is too small:
        double dx = (xe - cx);
        double dy = (ye - cy);

        if ((dx * dx + dy * dy) <= SQUARE_TOLERANCE) {
            skipped = true;
            sx = xe;
            sy = ye;
            return;
        }
        _lineTo(xe, ye);
    }

    private void _lineTo(final double xe, final double ye) {
        delegate.lineTo(xe, ye);
        cx = xe;
        cy = ye;
        skipped = false;
    }

    @Override
    public void quadTo(final double x1, final double y1,
                       final double xe, final double ye)
    {
        // Test if curve is too small:
        double dx = (xe - cx);
        double dy = (ye - cy);

        if ((dx * dx + dy * dy) <= SQUARE_TOLERANCE) {
            // check control points P1:
            dx = (x1 - cx);
            dy = (y1 - cy);

            if ((dx * dx + dy * dy) <= SQUARE_TOLERANCE) {
                skipped = true;
                sx = xe;
                sy = ye;
                return;
            }
        }
        delegate.quadTo(x1, y1, xe, ye);
        cx = xe;
        cy = ye;
        skipped = false;
    }

    @Override
    public void curveTo(final double x1, final double y1,
                        final double x2, final double y2,
                        final double xe, final double ye)
    {
        // Test if curve is too small:
        double dx = (xe - cx);
        double dy = (ye - cy);

        if ((dx * dx + dy * dy) <= SQUARE_TOLERANCE) {
            // check control points P1:
            dx = (x1 - cx);
            dy = (y1 - cy);

            if ((dx * dx + dy * dy) <= SQUARE_TOLERANCE) {
                // check control points P2:
                dx = (x2 - cx);
                dy = (y2 - cy);

                if ((dx * dx + dy * dy) <= SQUARE_TOLERANCE) {
                    skipped = true;
                    sx = xe;
                    sy = ye;
                    return;
                }
            }
        }
        delegate.curveTo(x1, y1, x2, y2, xe, ye);
        cx = xe;
        cy = ye;
        skipped = false;
    }

    @Override
    public long getNativeConsumer() {
        return 0;
    }
}
