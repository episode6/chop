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

  @Test
  public void testOuterClass() {

    String expectedTag = DefaultTaggerTest.class.getSimpleName();

    Chop.e("Test Message");

    verify(mTree).chopLog(
        any(Chop.Level.class),
        eq(expectedTag),
        anyString()
    );
  }

  @After
  public void cleanUp() {
    ChopInternals.TREE_FARM.digUpTree(mTree);
  }
}
