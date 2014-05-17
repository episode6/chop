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
 * Tests usage of Chop.withTag()
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
  public void testSettableTaggerClass() {
    Class<?> tag = SettableTaggerTest.class;
    String expectedTag = tag.getSimpleName();

    Chop.withTag(tag).d("Test message");

    verify(mTree).chopLog(
        any(Chop.Level.class),
        eq(expectedTag),
        anyString());
  }

  @Test
  public void testImpossibleDefault() {
    Chop.ChoppingToolsAdapter adapter = Chop.withTag("testTag");

    try {
      Chop.DefaultableChoppingToolsAdapter defaultableAdapter = (Chop.DefaultableChoppingToolsAdapter)adapter;
      defaultableAdapter.byDefault();
    } catch (ClassCastException e) {
      // Success
      return;
    }
    fail("Expected a ClassCastException");
  }

  @Test
  public void testImpossibleDefaultClass() {
    Class<?> tag = SettableTaggerTest.class;
    Chop.ChoppingToolsAdapter adapter = Chop.withTag(tag);

    try {
      Chop.DefaultableChoppingToolsAdapter defaultableAdapter = (Chop.DefaultableChoppingToolsAdapter)adapter;
      defaultableAdapter.byDefault();
    } catch (ClassCastException e) {
      // Success
      return;
    }
    fail("Expected a ClassCastException");
  }
}
