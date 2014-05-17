package com.episode6.hackit.chop;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Tests Chop & ChoppingToolsAdapter to ensure they pass the correct log level
 * to the trees for the method being called (protection against copy-pasta)
 */
@RunWith(ChopTestTestRunner.class)
public class LogLevelTranslationTest {

  @Mock Chop.Tree mTree;

  @Before
  public void setUp() {
    when(mTree.supportsLevel(any(Chop.Level.class)))
        .thenReturn(true);

    Chop.plantTree(mTree);

    // in these tests we don't care about the initial calls to Tree.supportsLevel(), so
    // to ensure our tests are truly isolated, we reset the mock Tree and then re-enable support
    // for all log levels.
    reset(mTree);
    when(mTree.supportsLevel(any(Chop.Level.class)))
        .thenReturn(true);
  }

  @After
  public void cleanUp() {
    ChopInternals.TREE_FARM.digUpTree(mTree);
  }

  @Test
  public void testLevelV() {
    Chop.v("Verbose");
    Chop.v(new Throwable(), "Verbose");

    verifyLevel(Chop.Level.V);
  }

  @Test
  public void testLevelD() {
    Chop.d("Debug");
    Chop.d(new Throwable(), "Debug");

    verifyLevel(Chop.Level.D);
  }

  @Test
  public void testLevelI() {
    Chop.i("Info");
    Chop.i(new Throwable(), "Info");

    verifyLevel(Chop.Level.I);
  }

  @Test
  public void testLevelW() {
    Chop.w("Warning");
    Chop.w(new Throwable(), "Warning");

    verifyLevel(Chop.Level.W);
  }

  @Test
  public void testLevelE() {
    Chop.e("Error");
    Chop.e(new Throwable(), "Error");

    verifyLevel(Chop.Level.E);
  }

  @Test
  public void testAdapterLevelV() {
    Chop.withTag("TAG").v("Verbose");
    Chop.withTag("TAG").v(new Throwable(), "Verbose");

    verifyLevel(Chop.Level.V);
  }

  @Test
  public void testAdapterLevelD() {
    Chop.withTag("TAG").d("Debug");
    Chop.withTag("TAG").d(new Throwable(), "Debug");

    verifyLevel(Chop.Level.D);
  }

  @Test
  public void testAdapterLevelI() {
    Chop.withTag("TAG").i("Info");
    Chop.withTag("TAG").i(new Throwable(), "Info");

    verifyLevel(Chop.Level.I);
  }

  @Test
  public void testAdapterLevelW() {
    Chop.withTag("TAG").w("Warning");
    Chop.withTag("TAG").w(new Throwable(), "Warning");

    verifyLevel(Chop.Level.W);
  }

  @Test
  public void testAdapterLevelE() {
    Chop.withTag("TAG").e("Error");
    Chop.withTag("TAG").e(new Throwable(), "Error");

    verifyLevel(Chop.Level.E);
  }

  /**
   * Verify that supportsLevel and chopLog were called with the provided Chop.Level
   * @param level The level that was supposed to be used
   */
  private void verifyLevel(Chop.Level level) {
    verify(mTree, times(2)).supportsLevel(level);
    verify(mTree, times(3)).chopLog(eq(level), anyString(), anyString());
    verifyNoMoreInteractions(mTree);
  }
}
