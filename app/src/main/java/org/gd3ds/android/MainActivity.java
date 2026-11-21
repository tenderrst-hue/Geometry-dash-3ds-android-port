package org.gd3ds.android;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.*;
import android.view.*;
import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 * First Android presentation/input shell for the GD3DS port.
 * The two logical 3DS screens are stacked vertically. This is a UI prototype;
 * original game simulation/rendering has not yet been ported from citro2d.
 */
public final class MainActivity extends Activity {
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setContentView(new DualScreenView(this));
    }

    static final class DualScreenView extends View implements Choreographer.FrameCallback {
        private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Choreographer choreographer = Choreographer.getInstance();
        private long lastFrame;
        private float elapsed, cubeY, cubeV;
        private boolean pressed;
        private float progress = 0.18f;
        private int frames;
        private long fpsStart;

        DualScreenView(Context c) { super(c); setLayerType(View.LAYER_TYPE_HARDWARE, null); }

        @Override protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            choreographer.postFrameCallback(this);
        }
        @Override protected void onDetachedFromWindow() {
            choreographer.removeFrameCallback(this);
            super.onDetachedFromWindow();
        }
        @Override public void doFrame(long time) {
            if (lastFrame != 0) {
                float dt = Math.min((time - lastFrame) / 1_000_000_000f, 0.033f);
                elapsed += dt;
                if (pressed) cubeV = -540f;
                cubeV += 1450f * dt;
                cubeY += cubeV * dt;
                if (cubeY > 0) { cubeY = 0; cubeV = 0; }
                if (pressed) progress = Math.min(0.99f, progress + dt * 0.035f);
            }
            lastFrame = time;
            frames++;
            if (time - fpsStart > 1_000_000_000L) { fpsStart = time; frames = 0; }
            invalidate();
            choreographer.postFrameCallback(this);
        }

        @Override protected void onDraw(Canvas c) {
            super.onDraw(c);
            float w = getWidth(), h = getHeight();
            float split = h * 0.61f;
            drawGameplay(c, w, split);
            drawLowerScreen(c, w, h - split, split);
            p.setColor(Color.rgb(76, 225, 255)); p.setStrokeWidth(3);
            c.drawLine(0, split, w, split, p);
        }

        private void drawGameplay(Canvas c, float w, float h) {
            c.save();
            c.clipRect(0, 0, w, h);
            p.setShader(new LinearGradient(0, 0, 0, h, Color.rgb(8,18,48), Color.rgb(22,12,58), Shader.TileMode.CLAMP));
            c.drawRect(0, 0, w, h, p); p.setShader(null);
            // Neon grid and horizon
            p.setColor(0x3348dfff); p.setStrokeWidth(1);
            for (int x=0; x<w; x+=36) c.drawLine(x, 0, x, h, p);
            for (int y=18; y<h; y+=36) c.drawLine(0, y, w, y, p);
            float ground = h * .79f;
            p.setColor(Color.rgb(30, 225, 245)); p.setStrokeWidth(4);
            c.drawLine(0, ground, w, ground, p);
            // Platforms and spikes
            p.setColor(Color.rgb(25, 115, 180));
            c.drawRect(w*.56f, ground-26, w*.69f, ground, p);
            p.setColor(Color.rgb(255, 80, 170));
            Path spike = new Path(); spike.moveTo(w*.74f,ground); spike.lineTo(w*.79f,ground-42); spike.lineTo(w*.84f,ground); spike.close(); c.drawPath(spike,p);
            // Player cube
            float size = Math.min(w,h)*.095f, cx=w*.22f, cy=ground-size+cubeY;
            c.save(); c.rotate((float)(elapsed*180), cx+size/2, cy+size/2);
            p.setColor(Color.rgb(30, 235, 255)); c.drawRoundRect(cx,cy,cx+size,cy+size,5,5,p);
            p.setColor(Color.WHITE); p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(3); c.drawRect(cx+5,cy+5,cx+size-5,cy+size-5,p); p.setStyle(Paint.Style.FILL);
            c.restore();
            // Progress strip
            p.setColor(0x88555566); c.drawRoundRect(w*.08f, h*.08f, w*.92f, h*.08f+8, 8,8,p);
            p.setColor(Color.rgb(50,235,210)); c.drawRoundRect(w*.08f,h*.08f,w*(.08f+.84f*progress),h*.08f+8,8,8,p);
            text(c, "GAMEPLAY SCREEN", 16, 28, 11, Color.WHITE);
            text(c, "ANDROID PORT • INPUT TEST", 16, 44, 8, 0xff8bdcff);
            c.restore();
        }

        private void drawLowerScreen(Canvas c, float w, float h, float top) {
            c.save(); c.translate(0, top);
            p.setShader(new LinearGradient(0,0,0,h,0xff10182d,0xff080d1b,Shader.TileMode.CLAMP));
            c.drawRect(0,0,w,h,p); p.setShader(null);
            text(c, "SECONDARY 3DS SCREEN", 16, 28, 11, Color.WHITE);
            text(c, "TOUCH THE TOP SCREEN TO JUMP • HOLD TO KEEP INPUT ACTIVE", 16, 47, 8, 0xff91a9c8);
            float cy = h*.43f, r = Math.min(w*.105f, h*.18f);
            String[] labels = {"ICON", "SHOP", "STATS", "SETTINGS"};
            for (int i=0;i<4;i++) {
                float x = w*(.14f + i*.24f);
                p.setColor(i==0?0xff1bc9e8:0xff293c65);
                c.drawRoundRect(x-r,cy-r,x+r,cy+r,14,14,p);
                p.setColor(0xffd9f8ff); p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(2);
                c.drawRoundRect(x-r+5,cy-r+5,x+r-5,cy+r-5,10,10,p); p.setStyle(Paint.Style.FILL);
                text(c,labels[i],x,cy+r+19,8,0xffd7e5ff,true);
            }
            p.setColor(0xff34415d); c.drawRoundRect(w*.08f,h*.78f,w*.92f,h*.78f+7,6,6,p);
            p.setColor(0xff26d5e8); c.drawRoundRect(w*.08f,h*.78f,w*.92f,h*.78f+7,6,6,p);
            text(c, "GD3DS  •  DUAL DISPLAY LAYOUT", w/2, h*.94f, 9, 0xff8ba5c8, true);
            c.restore();
        }

        private void text(Canvas c, String s, float x, float y, float size, int color) { text(c,s,x,y,size,color,false); }
        private void text(Canvas c, String s, float x, float y, float size, int color, boolean center) {
            p.setColor(color); p.setTextSize(size*getResources().getDisplayMetrics().scaledDensity);
            p.setTypeface(Typeface.create("sans-serif",Typeface.BOLD));
            p.setTextAlign(center?Paint.Align.CENTER:Paint.Align.LEFT); c.drawText(s,x,y,p);
        }

        @Override public boolean onTouchEvent(android.view.MotionEvent e) {
            float split = getHeight()*.61f;
            if (e.getY() <= split) {
                if (e.getActionMasked()==MotionEvent.ACTION_DOWN || e.getActionMasked()==MotionEvent.ACTION_MOVE) pressed=true;
                if (e.getActionMasked()==MotionEvent.ACTION_UP || e.getActionMasked()==MotionEvent.ACTION_CANCEL) pressed=false;
                return true;
            }
            if (e.getActionMasked()==MotionEvent.ACTION_DOWN || e.getActionMasked()==MotionEvent.ACTION_UP) return true;
            return true;
        }
    }
}
