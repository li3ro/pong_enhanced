package com.wee.boo.pong;

import java.util.concurrent.Semaphore;
import android.app.Activity;

public class Game
{
  int width;
  int height;
  int p1pos;
  int p2pos;
  float ball_posx;
  float ball_posy;
  float ball_speedx;
  float ball_speedy;
  float ball_angle;
  float ball_speed;
  int p1score;
  int p2score;
  public int fps;
  Semaphore audio_sem;
  Freq audio_freq;
  public static final float pi = (float)Math.PI;
  int score_pos;
  boolean game_over;
  int player_distance;
  Activity main_activity;

  public Game(int w, int h, int _fps, Freq _f, Activity a)
  {
    main_activity = a;
    width = w;
    height = h;
    p1pos = height / 2;
    p2pos = height / 2;
    p1score = 0;
    p2score = 0;
    ball_speed = 0;
    fps = _fps;
    audio_freq = _f;
    score_pos = 0;
    game_over = false;
    player_distance = 50; /* dumb default value, will be changed */
    new_ball();
  }

  public void set_player_distance()
  {
    android.util.DisplayMetrics metrics = new android.util.DisplayMetrics();
    main_activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
    if (metrics.xdpi < 10) metrics.xdpi = 10;
    player_distance = (int)(metrics.xdpi / 144 * 100 + .5);
    if (player_distance > metrics.widthPixels / 10)
      player_distance = metrics.widthPixels / 10;
    android.util.Log.d("blop", "set_player_distance xdpi=" + metrics.xdpi + " width=" + metrics.widthPixels);
  }

  synchronized public void position(int x, int y)
  {
    if (game_over) return;
    if (x < width / 2) {
      p1pos = y;
      if (p1pos < 10) p1pos = 10;
      if (p1pos > height - 10) p1pos = height - 10;
    } else {
      p2pos = y;
      if (p2pos < 10) p2pos = 10;
      if (p2pos > height - 10) p2pos = height - 10;
    }
  }

  /* R3(x, A, B, C, D):
   *    f(x) calculated by linear interpolation
   *    where f(A) = C and f(B) = D.
   */
  public float R3(float x, float A, float B, float C, float D)
  {
    return C + (x-A) * (D-C) / (B-A);
  }

  private boolean lost_p1(float ox, float oy)
  {
    float y = R3(player_distance, ox, ball_posx, oy, ball_posy);
    return Math.abs(y-p1pos) > 30;
  }

  private boolean lost_p2(float ox, float oy)
  {
    float y = R3(width-player_distance, ox, ball_posx, oy, ball_posy);
    return Math.abs(y-p2pos) > 30;
  }

  /* retun false if ball gets out of screen, in which case
   * ball_posx=-100 if player 1 loses and ball_posx=-200 if player 2 loses
   */
  /* updates scores */
  synchronized public boolean move()
  {
    float ox = ball_posx, oy = ball_posy;
    ball_posx += ball_speedx;
    ball_posy += ball_speedy;
    /* ball reaches the left */
    if (ball_posx < -40) {
      p2score++;
      ball_posx = -100;
      return false;
    }
    /* ball at left player's position */
    if (ox >= player_distance && ball_posx < player_distance) {
      if (!lost_p1(ox, oy)) {
        float at_zero = R3(player_distance, ox, ball_posx, oy, ball_posy);
        ball_posx = player_distance-(ball_posx-player_distance);
        ball_angle = pi-ball_angle + R3(at_zero-p1pos, -20, 20, pi/4, -pi/4);
        while (ball_angle >= pi) ball_angle -= 2*pi;
        while (ball_angle < -pi) ball_angle += 2*pi;
        if (ball_angle < -pi/2+pi/12) ball_angle = -pi/2+pi/12;
        if (ball_angle > pi/2-pi/12) ball_angle = pi/2-pi/12;
        inc_speed();
        compute_speed();
        audio_sem.release();
      }
    }
    /* ball reaches the right */
    if (ball_posx > width + 40) {
      p1score++;
      ball_posx = -200;
      return false;
    }
    /* ball at right player's position */
    if (ox < width-1-player_distance && ball_posx >= width-1-player_distance) {
      if (!lost_p2(ox, oy)) {
        float at_zero = R3(width-player_distance, ox, ball_posx, oy, ball_posy);
        ball_posx = width-1-player_distance -
                    (ball_posx - (width-1-player_distance));
        ball_angle = pi-ball_angle +R3(at_zero-p2pos, -20, 20, -pi/4, pi/4);
        while (ball_angle >= pi) ball_angle -= 2*pi;
        while (ball_angle < -pi) ball_angle += 2*pi;
        if (ball_angle>0 && ball_angle < pi/2+pi/12) ball_angle = pi/2+pi/12;
        if (ball_angle<0 && ball_angle > -pi/2-pi/12) ball_angle = -pi/2-pi/12;
        inc_speed();
        compute_speed();
        audio_sem.release();
      }
    }
    /* ball reaches the top */
    if (ball_posy < 5) {
      ball_posy = 5-(ball_posy-5);
      ball_angle = -ball_angle;
      inc_speed();
      compute_speed();
      audio_sem.release();
    }
    /* ball reaches the bottom */
    if (ball_posy >= height-1-5) {
      ball_posy = height-1-5 - (ball_posy - (height-1-5));
      ball_angle = -ball_angle;
      inc_speed();
      compute_speed();
      audio_sem.release();
    }
    return true;
  }

  private void inc_speed()
  {
    ball_speed *= 1.03;
    if (ball_speed > width / 6) ball_speed = width / 6;
    set_audio_freq();
  }

  private void compute_speed()
  {
    ball_speedx = (float)Math.cos(ball_angle) * ball_speed;
    ball_speedy = -(float)Math.sin(ball_angle) * ball_speed;
  }

  synchronized public void new_ball()
  {
    if (ball_posx == -100)
      ball_angle = -3 * (float)Math.PI / 4;
    else
      ball_angle = -1 * (float)Math.PI / 4;
    ball_speed /= 1.5;
    if (ball_speed < width / fps / 3) ball_speed = width / fps / 3;
    set_audio_freq();
    compute_speed();
    ball_posx = width / 2;
    ball_posy = 5;
  }

  public void set_size(int w, int h)
  {
    width = w;
    height = h;
    p1pos = height / 2;
    p2pos = height / 2;
    set_player_distance();
    new_ball();
  }

  public void set_audio_sem(Semaphore s) { audio_sem = s; }

  private void set_audio_freq()
  {
    int v = (int)R3(ball_speed, (float)(width / fps / 3), (float)(width / 6),
                    440, 1660);
    v /= 10;
    v *= 10;
    audio_freq.freq = v;
  }

  synchronized public void game_over()
  {
    p1pos = -100;
    p2pos = -100;
    game_over = true;
  }
}
