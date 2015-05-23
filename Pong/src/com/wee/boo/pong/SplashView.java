package com.wee.boo.pong;

import android.view.View;
import android.graphics.Canvas;

import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Paint;

import java.util.concurrent.Semaphore;

public class SplashView extends View
{
  private Paint p;
  private GameState s;
  public boolean good_size;
  public Semaphore wait_for_size;

  public SplashView(Context c, GameState _s)
  {
    super(c);
    p = new Paint();
    p.setARGB(255, 255, 255, 255);
    s = _s;
    good_size = false;
    wait_for_size = new Semaphore(0);
  }

  public void onDraw(Canvas c)
  {
    super.onDraw(c);
    s.s.draw(c, p);
  }

  public void onSizeChanged(int w, int h, int ow, int oh)
  {
android.util.Log.d("blop", "SplashView width " + w + " h " + h + " oh " + oh + " ow " + ow);
    if (w == 0 || h == 0) return;
    synchronized (s) {
      s.width = w;
      s.height = h;
    }
    s.s.set_size();
    good_size = true;
    wait_for_size.release();
  }
}
