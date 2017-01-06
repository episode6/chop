package com.episode6.hackit.chop.android;

import android.util.Log;

import com.episode6.hackit.chop.Chop;

import javax.annotation.Nullable;

public class AndroidDebugTree implements Chop.Tree {

  private final @Nullable String mTagPrefix;

  public AndroidDebugTree() {
    this(null);
  }

  public AndroidDebugTree(String tagPrefix) {
    mTagPrefix = tagPrefix;
  }

  @Override
  public boolean supportsLevel(Chop.Level level) {
    return true;
  }

  @Override
  public void chopLog(Chop.Level level, String tag, String message) {
    final String formattedTag = mTagPrefix == null ? tag : mTagPrefix.concat(tag);
    switch (level) {
      case V:
        Log.v(formattedTag, message);
        break;
      case D:
        Log.d(formattedTag, message);
        break;
      case I:
        Log.i(formattedTag, message);
        break;
      case W:
        Log.w(formattedTag, message);
        break;
      case E:
        Log.e(formattedTag, message);
        break;
    }
  }
}
