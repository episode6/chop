package com.episode6.hackit.chop;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests the tag output when using Chop.withTag()
 */
@RunWith(ChopTestTestRunner.class)
public class SettableTaggerTest {

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
  public void testSettableTagger() {
    String expectedTag = "MYTESTTAG";

    Chop.withTag(expectedTag).d("Test message");

    verify(mTree).chopLog(
        any(Chop.Level.class),
        eq(expectedTag),
        anyString());
  }

  @Test
  public void testSettableTaggerAsDefault() {
    try {
      Chop.withTag("CustomTag").byDefault();
    } catch (IllegalArgumentException e) {
      // pass
      return;
    }

    fail("Did not throw exception when trying to set use the SettableTagger by default");
  }
}
