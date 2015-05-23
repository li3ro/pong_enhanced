package com.wee.boo.pong;

class Sync {
  boolean v;
  Sync(boolean init) { v = init; }
  synchronized public boolean get() { return v; }
  synchronized public void set(boolean _v) { v = _v; }
}
