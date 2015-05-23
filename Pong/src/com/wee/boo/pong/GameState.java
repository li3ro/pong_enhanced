package com.wee.boo.pong;

import android.app.Activity;
import android.os.Handler;

class GameState
{
  public Game g;
  public GamePlay gp;
  public Splash s;
  public SplashView sv;
  public PongView pv;
  public int width;
  public int height;
  public boolean in_splash;
  public boolean stopped;
  public Activity a;
  public Freq f;
  public Handler h;

  GameState() {
    in_splash = true;
    stopped = true;
  }

  void set_game(Game _g) { g = _g; }
  void set_splash_view(SplashView _sv) { sv = _sv; }
  void set_pong_view(PongView _pv) { pv = _pv; }
  void set_activity(Activity _a) { a = _a; }
  void set_freq(Freq _f) { f = _f; }
  void set_handler(Handler _h) { h = _h; }

  synchronized void start()
  {
	  if(stopped)
		  h.sendEmptyMessage(99);	// show interstitial
    if (!stopped) return;
    stopped = false;
    if (in_splash) {
      s = new Splash(this);
      h.sendEmptyMessage(0); /* set content view in UI thread */
      s.start();
    } else {
      gp = new GamePlay(g, pv, f, a);
      h.sendEmptyMessage(1); /* set content view in UI thread */
      gp.start();
    }
  }

  synchronized void stop()
  {
    if (stopped) return;
    stopped = true;
    if (in_splash) s.die(); else gp.die();
  }

  synchronized void go_game()
  {
    stop();
    in_splash = false;
    start();
  }
}
