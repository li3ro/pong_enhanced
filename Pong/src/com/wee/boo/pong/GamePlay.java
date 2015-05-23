package com.wee.boo.pong;

import java.lang.Thread;
import java.util.concurrent.Semaphore;

import android.view.View;
import android.media.AudioTrack;
import android.media.AudioManager;
import android.media.AudioFormat;

import java.util.concurrent.TimeUnit;

import android.media.MediaPlayer;
import android.content.Context;
import com.wee.boo.pong.R;

public class GamePlay extends Thread implements MediaPlayer.OnCompletionListener {
	private static final int STRIKES_4_GAME_OVER = 11;
	Game g;
  View v;
  Semaphore timer_sem;
  Semaphore audio_sem;
  Semaphore audio_done;
  Thread timer_thread = null;
  Thread audio_thread = null;
  Freq audio_freq;
  float cur_freq;
  Sync dead;
  Context app_context;

  public GamePlay(Game _g, View _v, Freq _f, Context _c)
  {
    g = _g;
    v = _v;
    audio_freq = _f;
    cur_freq = 0;
    app_context = _c;
    dead = new Sync(false);
    timer_sem = new Semaphore(0);
    audio_sem = new Semaphore(0);
    audio_done = new Semaphore(0);
  }

  public void onCompletion(MediaPlayer mp)
  {
    mp.release();
  }

  private void play_lost_sound()
  {
    MediaPlayer p = MediaPlayer.create(app_context, R.raw.lost);
    p.setOnCompletionListener(this);
    p.start();
  }

  private void play_won_sound()
  {
    MediaPlayer p = MediaPlayer.create(app_context, R.raw.won);
    p.setOnCompletionListener(this);
    p.start();
  }

  public void run()
  {
    android.util.Log.d("blop", "GamePlay starts");
    g.set_audio_sem(audio_sem);

    /* reset game if game over (yeah it's possible, life cycle my ass...) */
    if (g.p1score == STRIKES_4_GAME_OVER || g.p2score == STRIKES_4_GAME_OVER) {
      g.p1score = 0;
      g.p2score = 0;
      g.ball_speed = 0;
      g.game_over = false;
      g.p1pos = g.height / 2;
      g.p2pos = g.height / 2;
      g.audio_freq.freq = 440;
      g.score_pos = 0;
    }

    /* throw a timer that bips every 1000/fps ms */
    timer_thread = new Thread() {
      public void run() {
        android.util.Log.d("blop", "GamePlay TIMER starts");
        while (!dead.get()) {
          synchronized(this) {
            try { wait(1000/g.fps); } catch (Exception e)
              { android.util.Log.d("blop", "wait error!"); }
          }
          timer_sem.release();
        }
        android.util.Log.d("blop", "GamePlay TIMER stops");
      }
    };
    timer_thread.start();

    audio_thread = new Thread() {
      short []audio_buf;
      short []empty;
      public void run() {
        android.util.Log.d("blop", "GamePlay audio_thread start");
        AudioTrack t;
        int rate = AudioTrack.getNativeOutputSampleRate(
                       AudioManager.STREAM_MUSIC);
        int nb_samples = AudioTrack.getMinBufferSize(rate,
                             AudioFormat.CHANNEL_OUT_MONO,
                             AudioFormat.ENCODING_PCM_16BIT);
        if (nb_samples < 1024) nb_samples = 1024;
        android.util.Log.d("blop", "rate = " + rate + "Hz nb_samples= " + nb_samples);
        audio_buf = new short[1024];
        empty = new short[1024];
        int i;
        for (i = 0; i < 1024; i++) empty[i] = 0;
        t = new AudioTrack(AudioManager.STREAM_MUSIC,
                        rate, AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT, nb_samples,
                        AudioTrack.MODE_STREAM);
        t.play();
        while (!dead.get()) {
          if (cur_freq != audio_freq.freq) {
            cur_freq = audio_freq.freq;
            for (i = 0; i < 1024; i++) {
              double v = Math.sin((double)i * cur_freq * Math.PI*2./rate);
              v *= Math.max((double)0,
                              32767 - (double)i/1024*32767);
              audio_buf[i] = (short)v;
            }
          }
          if (audio_sem.tryAcquire())
            t.write(audio_buf, 0, 1024);
          else
            t.write(empty, 0, 1024);
        }
        t.stop();
        t.release();
        android.util.Log.d("blop", "GamePlay audio_thread stops");
      }
    };
    audio_thread.start();

    g.new_ball();

    while (!dead.get()) {
      try {
        timer_sem.acquire();
        if (g.move() == false) {
          if (g.p1score == STRIKES_4_GAME_OVER || g.p2score == STRIKES_4_GAME_OVER) {
            /* game over */
            g.game_over();
            v.postInvalidate();
            play_won_sound();
            int i;
            int n_frames = g.fps;
            for (i = 0; i < n_frames; i++) {
              g.score_pos = (int)g.R3(i, 0, n_frames-1,
                                      0, (g.height-7*5)/2 - 15);
              timer_sem.acquire();
              v.postInvalidate();
            }
            timer_sem.acquire(g.fps*3);
            die();
          } else {
            play_lost_sound();
            g.new_ball();
            for (int i = 0; i < g.fps; i++) {
              v.postInvalidate();
              timer_sem.acquire();
            }
          }
        }
        v.postInvalidate();
      } catch (Exception e) {}
    }
    android.util.Log.d("blop", "GamePlay exits");
  }

  public void die()
  {
    android.util.Log.d("blop", "GamePlay die() called");
    dead.set(true);
    interrupt();
    if (timer_thread != null) timer_thread.interrupt();
    if (audio_thread != null) audio_thread.interrupt();
  }
}
