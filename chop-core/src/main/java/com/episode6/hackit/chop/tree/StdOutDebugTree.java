package com.episode6.hackit.chop.tree;

import com.episode6.hackit.chop.Chop;

import java.io.PrintStream;

/**
 * A simple Java Tree that outputs logs to StdOut or StdErr (or both)
 */
public class StdOutDebugTree implements Chop.Tree {

  private static final String LOG_FORMAT = "::%s::%s::  %s";

  public enum OutputType {
    STD_OUT_ONLY,
    STD_ERR_ONLY,
    LOGICAL_SEPARATION
  }

  private final OutputType mOutputType;

  public StdOutDebugTree() {
    this(OutputType.LOGICAL_SEPARATION);
  }

  public StdOutDebugTree(OutputType outputType) {
    mOutputType = outputType;
  }

  @Override
  public boolean supportsLevel(Chop.Level level) {
    return true;
  }

  @Override
  public void chopLog(Chop.Level level, String tag, String message) {
    String output = String.format(LOG_FORMAT, level, tag, message);
    PrintStream printStream;
    switch (mOutputType) {
      case STD_ERR_ONLY:
        printStream = System.err;
        break;
      case STD_OUT_ONLY:
        printStream = System.out;
        break;
      default:
        printStream = level == Chop.Level.E ? System.err : System.out;
        break;
    }

    printStream.println(output);
  }
}
