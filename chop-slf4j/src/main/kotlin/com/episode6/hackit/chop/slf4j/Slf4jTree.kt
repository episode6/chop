package com.episode6.hackit.chop.slf4j

import com.episode6.hackit.chop.Chop
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * An implementation of [Chop.Tree] that writes to an slf4j [Logger]
 */
open class Slf4jTree(private val logger: Logger) : Chop.Tree {

  companion object {

    /**
     * Convenience method to create a new [Slf4jTree] with a new [Logger]
     * from slf4j's [LoggerFactory.getLogger]
     */
    @JvmStatic
    fun create(loggerId: String): Slf4jTree = Slf4jTree(LoggerFactory.getLogger(loggerId))
  }

  override fun supportsLevel(level: Chop.Level): Boolean = true

  override fun chopLog(level: Chop.Level, tag: String, message: String) {
    val log = "::$tag::$level::  $message"
    when(level) {
      Chop.Level.E -> logger.error(log)
      Chop.Level.W -> logger.warn(log)
      Chop.Level.I -> logger.info(log)
      else -> logger.debug(log)
    }
  }

}
