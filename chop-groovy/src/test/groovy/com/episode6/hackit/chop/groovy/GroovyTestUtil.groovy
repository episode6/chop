package com.episode6.hackit.chop.groovy

import com.episode6.hackit.chop.Chop

/**
 *
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
}
