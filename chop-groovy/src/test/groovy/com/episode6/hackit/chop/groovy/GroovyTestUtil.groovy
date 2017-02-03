package com.episode6.hackit.chop.groovy

import com.episode6.hackit.chop.Chop

/**
 * util method used by GroovyDebugTaggerTest
 */
class GroovyTestUtil {

  static void logMessage(String testMessage) {
    Chop.e(testMessage)
  }

  static Closure getLogClosure(String testMessage) {
    return {
      Chop.e(testMessage)
    }
  }

  static Runnable getLogRunnable(final String testMessage) {
    return new Runnable() {
      @Override
      void run() {
        Chop.e(testMessage)
      }
    }
  }

  static Closure<Closure> getDoubleLogClosure(String testMessage) {
    return {
      return {
        Chop.e(testMessage)
      }
    }
  }
}
