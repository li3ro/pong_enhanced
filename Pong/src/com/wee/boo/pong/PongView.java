package com.wee.boo.pong;

import android.view.View;
import android.graphics.Canvas;

import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Paint;

import java.util.concurrent.Semaphore;

public class PongView extends View
{
    private Paint p;
    Game g;

    public PongView(Context c)
    {
        super(c);
        p = new Paint();
        p.setARGB(255, 255, 255, 255);
    }

    public PongView(Context c, AttributeSet a)
    {
        super(c, a);
        p = new Paint();
        p.setARGB(255, 255, 255, 255);
    }

    private final int[][][] numbers = {
      /* 0 */
      { { 0, 1, 1, 1, 0 },
        { 1, 0, 0, 0, 1 },
        { 1, 0, 0, 0, 1 },
        { 1, 0, 0, 0, 1 },
        { 1, 0, 0, 0, 1 },
        { 1, 0, 0, 0, 1 },
        { 0, 1, 1, 1, 0 } },
      /* 1 */
      { { 0, 0, 1, 0, 0 },
        { 0, 1, 1, 0, 0 },
        { 1, 0, 1, 0, 0 },
        { 0, 0, 1, 0, 0 },
        { 0, 0, 1, 0, 0 },
        { 0, 0, 1, 0, 0 },
        { 1, 1, 1, 1, 1 } },
      /* 2 */
      { { 0, 1, 1, 1, 0 },
        { 1, 0, 0, 0, 1 },
        { 0, 0, 0, 0, 1 },
        { 0, 0, 0, 1, 0 },
        { 0, 0, 1, 0, 0 },
        { 0, 1, 0, 0, 0 },
        { 1, 1, 1, 1, 1 } },
      /* 3 */
      { { 0, 1, 1, 1, 0 },
        { 1, 0, 0, 0, 1 },
        { 0, 0, 0, 0, 1 },
        { 0, 0, 1, 1, 0 },
        { 0, 0, 0, 0, 1 },
        { 1, 0, 0, 0, 1 },
        { 0, 1, 1, 1, 0 } },
      /* 4 */
      { { 0, 0, 0, 1, 0 },
        { 0, 0, 1, 0, 0 },
        { 0, 1, 0, 0, 0 },
        { 1, 0, 0, 1, 0 },
        { 1, 1, 1, 1, 1 },
        { 0, 0, 0, 1, 0 },
        { 0, 0, 0, 1, 0 } },
      /* 5 */
      { { 1, 1, 1, 1, 1 },
        { 1, 0, 0, 0, 0 },
        { 1, 0, 0, 0, 0 },
        { 0, 1, 1, 1, 0 },
        { 0, 0, 0, 0, 1 },
        { 1, 0, 0, 0, 1 },
        { 0, 1, 1, 1, 0 } },
      /* 6 */
      { { 0, 1, 1, 1, 0 },
        { 1, 0, 0, 0, 1 },
        { 1, 0, 0, 0, 0 },
        { 1, 1, 1, 1, 0 },
        { 1, 0, 0, 0, 1 },
        { 1, 0, 0, 0, 1 },
        { 0, 1, 1, 1, 0 } },
      /* 7 */
      { { 1, 1, 1, 1, 1 },
        { 0, 0, 0, 1, 0 },
        { 0, 0, 0, 1, 0 },
        { 0, 0, 1, 0, 0 },
        { 0, 0, 1, 0, 0 },
        { 0, 1, 0, 0, 0 },
        { 0, 1, 0, 0, 0 } },
      /* 8 */
      { { 0, 1, 1, 1, 0 },
        { 1, 0, 0, 0, 1 },
        { 1, 0, 0, 0, 1 },
        { 0, 1, 1, 1, 0 },
        { 1, 0, 0, 0, 1 },
        { 1, 0, 0, 0, 1 },
        { 0, 1, 1, 1, 0 } },
      /* 9 */
      { { 0, 1, 1, 1, 0 },
        { 1, 0, 0, 0, 1 },
        { 1, 0, 0, 0, 1 },
        { 0, 1, 1, 1, 1 },
        { 0, 0, 0, 0, 1 },
        { 1, 0, 0, 0, 1 },
        { 0, 1, 1, 1, 0 } },
    };

    private void draw_number(Canvas c, int x, int y, int v)
    {
      int i, j;
      for (i = 0; i < 7; i++) for (j = 0; j < 5; j++)
        if (numbers[v][i][j] == 1)
          c.drawRect(x+j*5, y+i*5+g.score_pos, x+j*5+5, y+i*5+5+g.score_pos, p);
    }

    public void onDraw(Canvas c)
    {
        super.onDraw(c);

        /* clear */
        c.drawRGB(0, 0, 0);
        /* draw ball */
        c.drawRect(g.ball_posx-5, g.ball_posy-5,
                   g.ball_posx+5, g.ball_posy+5, p);
        /* draw sep bar */
        c.drawRect(g.width/2-5, 0, g.width/2+5, g.height, p);
        /* draw player 1 */
        c.drawRect(g.player_distance-15/*0+60*/, g.p1pos-20,
                   g.player_distance-5/*10+60*/, g.p1pos+20, p);
        /* draw player 2 */
        c.drawRect(g.width-g.player_distance+5/*g.width-10-60*/, g.p2pos-20,
                   g.width-g.player_distance+15/* g.width-60*/, g.p2pos+20, p);
        /* draw score player 1 */
        if (g.p1score < 10) {
          draw_number(c, g.width/2 - 5*5 - 20, 15, g.p1score);
        } else {
          draw_number(c, g.width/2 - 5*5 - 20 - 5*5-10, 15, g.p1score/10);
          draw_number(c, g.width/2 - 5*5 - 20, 15, g.p1score%10);
        }
        /* draw score player 2 */
        if (g.p2score < 10) {
          draw_number(c, g.width/2 + 20, 15, g.p2score);
        } else {
          draw_number(c, g.width/2 + 20, 15, g.p2score/10);
          draw_number(c, g.width/2 + 5*5 + 20 + 10, 15, g.p2score%10);
        }
    }

  public void onLayout(boolean is_new, int a, int b, int c, int d)
  {
    if (is_new) g.set_size(c-a, d-b);
  }

  public void set_game(Game _g)
  {
    g = _g;
  }
}
