package com.wee.boo.pong;

import android.view.View.OnTouchListener;
import android.view.View;
import android.view.MotionEvent;

public class TouchScreen implements OnTouchListener
{
  Game g;

  public TouchScreen(Game _g)
  {
    g = _g;
  }

  public boolean onTouch(View v, MotionEvent e)
  {
    for (int i = 0; i < e.getPointerCount(); i++)
      g.position((int)e.getX(i), (int)e.getY(i));
    return true;
  }
}
