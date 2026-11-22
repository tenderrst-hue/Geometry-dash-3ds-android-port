package com.chickengun.android;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.*;
import android.view.*;
import android.content.Context;
import android.os.Handler;

/**
 * Android adaptation of the Chicken Gun PS2 menu prototype.
 * The supplied PS2 source contains menu/UI code only; gameplay is not present.
 */
public final class MainActivity extends Activity {
    private GameView gameView;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        gameView = new GameView(this);
        setContentView(gameView);
    }

    @Override public void onBackPressed() {
        if (gameView != null) gameView.back();
        else super.onBackPressed();
    }

    static final class GameView extends View {
        private static final String[] MAPS = {
            "Proto07", "Lava", "RaceTrack1", "House",
            "ChaloHouse", "Dungeons", "Dust2", "SmallTown"
        };
        private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Handler handler = new Handler();
        private int screen = 0, selected = 1, mode = 0, map = 0, players = 6;
        private boolean vibro = true;
        private String notice = "";
        private long noticeUntil;
        private long loadingStart;
        private float sx = 1, sy = 1, ox, oy;

        GameView(Context context) { super(context); setLayerType(View.LAYER_TYPE_HARDWARE, null); }

        void back() {
            if (screen == 1 || screen == 2) screen = 0;
            else if (screen == 3 || screen == 4) screen = 1;
            else screen = 0;
            invalidate();
        }

        @Override protected void onDraw(Canvas c) {
            super.onDraw(c);
            sx = getWidth() / 400f;
            sy = getHeight() / 700f;
            float scale = Math.min(sx, sy);
            ox = (getWidth() - 400 * scale) / 2f;
            oy = (getHeight() - 700 * scale) / 2f;
            c.save();
            c.translate(ox, oy);
            c.scale(scale, scale);
            background(c);
            if (screen == 0) drawTitle(c);
            else if (screen == 1) drawCreate(c);
            else if (screen == 2) drawSettings(c);
            else if (screen == 3) drawLoading(c);
            else drawGameNotice(c);
            if (!notice.isEmpty() && System.currentTimeMillis() < noticeUntil)
                text(c, notice, 200, 666, 11, Color.WHITE, true);
            c.restore();
            if (screen == 3 && System.currentTimeMillis() - loadingStart > 1400) {
                screen = 4;
                invalidate();
            } else if (screen == 3) postInvalidateDelayed(32);
        }

        private void background(Canvas c) {
            p.setShader(new LinearGradient(0, 0, 0, 700, 0xff111827, 0xff263449, Shader.TileMode.CLAMP));
            c.drawRect(0, 0, 400, 700, p); p.setShader(null);
            p.setColor(0x182fd7ff);
            for (int y=0; y<700; y+=32) c.drawLine(0, y, 400, y, p);
            for (int x=0; x<400; x+=32) c.drawLine(x, 0, x, 700, p);
            p.setColor(0xfff4b942); c.drawCircle(340, 72, 22, p);
        }

        private void header(Canvas c, String subtitle) {
            text(c, "CHICKEN GUN", 200, 82, 31, 0xffffd45a, true);
            text(c, subtitle, 200, 108, 10, 0xffd7e4f5, true);
            p.setColor(0xfff4b942); c.drawRoundRect(24, 126, 376, 130, 2, 2, p);
        }

        private void button(Canvas c, float x, float y, float w, float h, String label, boolean active) {
            p.setShader(new LinearGradient(x, y, x, y+h,
                active ? 0xffffd45a : 0xff385675,
                active ? 0xffd8872d : 0xff1b3048, Shader.TileMode.CLAMP));
            c.drawRoundRect(x, y, x+w, y+h, 13, 13, p); p.setShader(null);
            p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(active ? 3 : 1.5f);
            p.setColor(active ? Color.WHITE : 0xff7895b3);
            c.drawRoundRect(x+2, y+2, x+w-2, y+h-2, 11, 11, p); p.setStyle(Paint.Style.FILL);
            text(c, label, x+w/2, y+h/2+6, 17, active ? 0xff182333 : Color.WHITE, true);
        }

        private void panel(Canvas c, float x, float y, float w, float h) {
            p.setColor(0xe8172638); c.drawRoundRect(x, y, x+w, y+h, 16, 16, p);
            p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(1.5f); p.setColor(0xff66829e);
            c.drawRoundRect(x, y, x+w, y+h, 16, 16, p); p.setStyle(Paint.Style.FILL);
        }

        private void drawTitle(Canvas c) {
            header(c, "ANDROID MENU PORT • PS2 MENU BASE");
            panel(c, 22, 166, 356, 430);
            text(c, "MAIN MENU", 200, 205, 13, 0xff9fc2df, true);
            button(c, 55, 244, 290, 66, "PLAY TEAM BATTLE", selected == 1);
            button(c, 55, 330, 290, 58, "SHOP", selected == 0);
            button(c, 55, 408, 290, 58, "SERVERS", selected == 2);
            button(c, 55, 500, 290, 52, "SETTINGS", false);
            text(c, "Tap a button to select", 200, 575, 10, 0xffb8c8d8, true);
            text(c, "Touch controls enabled", 200, 620, 10, 0xff91a9c4, true);
        }

        private void drawCreate(Canvas c) {
            header(c, "CREATE MATCH");
            panel(c, 20, 154, 360, 470);
            button(c, 34, 170, 92, 42, "BACK", false);
            text(c, "GAME MODE", 200, 248, 13, 0xffc5d9ed, true);
            button(c, 36, 266, 156, 54, "DEATHMATCH", mode == 0);
            button(c, 208, 266, 156, 54, "TEAM FIGHT", mode == 1);
            text(c, "MAP", 200, 365, 13, 0xffc5d9ed, true);
            button(c, 36, 382, 48, 52, "<", false);
            button(c, 92, 382, 216, 52, MAPS[map], true);
            button(c, 316, 382, 48, 52, ">", false);
            text(c, map == 0 ? "READY" : "NOT CONVERTED", 200, 454, 10,
                map == 0 ? 0xff7ff0a5 : 0xffffa56a, true);
            text(c, "PLAYERS", 200, 495, 13, 0xffc5d9ed, true);
            button(c, 86, 510, 62, 48, "-", false);
            text(c, String.valueOf(players), 200, 542, 22, Color.WHITE, true);
            button(c, 252, 510, 62, 48, "+", false);
            button(c, 74, 572, 252, 42, "CREATE MATCH", true);
        }

        private void drawSettings(Canvas c) {
            header(c, "SETTINGS");
            panel(c, 22, 166, 356, 420);
            button(c, 34, 180, 92, 42, "BACK", false);
            text(c, "VIBRATION", 86, 294, 16, Color.WHITE, false);
            button(c, 244, 266, 112, 52, vibro ? "ON" : "OFF", true);
            text(c, "PS2 menu option", 200, 380, 11, 0xffa8bbd0, true);
        }

        private void drawLoading(Canvas c) {
            header(c, "LOADING MATCH");
            panel(c, 28, 260, 344, 190);
            text(c, "Preparing " + MAPS[map], 200, 330, 17, Color.WHITE, true);
            float progress = Math.min(1f, (System.currentTimeMillis()-loadingStart)/1400f);
            p.setColor(0xff30445c); c.drawRoundRect(55, 365, 345, 381, 8, 8, p);
            p.setColor(0xffffc34f); c.drawRoundRect(55, 365, 55+290*progress, 381, 8, 8, p);
            text(c, "This is a menu loading test", 200, 420, 10, 0xffb7c8d8, true);
        }

        private void drawGameNotice(Canvas c) {
            header(c, "MATCH");
            panel(c, 22, 190, 356, 330);
            text(c, "CHICKEN GUN", 200, 270, 25, 0xffffd45a, true);
            text(c, "Gameplay is not included", 200, 330, 15, Color.WHITE, true);
            text(c, "in the supplied PS2 source.", 200, 355, 12, 0xffc6d4e4, true);
            text(c, "The source currently contains", 200, 400, 11, 0xffa9bfd5, true);
            text(c, "menu, UI and input code only.", 200, 420, 11, 0xffa9bfd5, true);
            button(c, 70, 465, 260, 46, "BACK TO MENU", true);
        }

        private void text(Canvas c, String s, float x, float y, float size, int color, boolean center) {
            p.setShader(null); p.setColor(color); p.setTextSize(size * getResources().getDisplayMetrics().scaledDensity);
            p.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
            p.setTextAlign(center ? Paint.Align.CENTER : Paint.Align.LEFT);
            c.drawText(s, x, y, p);
        }

        private void toast(String s) {
            notice = s; noticeUntil = System.currentTimeMillis() + 1800;
            invalidate();
        }

        @Override public boolean onTouchEvent(MotionEvent e) {
            if (e.getActionMasked() != MotionEvent.ACTION_UP) return true;
            float scale = Math.min(getWidth()/400f, getHeight()/700f);
            float x = (e.getX() - (getWidth()-400*scale)/2f) / scale;
            float y = (e.getY() - (getHeight()-700*scale)/2f) / scale;
            if (screen == 0) {
                if (y >= 230 && y <= 320) { screen = 1; }
                else if (y >= 320 && y <= 395) { toast("Shop is not available yet"); }
                else if (y >= 398 && y <= 475) { toast("Servers are not available yet"); }
                else if (y >= 485 && y <= 565) { screen = 2; }
            } else if (screen == 1) {
                if (y >= 165 && y <= 220 && x < 140) screen = 0;
                else if (y >= 260 && y <= 330) {
                    if (x < 200) mode = 0; else mode = 1;
                } else if (y >= 375 && y <= 440) {
                    if (x < 90) map = Math.max(0, map-1);
                    else if (x > 310) map = Math.min(MAPS.length-1, map+1);
                } else if (y >= 505 && y <= 560) {
                    if (x < 160) players = Math.max(2, players-1);
                    else if (x > 240) players = Math.min(16, players+1);
                } else if (y >= 565 && y <= 625) {
                    if (map == 0) { screen = 3; loadingStart = System.currentTimeMillis(); }
                    else toast("This map is not converted yet");
                }
            } else if (screen == 2) {
                if (y >= 175 && y <= 230 && x < 140) screen = 0;
                else if (y >= 250 && y <= 330) vibro = !vibro;
            } else if (screen == 3) {
                if (y < 220) screen = 1;
            } else if (screen == 4) {
                if (y >= 450 && y <= 530) screen = 0;
            }
            invalidate();
            return true;
        }
    }
}
