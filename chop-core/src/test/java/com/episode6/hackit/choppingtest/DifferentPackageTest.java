package com.episode6.hackit.choppingtest;

import com.episode6.hackit.chop.Chop;
import com.episode6.hackit.chop.ChopTestTestRunner;
import com.episode6.hackit.chop.ChopTestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.io.IOException;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests Chop accessibility from a different package
 */
@RunWith(ChopTestTestRunner.class)
public class DifferentPackageTest {

  @Mock
  Chop.Tree mTree;

  @Before
  public void setUp() {
    when(mTree.supportsLevel(any(Chop.Level.class)))
        .thenReturn(true);

    Chop.plantTree(mTree);
  }

  @After
  public void cleanUp() {
    ChopTestUtil.digUpTree(mTree);
  }

  @Test
  public void testSettableTagger() {
    String expectedTag = "TESTTAG";

    // make sure this always compiles
    Chop.withTag(expectedTag).d("Test Message");

    verify(mTree).chopLog(
        any(Chop.Level.class),
        eq(expectedTag),
        anyString());
  }

  @Test
  public void testDefaults() {

    // make sure this always compiles
    Chop.withFormatter(Chop.Defaults.FORMATTER)
        .andTagger(Chop.Defaults.TAGGER)
        .byDefault();

    String expectedTag = DifferentPackageTest.class.getSimpleName();

    Chop.d("Test Message");

    verify(mTree).chopLog(
        any(Chop.Level.class),
        eq(expectedTag),
        anyString());
  }

  @Test(expected = Throwable.class)
  public void testThrowPassedThrowable() throws Throwable {
    throw Chop.e(new Throwable(), "test message");
  }

  @Test(expected = IOException.class)
  public void testThrowPassedException() throws IOException {
    throw Chop.e(new IOException(), "test message");
  }
}
