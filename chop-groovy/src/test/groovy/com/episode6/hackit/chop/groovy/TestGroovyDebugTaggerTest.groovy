package com.episode6.hackit.chop.groovy

import com.episode6.hackit.chop.Chop
import com.episode6.hackit.chop.Chop.Tree
import com.episode6.hackit.chop.ChopTestUtil
import spock.lang.Specification

/**
 * Tests {@link GroovyDebugTagger}
 * Has a strange name because the tagger will otherwise ignore this class in the
 * stacktrace because it matches its own name
 */
class TestGroovyDebugTaggerTest extends Specification {

  Tree mockTree

  def setup() {
    Chop.withTagger(new GroovyDebugTagger()).byDefault()
    mockTree = Mock(Tree) {
      _ * supportsLevel(_) >> true
    }
    Chop.plantTree(mockTree)
  }

  def cleanup() {
    ChopTestUtil.digUpTree(mockTree)
  }

  def "test called from method in diff file"() {
    when:
    GroovyTestUtil.logMessage("test message")

    then:
    1 * mockTree.chopLog(Chop.Level.E, "GroovyTestUtil:11", "test message")
  }

  def "test called from closure in diff file"() {
    when:
    GroovyTestUtil.getLogClosure("test message").call()

    then:
    1 * mockTree.chopLog(Chop.Level.E, "GroovyTestUtil:16", "test message")
  }

  def "test called from runnable in diff file"() {
    when:
    GroovyTestUtil.getLogRunnable("test message").run()

    then:
    1 * mockTree.chopLog(Chop.Level.E, "GroovyTestUtil:24", "test message")
  }


}
