package com.episode6.hackit.chop.android;

import android.util.Log;

import com.episode6.hackit.chop.Chop;

public class AndroidDebugTree implements Chop.Tree {

  @Override
  public boolean supportsLevel(Chop.Level level) {
    return true;
  }

  @Override
  public void chopLog(Chop.Level level, String tag, String message) {
    switch (level) {
      case V:
        Log.v(tag, message);
        break;
      case D:
        Log.d(tag, message);
        break;
      case I:
        Log.i(tag, message);
        break;
      case W:
        Log.w(tag, message);
        break;
      case E:
        Log.e(tag, message);
        break;
    }
  }
}
