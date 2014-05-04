package com.episode6.hackit.chop.tree;

import com.episode6.hackit.chop.Chop;

/**
 * Tree that does nothing
 */
public class DeadTree implements Chop.Tree {

  @Override
  public boolean supportsLevel(Chop.Level level) {
    return false;
  }

  @Override
  public void chopLog(Chop.Level level, String tag, String message) {

  }
}
