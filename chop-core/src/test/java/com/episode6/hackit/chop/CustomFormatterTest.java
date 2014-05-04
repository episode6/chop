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

/**
 * Tests usage of a custom formatter to ensure the expected Strings get output to the trees
 */
@RunWith(ChopTestTestRunner.class)
public class CustomFormatterTest {

  @Mock Chop.Tree mTree;
  @Mock Chop.Formatter mFormatter;

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
  public void testSimpleMessage() {
    String expectedMessage = "STOLEN MESSAGE";
    when(mFormatter.formatLog(anyString()))
        .thenReturn(expectedMessage);

    Chop.withFormatter(mFormatter).d("My Log Message");

    verify(mTree).chopLog(
        any(Chop.Level.class),
        anyString(),
        eq(expectedMessage));
  }

  @Test
  public void testThrowableMessage() {
    String firstExpectedMessage = "STOLEN MESSAGE";
    String secondExpectedMessage = "STOLEN THROWABLE";
    when(mFormatter.formatLog(anyString()))
        .thenReturn(firstExpectedMessage);
    when(mFormatter.formatThrowable(any(Throwable.class)))
        .thenReturn(secondExpectedMessage);

    Chop.withFormatter(mFormatter).d(new Throwable(), "My Log Message");

    verify(mTree).chopLog(
        any(Chop.Level.class),
        anyString(),
        eq(firstExpectedMessage));
    verify(mTree).chopLog(
        any(Chop.Level.class),
        anyString(),
        eq(secondExpectedMessage));
  }
}
