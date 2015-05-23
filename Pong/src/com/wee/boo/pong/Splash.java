package com.wee.boo.pong;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import java.util.Random;
import java.util.concurrent.Semaphore;

import android.media.MediaPlayer;
import com.wee.boo.pong.R;

class Splash extends Thread implements MediaPlayer.OnCompletionListener {
  public static final int [][]init_logo = {
		{47, 55},
	    {58, 55},
	    {66, 56},
		{74, 58},
		{80, 63},
		{83, 69},
		{75, 75},
		{65, 80},
		{75, 85},
		{83, 89},
		{80, 95},
		{74, 99},
		{67, 101},
		{58, 101},
		{47, 101},
		{58, 117},
		{51, 124},
		{52, 134},
		{60, 139},
		{65, 133},
		{68, 125},
		{69, 117},
		{79, 120},
		{83, 128},
		{83, 137},
		{76, 143},
		{83, 155},
		{22, 168},
		{31, 168},
		{40, 168},
		{50, 168},
		{60, 168},
		{69, 168},
		{78, 169},
		{83, 177},
		{83, 187},
		{82, 196},
		{72, 204},
		{62, 202},
		{59, 194},
		{58, 186},
		{59, 177},
		{64, 219},
		{57, 224},
		{54, 234},
		{56, 243},
		{63, 250},
		{73, 250},
		{82, 243},
		{83, 234},
		{83, 226},
		{74, 220},
		{64, 262},
		{57, 267},
		{54, 277},
		{56, 286},
		{63, 293},
		{73, 293},
		{82, 286},
		{83, 277},
		{83, 269},
		{74, 263},
		{141, 302},
		{151, 302},
		{134, 295},
		{134, 286},
		{135, 276},
		{142, 271},
		{152, 272},
		{161, 278},
		{161, 286},
		{160, 295},
		{161, 249},
		{152, 249},
		{143, 248},
		{134, 248},
		{118, 249},
		{106, 227},
		{115, 228},
		{124, 228},
		{133, 228},
		{143, 228},
		{153, 227},
		{162, 227},
		{140, 221},
		{138, 213},
		{140, 203},
		{148, 197},
		{158, 197},
		{164, 208},
		{164, 219},
		{139, 174},
		{148, 174},
		{157, 174},
		{164, 168},
		{164, 159},
		{164, 149},
		{157, 141},
		{148, 141},
		{139, 141},
		{153, 116},
		{160, 107},
		{160, 98},
		{151, 91},
		{142, 91},
		{135, 91},
		{125, 91},
		{116, 91},
		{122, 98},
		{122, 106},
		{125, 65},
		{119, 59},
		{119, 49},
		{120, 41},
		{130, 38},
		{138, 41},
		{140, 50},
		{143, 57},
		{149, 66},
		{156, 67},
		{162, 61},
		{163, 50},
		{160, 40},
		{155, 35}
  };
  public static final int [][]start_pos = new int[123][2];
  public static final int [][]pos = new int[123][2];
  public static final int [][]logo = new int[123][2];

  public static final int logo_width = 320;
  public static final int logo_height = 200;
  public static final Random rand = new Random();

  private GameState gs;
  private Sync finished;

  public Splash(GameState _gs) {
    gs = _gs;
    finished = new Sync(false);
  }

  public void onCompletion(MediaPlayer mp)
  {
    mp.release();
  }

  public static final int []bitmap = new int[64*64];

  public static final Paint mp = new Paint();

  public static final void draw(Canvas c, Paint p)
  {
    int i;
    c.drawARGB(255, 0, 0, 0);
    c.drawRGB(0, 0, 0);
    for (i = 0; i < 113; i++) {
      int x = pos[i][1] + rand.nextInt(5);
      int y = pos[i][0] + rand.nextInt(5);
//      c.drawRect(x, y, x+10, y+10, p);
        c.drawBitmap(bitmap, 0, 64, x-32, y-32, 64, 64, true, null);
    }
  }

  public void run()
  {
//  mp.setXfermode(new android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.
//  ADD));
  try {
    int fps;
    int n_frames;
    int x;
    int y;
    int logo_size = logo.length;
    int i, j;

    for (i = 0; i < 64; i++)
    for (j = 0; j < 64; j++) {
      int alpha = (int)(((i-32)*(i-32) + (j-32)*(j-32))/2048.*255);
      if (alpha > 255) alpha = 255;
      alpha = 255-alpha;
      double a = alpha / 255.;
      a = 1 - Math.sqrt((i/32. - 1.) * (i/32. -1.) + (j/32.-1.)*(j/32.-1.));
      if (a < 0) a = 0; if (a > 1) a = 1;
      if (a > 0.8) a = 0.8; a /= 0.8;
      a = a*a*a;
      alpha = (int)(a * 255);
      if (alpha > 255) alpha = 255;
      bitmap[i*64+j] = 0xffffff | (alpha << 24);
    }
    fps = 25;
    n_frames = fps * 7;

    if (!gs.sv.good_size)
      try { gs.sv.wait_for_size.acquire(); }
      catch(Exception e) { throw new Exception(); }
    if (finished.get()) throw new Exception();

    MediaPlayer p = MediaPlayer.create(gs.a, R.raw.splash);
    p.setOnCompletionListener(this);
    p.start();

    for (i = 0 ; i < n_frames / 3; i++) {
      for (j = 0; j < logo_size; j++) {
        pos[j][0] = (int)gs.g.R3(i, 0, n_frames/3-1, start_pos[j][0], logo[j][0]);
        pos[j][1] = (int)gs.g.R3(i, 0, n_frames/3-1, start_pos[j][1], logo[j][1]);
      }
      gs.sv.postInvalidate();
      synchronized (this) { try { wait(1000/fps); } catch(Exception e) {}}
      if (finished.get()) throw new Exception();
    }

    for (i = 0 ; i < n_frames / 3 / 2; i++) {
      gs.sv.postInvalidate();
      synchronized (this) { try { wait(1000/fps); } catch(Exception e) {}}
      if (finished.get()) throw new Exception();
    }

    synchronized (gs) {
    for (i = 0; i < logo_size; i++) {
/*
      start_pos[i][0]=rand.nextInt(200) + 100 + gs.height;
      start_pos[i][1]=rand.nextInt(200) + 100 + gs.width;
 */
      start_pos[i][0]=-360*2+rand.nextInt(360*4);
      start_pos[i][1]=rand.nextInt(gs.width) + gs.width;
    }
    }

    for (i = 0 ; i < n_frames / 2; i++) {
      for (j = 0; j < logo_size; j++) {
/*
        pos[j][0] = (int)gs.g.R3(i, n_frames/3, 0, start_pos[j][0], logo[j][0]);
        pos[j][1] = (int)gs.g.R3(i, n_frames/3, 0, start_pos[j][1], logo[j][1]);
*/
        float angle = (float)(gs.g.R3(i, 0, n_frames/3, 0,  start_pos[j][0]) / 180. * gs.g.pi);
        float rho = gs.g.R3(i, 0, n_frames/3, 0, start_pos[j][1]);
        pos[j][0] = (int)(rho * Math.sin(angle) + logo[j][0]);
        pos[j][1] = (int)(rho * Math.cos(angle) + logo[j][1]);
      }
      gs.sv.postInvalidate();
      synchronized (this) { try { wait(1000/fps); } catch(Exception e) {}}
      if (finished.get()) throw new Exception();
    }
  } catch (Exception e) {};
    android.util.Log.d("blop", "Splash done");
    if (!finished.get()) gs.go_game();
  }

  public void die()
  {
    finished.set(true);
    interrupt();
  }

  public void set_size()
  {
    int i;
    synchronized (gs) {
    for (i = 0; i < 113; i++) {
      start_pos[i][0]=rand.nextInt(200) - 300;
      start_pos[i][1]=rand.nextInt(200) - 300;
      logo[i][0] = init_logo[i][0] + (gs.height-200)/2;
      logo[i][1] = init_logo[i][1] + (gs.width-320)/2;
    }
    }
  }
}
