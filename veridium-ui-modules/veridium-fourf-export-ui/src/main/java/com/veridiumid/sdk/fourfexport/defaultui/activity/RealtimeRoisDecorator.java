package com.veridiumid.sdk.fourfexport.defaultui.activity;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.veridiumid.sdk.imaging.SimpleCameraImagingSampler;
import com.veridiumid.sdk.support.ui.AspectRatioSafeFrameLayout;
import com.veridiumid.sdk.fourfexport.defaultui.R;
import com.veridiumid.sdk.imagingcore.CameraLayoutDecorator;

public class RealtimeRoisDecorator implements CameraLayoutDecorator, RoisRenderer, SurfaceHolder.Callback{
    private static final String LOG_TAG = RealtimeRoisDecorator.class.getName();

    private final AspectRatioSafeFrameLayout cameraLayout;
    private final Activity activity;
    private final boolean drawFocusArea = false;
    private SurfaceView roisSurface;
    private SurfaceHolder roisSurfaceHolder;
    private volatile boolean surfaceAvailable = false;

    public RealtimeRoisDecorator(Activity activity, AspectRatioSafeFrameLayout cameraLayout) {
        this.activity = activity;
        this.cameraLayout = cameraLayout;
    }

    @Override
    public void preAttachView() {
        LayoutInflater inflater = LayoutInflater.from(activity);
        roisSurface = (SurfaceView) inflater.inflate(R.layout.export_layout_component_roi_surface, cameraLayout, false);

        cameraLayout.addView(roisSurface);
        roisSurfaceHolder = roisSurface.getHolder();
        roisSurfaceHolder.addCallback(this);
        roisSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
    }

    @Override
    public void postAttachView() {
        roisSurface.setZOrderOnTop(true);
    }

    @Override
    public void preDetachView() {

    }

    @Override
    public void postDetachView() {

    }

    @Override
    public void onCameraReady(float fov) {
        //adjust hand guide
    }

    @Override
    public boolean decorate(Canvas canvas, int cameraWidth, int cameraHeight) {
        return false; //FIXME. what should this do?
    }

    public void update(RectF[] roisArr, int color) {
        try {
            if (roisArr != null && roisArr.length == 4) {
                renderRegionOfInterest(roisSurfaceHolder, roisArr, color);
            } else {
                clearRegionsOfInterest(roisSurfaceHolder);
            }
        } catch (Throwable ignored) {
            ignored.printStackTrace();
        }
    }

    @Override
    public void clear() {
        if (roisSurfaceHolder != null) {
            clearRegionsOfInterest(roisSurfaceHolder);
        }
    }

    private void clearRegionsOfInterest(SurfaceHolder surfaceHolder) {
        Canvas c = surfaceHolder.lockCanvas();

        if (c == null) {
            // Surface isn't available to write on, so just return immediately.
            return;
        }

        c.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        surfaceHolder.unlockCanvasAndPost(c);
        for (int i = 0; i < paths.length; i++) {
            paths[i] = null;
        }
//        focusPath = null;
    }

    private final Path[] paths = new Path[4];

    private Path focusPath = null;

    private void renderRegionOfInterest(SurfaceHolder surfaceHolder, RectF[] rois, int color) {
        if (!surfaceAvailable) {
            return;
        }

        if (surfaceHolder == null) {
            return;
        }

        Canvas c = surfaceHolder.lockCanvas();
        if (c == null) {
            // Surface isn't available to write on, so just return immediately.
            return;
        }

        Paint paint = new Paint();

        paint.setStyle(Paint.Style.FILL);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));

        c.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        float canvasHeight = c.getHeight();
        float canvasWidth = c.getWidth();

        Path path;
        paint.setColor(color);
        for (int i = 0; i <= 3; i++) {
            if (paths[i] == null) {
                paths[i] = new Path();
            }

            path = paths[i];

            RectF oval = new RectF(
                    rois[i].left * canvasWidth,
                    rois[i].top * canvasHeight,
                    rois[i].right * canvasWidth,
                    rois[i].bottom * canvasHeight
            );

            float horzScalingFactor = 1f;
            float vertScalingFactor = 1f;
            oval = resizeOvalByScalingFactors(oval, horzScalingFactor, vertScalingFactor);

            float r = oval.height() / 2;
            path.reset();

            path.addRoundRect(oval, r, r, Path.Direction.CW);
            c.drawPath(path, paint);
        }


        if (drawFocusArea && SimpleCameraImagingSampler.focus_area_debug!=null) {
            paint.setColor(Color.argb(150, 0, 0, 150));

            if (focusPath == null) {
                focusPath = new Path();
            }
            focusPath.reset();

            RectF drawScaledFocusRect = new RectF(
                    SimpleCameraImagingSampler.focus_area_debug.left * canvasWidth,//left
                    SimpleCameraImagingSampler.focus_area_debug.top * canvasHeight, //top
                    SimpleCameraImagingSampler.focus_area_debug.right * canvasWidth, //right
                    SimpleCameraImagingSampler.focus_area_debug.bottom * canvasHeight // bottom
            );

            focusPath.addRect(drawScaledFocusRect, Path.Direction.CW);
            c.drawPath(focusPath, paint);
        }

        surfaceHolder.unlockCanvasAndPost(c);
    }

    @NonNull
    private RectF resizeOvalByScalingFactors(RectF oval, float horzScalingFactor, float vertScalingFactor) {
        float horzSize = oval.right - oval.left;
        float horzCenter = (oval.left + oval.right)/2;

        float vertSize = oval.bottom - oval.top;
        float vertCenter = (oval.bottom + oval.top)/2;

        float newOvalLeft = horzCenter - 0.5f*horzScalingFactor*horzSize;
        float newOvalright = horzCenter + 0.5f*horzScalingFactor*horzSize;
        float newOvalBottom = vertCenter + 0.5f*vertScalingFactor*vertSize;
        float newOvalTop = vertCenter - 0.5f*vertScalingFactor*vertSize;

        oval = new RectF(newOvalLeft, newOvalTop, newOvalright, newOvalBottom);
        return oval;
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        roisSurfaceHolder = holder;
        holder.addCallback(this);
        surfaceAvailable = true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (roisSurfaceHolder != null) {
            roisSurfaceHolder.removeCallback(this);
        }
        roisSurfaceHolder = holder;
        holder.addCallback(this);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        surfaceAvailable = false;
    }
}
