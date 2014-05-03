package com.episode6.hackit.chop;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(ChopTestTestRunner.class)
public class DefaultTaggerTest {

  @Mock Chop.Tree mTree;

  @Before
  public void setUp() {
    when(mTree.supportsLevel(any(Chop.Level.class)))
        .thenReturn(true);

    Chop.plantTree(mTree);
  }

  @After
  public void cleanUp() {
    ChopInternals.TREE_FARM.digUpTree(mTree);
  }

  @Test
  public void testOuterClass() {
    String expectedTag = DefaultTaggerTest.class.getSimpleName();

    Chop.d("Test Message");

    verifyExpectedTag(expectedTag);
  }

  @Test
  public void testAnonymousClass() {
    String expectedTag = DefaultTaggerTest.class.getSimpleName();

    new Runnable() {

      @Override
      public void run() {
        Chop.d("Test message");
      }
    }.run();

    verifyExpectedTag(expectedTag);
  }

  @Test
  public void testInnerClass() {
    String expectedTag = DefaultTaggerTest.class.getSimpleName() + "$" +
        InnerTestClass.class.getSimpleName();

    new InnerTestClass().printLog();

    verifyExpectedTag(expectedTag);
  }

  @Test
  public void testWithDefaultableToolsAdapter() {
    String expectedTag = DefaultTaggerTest.class.getSimpleName();

    Chop.withFormatter(Chop.Defaults.FORMATTER).d("Test Message");

    verifyExpectedTag(expectedTag);
  }

  private void verifyExpectedTag(String expectedTag) {
    verify(mTree).chopLog(
        any(Chop.Level.class),
        eq(expectedTag),
        anyString());
  }

  private class InnerTestClass {
    public void printLog() {
      Chop.d("Test Message");
    }
  }
}
