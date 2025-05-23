/*
 * Copyright (c) 2007, 2023, Oracle and/or its affiliates. All rights reserved.
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

import java.util.Arrays;
import sun.java2d.marlin.Helpers.PolyStack;
import sun.java2d.marlin.TransformingPathConsumer2D.CurveBasicMonotonizer;
import sun.java2d.marlin.TransformingPathConsumer2D.CurveClipSplitter;
import sun.java2d.marlin.TransformingPathConsumer2D.StartFlagPathConsumer2D;

// TODO: some of the arithmetic here is too verbose and prone to hard to
// debug typos. We should consider making a small Point/Vector class that
// has methods like plus(Point), minus(Point), dot(Point), cross(Point)and such
final class Stroker implements StartFlagPathConsumer2D, MarlinConst {

    private static final int MOVE_TO = 0;
    private static final int DRAWING_OP_TO = 1; // ie. curve, line, or quad
    private static final int CLOSE = 2;

    // join threshold = 1 subpixel (1/8th pixel):
    private static final double JOIN_ERROR = MarlinProperties.getStrokerJoinError();

    private static final double ROUND_JOIN_ERROR = 8.0 * JOIN_ERROR; // (8 h)

    private static final int JOIN_STYLE = MarlinProperties.getStrokerJoinStyle();

    // kappa = (4/3) * (SQRT(2) - 1)
    private static final double C = (4.0d * (Math.sqrt(2.0d) - 1.0d) / 3.0d);

    // SQRT(2)
    private static final double SQRT_2 = Math.sqrt(2.0d);

    // members:
    private DPathConsumer2D out;

    private int capStyle;
    private int joinStyle;

    private double lineWidth2;
    private double invHalfLineWidth2Sq;

    private final double[] offset0 = new double[2];
    private final double[] offset1 = new double[2];
    private final double[] offset2 = new double[2];
    private final double[] miter = new double[2];
    private double miterLimitSq;
    private double joinLimitMinSq;

    private int prev;

    // The starting point of the path, and the slope there.
    private double sx0, sy0, sdx, sdy;
    // the current point and the slope there.
    private double cx0, cy0, cdx, cdy; // c stands for current
    // vectors that when added to (sx0,sy0) and (cx0,cy0) respectively yield the
    // first and last points on the left parallel path. Since this path is
    // parallel, it's slope at any point is parallel to the slope of the
    // original path (thought they may have different directions), so these
    // could be computed from sdx,sdy and cdx,cdy (and vice versa), but that
    // would be error prone and hard to read, so we keep these anyway.
    private double smx, smy, cmx, cmy;

    private final PolyStack reverse;

    private final double[] lp = new double[8];
    private final double[] rp = new double[8];

    // per-thread renderer context
    final RendererContext rdrCtx;

    // dirty curve
    final Curve curve;

    // Bounds of the drawing region, at pixel precision.
    private double[] clipRect;

    // the outcode of the current point
    private int cOutCode = 0;

    // the outcode of the starting point
    private int sOutCode = 0;

    // flag indicating if the path is opened (clipped)
    private boolean opened = false;
    // flag indicating if the starting point's cap is done
    private boolean capStart = false;
    // flag indicating to monotonize curves
    private boolean monotonize;

    private boolean subdivide = false;
    private final CurveClipSplitter curveSplitter;

    /**
     * Constructs a <code>Stroker</code>.
     * @param rdrCtx per-thread renderer context
     */
    Stroker(final RendererContext rdrCtx) {
        this.rdrCtx = rdrCtx;

        this.reverse = (rdrCtx.stats != null) ?
            new PolyStack(rdrCtx,
                    rdrCtx.stats.stat_str_polystack_types,
                    rdrCtx.stats.stat_str_polystack_curves,
                    rdrCtx.stats.hist_str_polystack_curves,
                    rdrCtx.stats.stat_array_str_polystack_curves,
                    rdrCtx.stats.stat_array_str_polystack_types)
            : new PolyStack(rdrCtx);

        this.curve = rdrCtx.curve;
        this.curveSplitter = rdrCtx.curveClipSplitter;
    }

    /**
     * Inits the <code>Stroker</code>.
     *
     * @param pc2d an output <code>DPathConsumer2D</code>.
     * @param lineWidth the desired line width in pixels
     * @param capStyle the desired end cap style, one of
     * <code>CAP_BUTT</code>, <code>CAP_ROUND</code> or
     * <code>CAP_SQUARE</code>.
     * @param joinStyle the desired line join style, one of
     * <code>JOIN_MITER</code>, <code>JOIN_ROUND</code> or
     * <code>JOIN_BEVEL</code>.
     * @param miterLimit the desired miter limit
     * @param subdivideCurves true to indicate to subdivide curves, false if dasher does
     * @return this instance
     */
    Stroker init(final DPathConsumer2D pc2d,
                  final double lineWidth,
                  final int capStyle,
                  final int joinStyle,
                  final double miterLimit,
                  final boolean subdivideCurves)
    {
        this.out = pc2d;

        this.lineWidth2 = lineWidth / 2.0d;
        this.invHalfLineWidth2Sq = 1.0d / (2.0d * lineWidth2 * lineWidth2);
        this.monotonize = subdivideCurves;

        this.capStyle = capStyle;
        this.joinStyle = (JOIN_STYLE != -1) ? JOIN_STYLE : joinStyle;

        double miterScaledLimit = 0.0;

        if (joinStyle == JOIN_MITER) {
            miterScaledLimit = miterLimit * lineWidth2;
            this.miterLimitSq = miterScaledLimit * miterScaledLimit;

            if (rdrCtx.doRender) {
                final double limitMin = ((this.rdrCtx.clipInvScale == 0.0d) ? JOIN_ERROR
                        : (JOIN_ERROR * this.rdrCtx.clipInvScale))
                        + lineWidth2;

                this.joinLimitMinSq = limitMin * limitMin;
            } else {
                // createStrokedShape(): disable limit checks:
                this.joinLimitMinSq = 0.0;
            }
        } else if (joinStyle == JOIN_ROUND) {
            if (rdrCtx.doRender) {
                // chord:  s = 2 r * sin( phi / 2)
                // height: h = 2 r * sin( phi / 4)^2
                // small angles (phi < 90):
                // h = s^2 / (8 r)
                // so s^2 = (8 h * r)

                // height max (note ROUND_JOIN_ERROR = 8 * JOIN_ERROR)
                final double limitMin = ((this.rdrCtx.clipInvScale == 0.0d) ? ROUND_JOIN_ERROR
                        : (ROUND_JOIN_ERROR * this.rdrCtx.clipInvScale));

                // chord limit (s^2):
                this.joinLimitMinSq = limitMin * this.lineWidth2;
            } else {
                // createStrokedShape(): disable limit checks:
                this.joinLimitMinSq = 0.0;
            }
        }
        this.prev = CLOSE;

        rdrCtx.stroking = 1;

        if (rdrCtx.doClip) {
            // Adjust the clipping rectangle with the stroker margin (miter limit, width)
            double margin = lineWidth2;

            if (capStyle == CAP_SQUARE) {
                margin *= SQRT_2;
            }
            if ((joinStyle == JOIN_MITER) && (margin < miterScaledLimit)) {
                margin = miterScaledLimit;
            }

            // bounds as half-open intervals: minX <= x < maxX and minY <= y < maxY
            // adjust clip rectangle (ymin, ymax, xmin, xmax):
            final double[] _clipRect = rdrCtx.clipRect;
            _clipRect[0] -= margin;
            _clipRect[1] += margin;
            _clipRect[2] -= margin;
            _clipRect[3] += margin;
            this.clipRect = _clipRect;

            if (MarlinConst.DO_LOG_CLIP) {
                MarlinUtils.logInfo("clipRect (stroker): "
                                    + Arrays.toString(rdrCtx.clipRect));
            }

            // initialize curve splitter here for stroker & dasher:
            if (DO_CLIP_SUBDIVIDER) {
                subdivide = subdivideCurves;
                // adjust padded clip rectangle:
                curveSplitter.init();
            } else {
                subdivide = false;
            }
        } else {
            this.clipRect = null;
            this.cOutCode = 0;
            this.sOutCode = 0;
        }
        return this; // fluent API
    }

    void disableClipping() {
        this.clipRect = null;
        this.cOutCode = 0;
        this.sOutCode = 0;
    }

    /**
     * Disposes this stroker:
     * clean up before reusing this instance
     */
    void dispose() {
        reverse.dispose();

        opened   = false;
        capStart = false;

        if (DO_CLEAN_DIRTY) {
            // Force zero-fill dirty arrays:
            Arrays.fill(offset0, 0.0d);
            Arrays.fill(offset1, 0.0d);
            Arrays.fill(offset2, 0.0d);
            Arrays.fill(miter, 0.0d);
            Arrays.fill(lp, 0.0d);
            Arrays.fill(rp, 0.0d);
        }
    }

    private static void computeOffset(final double lx, final double ly,
                                      final double w, final double[] m)
    {
        double len = lx*lx + ly*ly;
        if (len == 0.0d) {
            m[0] = 0.0d;
            m[1] = 0.0d;
        } else {
            len = Math.sqrt(len);
            m[0] =  (ly * w) / len;
            m[1] = -(lx * w) / len;
        }
    }

    // Returns true if the vectors (dx1, dy1) and (dx2, dy2) are
    // clockwise (if dx1,dy1 needs to be rotated clockwise to close
    // the smallest angle between it and dx2,dy2).
    // This is equivalent to detecting whether a point q is on the right side
    // of a line passing through points p1, p2 where p2 = p1+(dx1,dy1) and
    // q = p2+(dx2,dy2), which is the same as saying p1, p2, q are in a
    // clockwise order.
    // NOTE: "clockwise" here assumes coordinates with 0,0 at the bottom left.
    private static boolean isCW(final double dx1, final double dy1,
                                final double dx2, final double dy2)
    {
        return dx1 * dy2 <= dy1 * dx2;
    }

    private void mayDrawRoundJoin(double cx, double cy,
                                  double omx, double omy,
                                  double mx, double my,
                                  boolean rev)
    {
        if ((omx == 0.0d && omy == 0.0d) || (mx == 0.0d && my == 0.0d)) {
            return;
        }
        final double domx = omx - mx;
        final double domy = omy - my;
        final double lenSq = domx * domx + domy * domy;

        if (lenSq < joinLimitMinSq) {
            return;
        }
        if (rev) {
            omx = -omx;
            omy = -omy;
            mx  = -mx;
            my  = -my;
        }
        drawRoundJoin(cx, cy, omx, omy, mx, my, rev);
    }

    private void drawRoundJoin(double cx, double cy,
                               double omx, double omy,
                               double mx, double my,
                               boolean rev)
    {
        // The sign of the dot product of mx,my and omx,omy is equal to the
        // the sign of the cosine of ext
        // (ext is the angle between omx,omy and mx,my).
        final double cosext = omx * mx + omy * my;
        // If it is >=0, we know that abs(ext) is <= 90 degrees, so we only
        // need 1 curve to approximate the circle section that joins omx,omy
        // and mx,my.
        if (cosext >= 0.0) {
            drawBezApproxForArc(cx, cy, omx, omy, mx, my, rev);
        } else {
            // we need to split the arc into 2 arcs spanning the same angle.
            // The point we want will be one of the 2 intersections of the
            // perpendicular bisector of the chord (omx,omy)->(mx,my) and the
            // circle. We could find this by scaling the vector
            // (omx+mx, omy+my)/2 so that it has length=lineWidth2 (and thus lies
            // on the circle), but that can have numerical problems when the angle
            // between omx,omy and mx,my is close to 180 degrees. So we compute a
            // normal of (omx,omy)-(mx,my). This will be the direction of the
            // perpendicular bisector. To get one of the intersections, we just scale
            // this vector that its length is lineWidth2 (this works because the
            // perpendicular bisector goes through the origin). This scaling doesn't
            // have numerical problems because we know that lineWidth2 divided by
            // this normal's length is at least 0.5 and at most sqrt(2)/2 (because
            // we know the angle of the arc is > 90 degrees).
            final double nx = my - omy;
            final double ny = omx - mx;
            final double nlen = Math.sqrt(nx * nx + ny * ny);
            final double scale = lineWidth2 / nlen;
            double mmx = nx * scale, mmy = ny * scale;

            // if (isCW(omx, omy, mx, my) != isCW(mmx, mmy, mx, my)) then we've
            // computed the wrong intersection so we get the other one.
            // The test above is equivalent to if (rev).
            if (rev) {
                mmx = -mmx;
                mmy = -mmy;
            }
            drawBezApproxForArc(cx, cy, omx, omy, mmx, mmy, rev);
            drawBezApproxForArc(cx, cy, mmx, mmy, mx, my, rev);
        }
    }

    // the input arc defined by omx,omy and mx,my must span <= 90 degrees.
    private void drawBezApproxForArc(final double cx, final double cy,
                                     final double omx, final double omy,
                                     final double mx, final double my,
                                     boolean rev)
    {
        final double cosext2 = (omx * mx + omy * my) * invHalfLineWidth2Sq;

        // check round off errors producing cos(ext) > 1 and a NaN below
        // cos(ext) == 1 implies colinear segments and an empty join anyway
        if (cosext2 >= 0.5d) {
            // just return to avoid generating a flat curve:
            return;
        }

        // cv is the length of P1-P0 and P2-P3 divided by the radius of the arc
        // (so, cv assumes the arc has radius 1). P0, P1, P2, P3 are the points that
        // define the bezier curve we're computing.
        // It is computed using the constraints that P1-P0 and P3-P2 are parallel
        // to the arc tangents at the endpoints, and that |P1-P0|=|P3-P2|.
        double cv = ((4.0d / 3.0d) * Math.sqrt(0.5d - cosext2) /
                            (1.0d + Math.sqrt(cosext2 + 0.5d)));
        // if clockwise, we need to negate cv.
        if (rev) { // rev is equivalent to isCW(omx, omy, mx, my)
            cv = -cv;
        }
        final double x1 = cx + omx;
        final double y1 = cy + omy;
        final double x2 = x1 - cv * omy;
        final double y2 = y1 + cv * omx;

        final double x4 = cx + mx;
        final double y4 = cy + my;
        final double x3 = x4 + cv * my;
        final double y3 = y4 - cv * mx;

        emitCurveTo(x1, y1, x2, y2, x3, y3, x4, y4, rev);
    }

    private void drawRoundCap(double cx, double cy, double mx, double my) {
        final double Cmx = C * mx;
        final double Cmy = C * my;
        emitCurveTo(cx + mx - Cmy, cy + my + Cmx,
                    cx - my + Cmx, cy + mx + Cmy,
                    cx - my,       cy + mx);
        emitCurveTo(cx - my - Cmx, cy + mx - Cmy,
                    cx - mx - Cmy, cy - my + Cmx,
                    cx - mx,       cy - my);
    }

    // Return the intersection point of the lines (x0, y0) -> (x1, y1)
    // and (x0p, y0p) -> (x1p, y1p) in m[off] and m[off+1]
    private static void computeMiter(final double x0, final double y0,
                                     final double x1, final double y1,
                                     final double x0p, final double y0p,
                                     final double x1p, final double y1p,
                                     final double[] m)
    {
        double x10 = x1 - x0;
        double y10 = y1 - y0;
        double x10p = x1p - x0p;
        double y10p = y1p - y0p;

        // if this is 0, the lines are parallel. If they go in the
        // same direction, there is no intersection so m[off] and
        // m[off+1] will contain infinity, so no miter will be drawn.
        // If they go in the same direction that means that the start of the
        // current segment and the end of the previous segment have the same
        // tangent, in which case this method won't even be involved in
        // miter drawing because it won't be called by drawMiter (because
        // (mx == omx && my == omy) will be true, and drawMiter will return
        // immediately).
        double den = x10*y10p - x10p*y10;
        double t = x10p*(y0-y0p) - y10p*(x0-x0p);
        t /= den;
        m[0] = x0 + t*x10;
        m[1] = y0 + t*y10;
    }

    // Return the intersection point of the lines (x0, y0) -> (x1, y1)
    // and (x0p, y0p) -> (x1p, y1p) in m[off] and m[off+1]
    private static void safeComputeMiter(final double x0, final double y0,
                                         final double x1, final double y1,
                                         final double x0p, final double y0p,
                                         final double x1p, final double y1p,
                                         final double[] m)
    {
        double x10 = x1 - x0;
        double y10 = y1 - y0;
        double x10p = x1p - x0p;
        double y10p = y1p - y0p;

        // if this is 0, the lines are parallel. If they go in the
        // same direction, there is no intersection so m[off] and
        // m[off+1] will contain infinity, so no miter will be drawn.
        // If they go in the same direction that means that the start of the
        // current segment and the end of the previous segment have the same
        // tangent, in which case this method won't even be involved in
        // miter drawing because it won't be called by drawMiter (because
        // (mx == omx && my == omy) will be true, and drawMiter will return
        // immediately).
        double den = x10*y10p - x10p*y10;
        if (den == 0.0d) {
            m[2] = (x0 + x0p) / 2.0d;
            m[3] = (y0 + y0p) / 2.0d;
        } else {
            double t = x10p*(y0-y0p) - y10p*(x0-x0p);
            t /= den;
            m[2] = x0 + t*x10;
            m[3] = y0 + t*y10;
        }
    }

    private void drawMiter(final double pdx, final double pdy,
                           final double x0, final double y0,
                           final double dx, final double dy,
                           double omx, double omy,
                           double mx, double my,
                           boolean rev)
    {
        if ((mx == omx && my == omy) ||
            (pdx == 0.0d && pdy == 0.0d) ||
            (dx == 0.0d && dy == 0.0d))
        {
            return;
        }

        if (rev) {
            omx = -omx;
            omy = -omy;
            mx  = -mx;
            my  = -my;
        }

        computeMiter((x0 - pdx) + omx, (y0 - pdy) + omy, x0 + omx, y0 + omy,
                     (dx + x0) + mx, (dy + y0) + my, x0 + mx, y0 + my, miter);

        final double miterX = miter[0];
        final double miterY = miter[1];
        final double lenSq = (miterX - x0) * (miterX - x0) + (miterY - y0) * (miterY - y0);

        // If the lines are parallel, lenSq will be either NaN or +inf
        // (actually, I'm not sure if the latter is possible. The important
        // thing is that -inf is not possible, because lenSq is a square).
        // For both of those values, the comparison below will fail and
        // no miter will be drawn, which is correct.
        if ((lenSq < miterLimitSq) && (lenSq >= joinLimitMinSq)) {
            emitLineTo(miterX, miterY, rev);
        }
    }

    @Override
    public void moveTo(final double x0, final double y0) {
        _moveTo(x0, y0, cOutCode);
        // update starting point:
        this.sx0 = x0;
        this.sy0 = y0;
        this.sdx = 1.0d;
        this.sdy = 0.0d;
        this.opened   = false;
        this.capStart = false;

        if (clipRect != null) {
            final int outcode = Helpers.outcode(x0, y0, clipRect);
            this.cOutCode = outcode;
            this.sOutCode = outcode;
        }
    }

    private void _moveTo(final double x0, final double y0,
                        final int outcode)
    {
        if (prev == MOVE_TO) {
            this.cx0 = x0;
            this.cy0 = y0;
        } else {
            if (prev == DRAWING_OP_TO) {
                finish(outcode);
            }
            this.prev = MOVE_TO;
            this.cx0 = x0;
            this.cy0 = y0;
            this.cdx = 1.0d;
            this.cdy = 0.0d;
        }
    }

    /* Callback from CurveClipSplitter */
    @Override
    public void setStartFlag(boolean first) {
        if (first) {
            // reset flag:
            rdrCtx.firstFlags &= 0b110;
        } else {
            rdrCtx.firstFlags |= 0b001;
        }
    }

    @Override
    public void lineTo(final double x1, final double y1) {
        final int outcode0 = this.cOutCode;

        if (clipRect != null) {
            final int outcode1 = Helpers.outcode(x1, y1, clipRect);

            // Should clip
            final int orCode = (outcode0 | outcode1);
            if (orCode != 0) {
                final int sideCode = outcode0 & outcode1;

                // basic rejection criteria:
                if (sideCode == 0) {
                    // overlap clip:
                    if (subdivide) {
                        // avoid reentrance
                        subdivide = false;
                        // subdivide curve => callback with subdivided parts:
                        boolean ret = curveSplitter.splitLine(cx0, cy0, x1, y1,
                                                              orCode, this);
                        // reentrance is done:
                        subdivide = true;
                        if (ret) {
                            return;
                        }
                    }
                    // already subdivided so render it
                } else {
                    this.cOutCode = outcode1;
                    _moveTo(x1, y1, outcode0);
                    opened = true;
                    return;
                }
            }
            this.cOutCode = outcode1;
        }

        double dx = x1 - cx0;
        double dy = y1 - cy0;
        if (dx == 0.0d && dy == 0.0d) {
            if (prev == DRAWING_OP_TO) {
                // skip repeated end-point
                return;
            }
            dx = 1.0d;
        }
        computeOffset(dx, dy, lineWidth2, offset0);
        final double mx = offset0[0];
        final double my = offset0[1];

        drawJoin(cdx, cdy, cx0, cy0, dx, dy, cmx, cmy, mx, my, outcode0);

        emitLineTo(cx0 + mx, cy0 + my);
        emitLineTo( x1 + mx,  y1 + my);

        emitLineToRev(cx0 - mx, cy0 - my);
        emitLineToRev( x1 - mx,  y1 - my);

        this.prev = DRAWING_OP_TO;
        this.cx0 = x1;
        this.cy0 = y1;
        this.cdx = dx;
        this.cdy = dy;
        this.cmx = mx;
        this.cmy = my;
    }

    @Override
    public void closePath() {
        // distinguish empty path at all vs opened path ?
        if (prev != DRAWING_OP_TO && !opened) {
            if (prev == CLOSE) {
                return;
            }
            emitMoveTo(cx0, cy0 - lineWidth2);

            this.sdx = 1.0d;
            this.sdy = 0.0d;
            this.cdx = 1.0d;
            this.cdy = 0.0d;

            this.smx = 0.0d;
            this.smy = -lineWidth2;
            this.cmx = 0.0d;
            this.cmy = -lineWidth2;

            finish(cOutCode);
            return;
        }

        // basic acceptance criteria
        if ((sOutCode & cOutCode) == 0) {
            if ((cx0 != sx0) || (cy0 != sy0)) {
                // may subdivide line:
                lineTo(sx0, sy0);
            }

            // ignore starting point outside:
            if (sOutCode == 0) {
                drawJoin(cdx, cdy, cx0, cy0, sdx, sdy, cmx, cmy, smx, smy, sOutCode);

                emitLineTo(sx0 + smx, sy0 + smy);

                if (opened) {
                    emitLineTo(sx0 - smx, sy0 - smy);
                } else {
                    emitMoveTo(sx0 - smx, sy0 - smy);
                }
            }
        }
        // Ignore caps like finish(false)
        emitReverse();

        this.prev = CLOSE;
        this.cx0 = sx0;
        this.cy0 = sy0;
        this.cOutCode = sOutCode;

        if (opened) {
            // do not emit close
            opened = false;
        } else {
            emitClose();
        }
    }

    private void emitReverse() {
        reverse.popAll(out);
    }

    @Override
    public void pathDone() {
        if (prev == DRAWING_OP_TO) {
            finish(cOutCode);
        }

        out.pathDone();

        // this shouldn't matter since this object won't be used
        // after the call to this method.
        this.prev = CLOSE;

        // Dispose this instance:
        dispose();
    }

    private void finish(final int outcode) {
        // Problem: impossible to guess if the path will be closed in advance
        //          i.e. if caps must be drawn or not ?
        // Solution: use the ClosedPathDetector before Stroker to determine
        // if the path is a closed path or not
        if (rdrCtx.closedPath) {
            emitReverse();
        } else {
            if (outcode == 0) {
                // current point = end's cap:
                if (capStyle == CAP_ROUND) {
                    drawRoundCap(cx0, cy0, cmx, cmy);
                } else if (capStyle == CAP_SQUARE) {
                    emitLineTo(cx0 - cmy + cmx, cy0 + cmx + cmy);
                    emitLineTo(cx0 - cmy - cmx, cy0 + cmx - cmy);
                }
            }
            emitReverse();

            if (!capStart) {
                capStart = true;

                if (sOutCode == 0) {
                    // starting point = initial cap:
                    if (capStyle == CAP_ROUND) {
                        drawRoundCap(sx0, sy0, -smx, -smy);
                    } else if (capStyle == CAP_SQUARE) {
                        emitLineTo(sx0 + smy - smx, sy0 - smx - smy);
                        emitLineTo(sx0 + smy + smx, sy0 - smx + smy);
                    }
                }
            }
        }
        emitClose();
    }

    private void emitMoveTo(final double x0, final double y0) {
        out.moveTo(x0, y0);
    }

    private void emitLineTo(final double x1, final double y1) {
        out.lineTo(x1, y1);
    }

    private void emitLineToRev(final double x1, final double y1) {
        reverse.pushLine(x1, y1);
    }

    private void emitLineTo(final double x1, final double y1,
                            final boolean rev)
    {
        if (rev) {
            emitLineToRev(x1, y1);
        } else {
            emitLineTo(x1, y1);
        }
    }

    private void emitQuadTo(final double x1, final double y1,
                            final double x2, final double y2)
    {
        out.quadTo(x1, y1, x2, y2);
    }

    private void emitQuadToRev(final double x0, final double y0,
                               final double x1, final double y1)
    {
        reverse.pushQuad(x0, y0, x1, y1);
    }

    private void emitCurveTo(final double x1, final double y1,
                             final double x2, final double y2,
                             final double x3, final double y3)
    {
        out.curveTo(x1, y1, x2, y2, x3, y3);
    }

    private void emitCurveToRev(final double x0, final double y0,
                                final double x1, final double y1,
                                final double x2, final double y2)
    {
        reverse.pushCubic(x0, y0, x1, y1, x2, y2);
    }

    private void emitCurveTo(final double x0, final double y0,
                             final double x1, final double y1,
                             final double x2, final double y2,
                             final double x3, final double y3, final boolean rev)
    {
        if (rev) {
            reverse.pushCubic(x0, y0, x1, y1, x2, y2);
        } else {
            out.curveTo(x1, y1, x2, y2, x3, y3);
        }
    }

    private void emitClose() {
        out.closePath();
    }

    private void drawJoin(double pdx, double pdy,
                          double x0, double y0,
                          double dx, double dy,
                          double omx, double omy,
                          double mx, double my,
                          final int outcode)
    {
        if (prev != DRAWING_OP_TO) {
            prev = DRAWING_OP_TO;
            emitMoveTo(x0 + mx, y0 + my);
            if (!opened) {
                this.sdx = dx;
                this.sdy = dy;
                this.smx = mx;
                this.smy = my;
            }
        } else if (rdrCtx.firstFlags == 0) {
            // Precision on isCW is causing instabilities with Dasher !
            final boolean cw = isCW(pdx, pdy, dx, dy);
            if (outcode == 0) {
                if (joinStyle == JOIN_MITER) {
                    drawMiter(pdx, pdy, x0, y0, dx, dy, omx, omy, mx, my, cw);
                } else if (joinStyle == JOIN_ROUND) {
                    mayDrawRoundJoin(x0, y0, omx, omy, mx, my, cw);
                }
            }
            emitLineTo(x0, y0, !cw);
        }
    }

    private int getLineOffsets(final double x1, final double y1,
                               final double x2, final double y2,
                               final double[] left, final double[] right)
    {
        computeOffset(x2 - x1, y2 - y1, lineWidth2, offset0);
        final double mx = offset0[0];
        final double my = offset0[1];
        left[0] = x1 + mx;
        left[1] = y1 + my;
        left[2] = x2 + mx;
        left[3] = y2 + my;

        right[0] = x1 - mx;
        right[1] = y1 - my;
        right[2] = x2 - mx;
        right[3] = y2 - my;

        return 4;
    }

    private int computeOffsetCubic(final double[] pts, final int off,
                                   final double[] leftOff,
                                   final double[] rightOff)
    {
        // if p1=p2 or p3=p4 it means that the derivative at the endpoint
        // vanishes, which creates problems with computeOffset. Usually
        // this happens when this stroker object is trying to widen
        // a curve with a cusp. What happens is that curveTo splits
        // the input curve at the cusp, and passes it to this function.
        // because of inaccuracies in the splitting, we consider points
        // equal if they're very close to each other.
        final double x1 = pts[off    ]; final double y1 = pts[off + 1];
        final double x2 = pts[off + 2]; final double y2 = pts[off + 3];
        final double x3 = pts[off + 4]; final double y3 = pts[off + 5];
        final double x4 = pts[off + 6]; final double y4 = pts[off + 7];

        double dx1 = x2 - x1; double dy1 = y2 - y1;
        double dx4 = x4 - x3; double dy4 = y4 - y3;

        // if p1 == p2 && p3 == p4: draw line from p1->p4, unless p1 == p4,
        // in which case ignore if p1 == p2
        final boolean p1eqp2 = Helpers.withinD(dx1, dy1, 6.0d * Math.ulp(y2));
        final boolean p3eqp4 = Helpers.withinD(dx4, dy4, 6.0d * Math.ulp(y4));

        if (p1eqp2 && p3eqp4) {
            return getLineOffsets(x1, y1, x4, y4, leftOff, rightOff);
        } else if (p1eqp2) {
            dx1 = x3 - x1;
            dy1 = y3 - y1;
        } else if (p3eqp4) {
            dx4 = x4 - x2;
            dy4 = y4 - y2;
        }

        // if p2-p1 and p4-p3 are parallel, that must mean this curve is a line
        double dotsq = (dx1 * dx4 + dy1 * dy4);
        dotsq *= dotsq;
        final double l1sq = dx1 * dx1 + dy1 * dy1;
        final double l4sq = dx4 * dx4 + dy4 * dy4;

        if (Helpers.within(dotsq, l1sq * l4sq, 4.0d * Math.ulp(dotsq))) {
            return getLineOffsets(x1, y1, x4, y4, leftOff, rightOff);
        }

//      What we're trying to do in this function is to approximate an ideal
//      offset curve (call it I) of the input curve B using a bezier curve Bp.
//      The constraints I use to get the equations are:
//
//      1. The computed curve Bp should go through I(0) and I(1). These are
//      x1p, y1p, x4p, y4p, which are p1p and p4p. We still need to find
//      4 variables: the x and y components of p2p and p3p (i.e. x2p, y2p, x3p, y3p).
//
//      2. Bp should have slope equal in absolute value to I at the endpoints. So,
//      (by the way, the operator || in the comments below means "aligned with".
//      It is defined on vectors, so when we say I'(0) || Bp'(0) we mean that
//      vectors I'(0) and Bp'(0) are aligned, which is the same as saying
//      that the tangent lines of I and Bp at 0 are parallel. Mathematically
//      this means (I'(t) || Bp'(t)) <==> (I'(t) = c * Bp'(t)) where c is some
//      nonzero constant.)
//      I'(0) || Bp'(0) and I'(1) || Bp'(1). Obviously, I'(0) || B'(0) and
//      I'(1) || B'(1); therefore, Bp'(0) || B'(0) and Bp'(1) || B'(1).
//      We know that Bp'(0) || (p2p-p1p) and Bp'(1) || (p4p-p3p) and the same
//      is true for any bezier curve; therefore, we get the equations
//          (1) p2p = c1 * (p2-p1) + p1p
//          (2) p3p = c2 * (p4-p3) + p4p
//      We know p1p, p4p, p2, p1, p3, and p4; therefore, this reduces the number
//      of unknowns from 4 to 2 (i.e. just c1 and c2).
//      To eliminate these 2 unknowns we use the following constraint:
//
//      3. Bp(0.5) == I(0.5). Bp(0.5)=(x,y) and I(0.5)=(xi,yi), and I should note
//      that I(0.5) is *the only* reason for computing dxm,dym. This gives us
//          (3) Bp(0.5) = (p1p + 3 * (p2p + p3p) + p4p)/8, which is equivalent to
//          (4) p2p + p3p = (Bp(0.5)*8 - p1p - p4p) / 3
//      We can substitute (1) and (2) from above into (4) and we get:
//          (5) c1*(p2-p1) + c2*(p4-p3) = (Bp(0.5)*8 - p1p - p4p)/3 - p1p - p4p
//      which is equivalent to
//          (6) c1*(p2-p1) + c2*(p4-p3) = (4/3) * (Bp(0.5) * 2 - p1p - p4p)
//
//      The right side of this is a 2D vector, and we know I(0.5), which gives us
//      Bp(0.5), which gives us the value of the right side.
//      The left side is just a matrix vector multiplication in disguise. It is
//
//      [x2-x1, x4-x3][c1]
//      [y2-y1, y4-y3][c2]
//      which, is equal to
//      [dx1, dx4][c1]
//      [dy1, dy4][c2]
//      At this point we are left with a simple linear system and we solve it by
//      getting the inverse of the matrix above. Then we use [c1,c2] to compute
//      p2p and p3p.

        final double xm = (x1 + x4 + 3.0d * (x2 + x3)) / 8.0d;
        final double ym = (y1 + y4 + 3.0d * (y2 + y3)) / 8.0d;
        // (dxm,dym) is some tangent of B at t=0.5. This means it's equal to
        // c*B'(0.5) for some constant c.
        final double dxm = x3 + x4 - (x1 + x2);
        final double dym = y3 + y4 - (y1 + y2);

        // this computes the offsets at t=0, 0.5, 1, using the property that
        // for any bezier curve the vectors p2-p1 and p4-p3 are parallel to
        // the (dx/dt, dy/dt) vectors at the endpoints.
        computeOffset(dx1, dy1, lineWidth2, offset0);
        computeOffset(dxm, dym, lineWidth2, offset1);
        computeOffset(dx4, dy4, lineWidth2, offset2);

        // left side:
        double x1p = x1 + offset0[0]; // start
        double y1p = y1 + offset0[1]; // point
        double xi  = xm + offset1[0]; // interpolation
        double yi  = ym + offset1[1]; // point
        double x4p = x4 + offset2[0]; // end
        double y4p = y4 + offset2[1]; // point

        final double invdet43 = 4.0d / (3.0d * (dx1 * dy4 - dy1 * dx4));

        double two_pi_m_p1_m_p4x = 2.0d * xi - (x1p + x4p);
        double two_pi_m_p1_m_p4y = 2.0d * yi - (y1p + y4p);

        double c1 = invdet43 * (dy4 * two_pi_m_p1_m_p4x - dx4 * two_pi_m_p1_m_p4y);
        double c2 = invdet43 * (dx1 * two_pi_m_p1_m_p4y - dy1 * two_pi_m_p1_m_p4x);

        double x2p, y2p, x3p, y3p;

        if (c1 * c2 > 0.0) {
//            System.out.println("Buggy solver (left): c1 = " + c1 + " c2 = " + c2);

            // use lower quality approximation but good enough
            // to ensure cuve being in its convex hull
            x2p = x2 + offset1[0]; // 2nd
            y2p = y2 + offset1[1]; // point
            x3p = x3 + offset1[0]; // 3nd
            y3p = y3 + offset1[1]; // point

            safeComputeMiter(x1p, y1p, x1p+dx1, y1p+dy1, x2p, y2p, x2p-dxm, y2p-dym, leftOff);
            x2p = leftOff[2]; y2p = leftOff[3];

            safeComputeMiter(x4p, y4p, x4p+dx4, y4p+dy4, x3p, y3p, x3p-dxm, y3p-dym, leftOff);
            x3p = leftOff[2]; y3p = leftOff[3];
        } else {
            x2p = x1p + c1 * dx1; y2p = y1p + c1 * dy1;
            x3p = x4p + c2 * dx4; y3p = y4p + c2 * dy4;
        }

        leftOff[0] = x1p; leftOff[1] = y1p;
        leftOff[2] = x2p; leftOff[3] = y2p;
        leftOff[4] = x3p; leftOff[5] = y3p;
        leftOff[6] = x4p; leftOff[7] = y4p;

        // Right side:
        x1p = x1 - offset0[0]; // start
        y1p = y1 - offset0[1]; // point
        xi =  xm  - offset1[0]; // interpolation
        yi =  ym  - offset1[1]; // point
        x4p = x4 - offset2[0]; // end
        y4p = y4 - offset2[1]; // point

        two_pi_m_p1_m_p4x = 2.0d * xi - (x1p + x4p);
        two_pi_m_p1_m_p4y = 2.0d * yi - (y1p + y4p);

        c1 = invdet43 * (dy4 * two_pi_m_p1_m_p4x - dx4 * two_pi_m_p1_m_p4y);
        c2 = invdet43 * (dx1 * two_pi_m_p1_m_p4y - dy1 * two_pi_m_p1_m_p4x);

        if (c1 * c2 > 0.0) {
//            System.out.println("Buggy solver (right): c1 = " + c1 + " c2 = " + c2);

            // use lower quality approximation but good enough
            // to ensure cuve being in its convex hull
            x2p = x2 - offset1[0]; // 2nd
            y2p = y2 - offset1[1]; // point
            x3p = x3 - offset1[0]; // 3nd
            y3p = y3 - offset1[1]; // point

            safeComputeMiter(x1p, y1p, x1p+dx1, y1p+dy1, x2p, y2p, x2p-dxm, y2p-dym, rightOff);
            x2p = rightOff[2]; y2p = rightOff[3];

            safeComputeMiter(x4p, y4p, x4p+dx4, y4p+dy4, x3p, y3p, x3p-dxm, y3p-dym, rightOff);
            x3p = rightOff[2]; y3p = rightOff[3];
        } else {
            x2p = x1p + c1 * dx1; y2p = y1p + c1 * dy1;
            x3p = x4p + c2 * dx4; y3p = y4p + c2 * dy4;
        }

        rightOff[0] = x1p; rightOff[1] = y1p;
        rightOff[2] = x2p; rightOff[3] = y2p;
        rightOff[4] = x3p; rightOff[5] = y3p;
        rightOff[6] = x4p; rightOff[7] = y4p;

        return 8;
    }

    // compute offset curves using bezier spline through t=0.5 (i.e.
    // ComputedCurve(0.5) == IdealParallelCurve(0.5))
    // return the kind of curve in the right and left arrays.
    private int computeOffsetQuad(final double[] pts, final int off,
                                  final double[] leftOff,
                                  final double[] rightOff)
    {
        final double x1 = pts[off    ]; final double y1 = pts[off + 1];
        final double x2 = pts[off + 2]; final double y2 = pts[off + 3];
        final double x3 = pts[off + 4]; final double y3 = pts[off + 5];

        final double dx12 = x2 - x1; final double dy12 = y2 - y1;
        final double dx23 = x3 - x2; final double dy23 = y3 - y2;

        // if p1=p2 or p2=p3 it means that the derivative at the endpoint
        // vanishes, which creates problems with computeOffset. Usually
        // this happens when this stroker object is trying to widen
        // a curve with a cusp. What happens is that curveTo splits
        // the input curve at the cusp, and passes it to this function.
        // because of inaccuracies in the splitting, we consider points
        // equal if they're very close to each other.

        // if p1 == p2 or p2 == p3: draw line from p1->p3
        final boolean p1eqp2 = Helpers.withinD(dx12, dy12, 6.0d * Math.ulp(y2));
        final boolean p2eqp3 = Helpers.withinD(dx23, dy23, 6.0d * Math.ulp(y3));

        if (p1eqp2 || p2eqp3) {
            return getLineOffsets(x1, y1, x3, y3, leftOff, rightOff);
        }

        // if p2-p1 and p3-p2 are parallel, that must mean this curve is a line
        double dotsq = (dx12 * dx23 + dy12 * dy23);
        dotsq *= dotsq;
        final double l1sq = dx12 * dx12 + dy12 * dy12;
        final double l3sq = dx23 * dx23 + dy23 * dy23;

        if (Helpers.within(dotsq, l1sq * l3sq, 4.0d * Math.ulp(dotsq))) {
            return getLineOffsets(x1, y1, x3, y3, leftOff, rightOff);
        }

        // this computes the offsets at t=0, 0.5, 1, using the property that
        // for any bezier curve the vectors p2-p1 and p3-p2 are parallel to
        // the (dx/dt, dy/dt) vectors at the endpoints.
        computeOffset(dx12, dy12, lineWidth2, offset0);
        computeOffset(dx23, dy23, lineWidth2, offset1);

        double x1p = x1 + offset0[0]; // start
        double y1p = y1 + offset0[1]; // point
        double x3p = x3 + offset1[0]; // end
        double y3p = y3 + offset1[1]; // point

        safeComputeMiter(x1p, y1p, x1p+dx12, y1p+dy12, x3p, y3p, x3p-dx23, y3p-dy23, leftOff);
        leftOff[0] = x1p; leftOff[1] = y1p;
        leftOff[4] = x3p; leftOff[5] = y3p;

        x1p = x1 - offset0[0]; // start
        y1p = y1 - offset0[1]; // point
        x3p = x3 - offset1[0]; // end
        y3p = y3 - offset1[1]; // point

        safeComputeMiter(x1p, y1p, x1p+dx12, y1p+dy12, x3p, y3p, x3p-dx23, y3p-dy23, rightOff);
        rightOff[0] = x1p; rightOff[1] = y1p;
        rightOff[4] = x3p; rightOff[5] = y3p;

        return 6;
    }

    @Override
    public void curveTo(final double x1, final double y1,
                        final double x2, final double y2,
                        final double x3, final double y3)
    {
        final int outcode0 = this.cOutCode;

        if (clipRect != null) {
            final int outcode1 = Helpers.outcode(x1, y1, clipRect);
            final int outcode2 = Helpers.outcode(x2, y2, clipRect);
            final int outcode3 = Helpers.outcode(x3, y3, clipRect);

            // Should clip
            final int orCode = (outcode0 | outcode1 | outcode2 | outcode3);
            if (orCode != 0) {
                final int sideCode = outcode0 & outcode1 & outcode2 & outcode3;

                // basic rejection criteria:
                if (sideCode == 0) {
                    // overlap clip:
                    if (subdivide) {
                        // avoid reentrance
                        subdivide = false;
                        // subdivide curve => callback with subdivided parts:
                        boolean ret = curveSplitter.splitCurve(cx0, cy0, x1, y1,
                                                               x2, y2, x3, y3,
                                                               orCode, this);
                        // reentrance is done:
                        subdivide = true;
                        if (ret) {
                            return;
                        }
                    }
                    // already subdivided so render it
                } else {
                    this.cOutCode = outcode3;
                    _moveTo(x3, y3, outcode0);
                    opened = true;
                    return;
                }
            }
            this.cOutCode = outcode3;
        }
        _curveTo(x1, y1, x2, y2, x3, y3, outcode0);
    }

    private void _curveTo(final double x1, final double y1,
                          final double x2, final double y2,
                          final double x3, final double y3,
                          final int outcode0)
    {
        // need these so we can update the state at the end of this method
        double dxs = x1 - cx0;
        double dys = y1 - cy0;
        double dxf = x3 - x2;
        double dyf = y3 - y2;

        if ((dxs == 0.0d) && (dys == 0.0d)) {
            dxs = x2 - cx0;
            dys = y2 - cy0;
            if ((dxs == 0.0d) && (dys == 0.0d)) {
                dxs = x3 - cx0;
                dys = y3 - cy0;
            }
        }
        if ((dxf == 0.0d) && (dyf == 0.0d)) {
            dxf = x3 - x1;
            dyf = y3 - y1;
            if ((dxf == 0.0d) && (dyf == 0.0d)) {
                dxf = x3 - cx0;
                dyf = y3 - cy0;
            }
        }
        if ((dxs == 0.0d) && (dys == 0.0d)) {
            // this happens if the "curve" is just a point
            // fix outcode0 for lineTo() call:
            if (clipRect != null) {
                this.cOutCode = outcode0;
            }
            lineTo(cx0, cy0);
            return;
        }

        // if these vectors are too small, normalize them, to avoid future
        // precision problems.
        if (Math.abs(dxs) < 0.1d && Math.abs(dys) < 0.1d) {
            final double len = Math.sqrt(dxs * dxs + dys * dys);
            dxs /= len;
            dys /= len;
        }
        if (Math.abs(dxf) < 0.1d && Math.abs(dyf) < 0.1d) {
            final double len = Math.sqrt(dxf * dxf + dyf * dyf);
            dxf /= len;
            dyf /= len;
        }

        computeOffset(dxs, dys, lineWidth2, offset0);
        drawJoin(cdx, cdy, cx0, cy0, dxs, dys, cmx, cmy, offset0[0], offset0[1], outcode0);

        int nSplits = 0;
        final double[] mid;
        final double[] l = lp;

        if (monotonize) {
            // monotonize curve:
            final CurveBasicMonotonizer monotonizer
                = rdrCtx.monotonizer.curve(cx0, cy0, x1, y1, x2, y2, x3, y3);

            nSplits = monotonizer.nbSplits;
            mid = monotonizer.middle;
        } else {
            // use left instead:
            mid = l;
            mid[0] = cx0; mid[1] = cy0;
            mid[2] = x1;  mid[3] = y1;
            mid[4] = x2;  mid[5] = y2;
            mid[6] = x3;  mid[7] = y3;
        }
        final double[] r = rp;

        int kind = 0;
        for (int i = 0, off = 0; i <= nSplits; i++, off += 6) {
            kind = computeOffsetCubic(mid, off, l, r);

            emitLineTo(l[0], l[1]);

            switch(kind) {
            case 8:
                emitCurveTo(l[2], l[3], l[4], l[5], l[6], l[7]);
                emitCurveToRev(r[0], r[1], r[2], r[3], r[4], r[5]);
                break;
            case 4:
                emitLineTo(l[2], l[3]);
                emitLineToRev(r[0], r[1]);
                break;
            default:
            }
            emitLineToRev(r[kind - 2], r[kind - 1]);
        }

        this.prev = DRAWING_OP_TO;
        this.cx0 = x3;
        this.cy0 = y3;
        this.cdx = dxf;
        this.cdy = dyf;
        this.cmx = (l[kind - 2] - r[kind - 2]) / 2.0d;
        this.cmy = (l[kind - 1] - r[kind - 1]) / 2.0d;
    }

    @Override
    public void quadTo(final double x1, final double y1,
                       final double x2, final double y2)
    {
        final int outcode0 = this.cOutCode;

        if (clipRect != null) {
            final int outcode1 = Helpers.outcode(x1, y1, clipRect);
            final int outcode2 = Helpers.outcode(x2, y2, clipRect);

            // Should clip
            final int orCode = (outcode0 | outcode1 | outcode2);
            if (orCode != 0) {
                final int sideCode = outcode0 & outcode1 & outcode2;

                // basic rejection criteria:
                if (sideCode == 0) {
                    // overlap clip:
                    if (subdivide) {
                        // avoid reentrance
                        subdivide = false;
                        // subdivide curve => call lineTo() with subdivided curves:
                        boolean ret = curveSplitter.splitQuad(cx0, cy0, x1, y1,
                                                              x2, y2, orCode, this);
                        // reentrance is done:
                        subdivide = true;
                        if (ret) {
                            return;
                        }
                    }
                    // already subdivided so render it
                } else {
                    this.cOutCode = outcode2;
                    _moveTo(x2, y2, outcode0);
                    opened = true;
                    return;
                }
            }
            this.cOutCode = outcode2;
        }
        _quadTo(x1, y1, x2, y2, outcode0);
    }

    private void _quadTo(final double x1, final double y1,
                         final double x2, final double y2,
                         final int outcode0)
    {
        // need these so we can update the state at the end of this method
        double dxs = x1 - cx0;
        double dys = y1 - cy0;
        double dxf = x2 - x1;
        double dyf = y2 - y1;

        if (((dxs == 0.0d) && (dys == 0.0d)) || ((dxf == 0.0d) && (dyf == 0.0d))) {
            dxs = dxf = x2 - cx0;
            dys = dyf = y2 - cy0;
        }
        if ((dxs == 0.0d) && (dys == 0.0d)) {
            // this happens if the "curve" is just a point
            // fix outcode0 for lineTo() call:
            if (clipRect != null) {
                this.cOutCode = outcode0;
            }
            lineTo(cx0, cy0);
            return;
        }
        // if these vectors are too small, normalize them, to avoid future
        // precision problems.
        if (Math.abs(dxs) < 0.1d && Math.abs(dys) < 0.1d) {
            final double len = Math.sqrt(dxs * dxs + dys * dys);
            dxs /= len;
            dys /= len;
        }
        if (Math.abs(dxf) < 0.1d && Math.abs(dyf) < 0.1d) {
            final double len = Math.sqrt(dxf * dxf + dyf * dyf);
            dxf /= len;
            dyf /= len;
        }
        computeOffset(dxs, dys, lineWidth2, offset0);
        drawJoin(cdx, cdy, cx0, cy0, dxs, dys, cmx, cmy, offset0[0], offset0[1], outcode0);

        int nSplits = 0;
        final double[] mid;
        final double[] l = lp;

        if (monotonize) {
            // monotonize quad:
            final CurveBasicMonotonizer monotonizer
                = rdrCtx.monotonizer.quad(cx0, cy0, x1, y1, x2, y2);

            nSplits = monotonizer.nbSplits;
            mid = monotonizer.middle;
        } else {
            // use left instead:
            mid = l;
            mid[0] = cx0; mid[1] = cy0;
            mid[2] = x1;  mid[3] = y1;
            mid[4] = x2;  mid[5] = y2;
        }
        final double[] r = rp;

        int kind = 0;
        for (int i = 0, off = 0; i <= nSplits; i++, off += 4) {
            kind = computeOffsetQuad(mid, off, l, r);

            emitLineTo(l[0], l[1]);

            switch(kind) {
            case 6:
                emitQuadTo(l[2], l[3], l[4], l[5]);
                emitQuadToRev(r[0], r[1], r[2], r[3]);
                break;
            case 4:
                emitLineTo(l[2], l[3]);
                emitLineToRev(r[0], r[1]);
                break;
            default:
            }
            emitLineToRev(r[kind - 2], r[kind - 1]);
        }

        this.prev = DRAWING_OP_TO;
        this.cx0 = x2;
        this.cy0 = y2;
        this.cdx = dxf;
        this.cdy = dyf;
        this.cmx = (l[kind - 2] - r[kind - 2]) / 2.0d;
        this.cmy = (l[kind - 1] - r[kind - 1]) / 2.0d;
    }

    @Override public long getNativeConsumer() {
        throw new InternalError("Stroker doesn't use a native consumer");
    }
}
