package com.episode6.hackit.chop.slf4j

import com.episode6.hackit.chop.Chop
import com.episode6.hackit.mockspresso.annotation.RealObject
import com.episode6.hackit.mockspresso.quick.BuildQuickMockspresso
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.slf4j.Logger

class Slf4jTreeTest {
  companion object {
    val LOG_TAG = "TEST_TAG"
    val LOG_MESSAGE = "test log message"
    fun expectedOutput(level: Chop.Level): String = "::$LOG_TAG::$level::  $LOG_MESSAGE"
  }

  @get:Rule
  val mockspresso = BuildQuickMockspresso.with()
      .injector().simple()
      .mocker().mockito()
      .buildRule()

  @RealObject
  lateinit var tree: Slf4jTree

  @Mock
  lateinit var logger: Logger

  @Test
  fun testAlwaysEnabled() {
    Chop.Level.values().forEach {
      assertThat(tree.supportsLevel(it)).isTrue()
    }
  }

  @Test
  fun testLogVerbose() {
    tree.chopLog(Chop.Level.V, LOG_TAG, LOG_MESSAGE)

    verify(logger).debug(expectedOutput(Chop.Level.V))
    verifyNoMoreInteractions(logger)
  }

  @Test
  fun testLogDebug() {
    tree.chopLog(Chop.Level.D, LOG_TAG, LOG_MESSAGE)

    verify(logger).debug(expectedOutput(Chop.Level.D))
    verifyNoMoreInteractions(logger)
  }

  @Test
  fun testLogInfo() {
    tree.chopLog(Chop.Level.I, LOG_TAG, LOG_MESSAGE)

    verify(logger).info(expectedOutput(Chop.Level.I))
    verifyNoMoreInteractions(logger)
  }

  @Test
  fun testLogWarning() {
    tree.chopLog(Chop.Level.W, LOG_TAG, LOG_MESSAGE)

    verify(logger).warn(expectedOutput(Chop.Level.W))
    verifyNoMoreInteractions(logger)
  }

  @Test
  fun testLogError() {
    tree.chopLog(Chop.Level.E, LOG_TAG, LOG_MESSAGE)

    verify(logger).error(expectedOutput(Chop.Level.E))
    verifyNoMoreInteractions(logger)
  }
}
