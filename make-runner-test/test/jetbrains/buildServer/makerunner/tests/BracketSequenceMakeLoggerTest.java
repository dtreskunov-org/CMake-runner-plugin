/*
 * Copyright 2000-2011 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jetbrains.buildServer.makerunner.tests;

import junit.framework.TestCase;

/**
 * @author Vladislav.Rassokhin
 */
public class BracketSequenceMakeLoggerTest extends TestCase {
  public void testGetSequence() throws Exception {
    final BracketSequenceMakeLogger logger = new BracketSequenceMakeLogger();
    final String sequence = "(()(()))";
    for (int i = 0; i < sequence.length(); i++) {
      switch (sequence.charAt(i)) {
        case '(':
          logger.blockStart("");
          break;
        case ')':
          logger.blockFinish("");
          break;
      }
    }
    assertEquals(sequence, logger.getSequence());
  }

  public void testResetSequence() throws Exception {
    final BracketSequenceMakeLogger logger = new BracketSequenceMakeLogger();
    assertEquals(0, logger.getSequence().length());
    logger.blockStart("");
    logger.blockStart("");
    logger.blockStart("");
    logger.blockFinish("");
    assertEquals(4, logger.getSequence().length());
    logger.resetSequence();
    assertEquals(0, logger.getSequence().length());
  }

  public void testIsSequenceCorrect() throws Exception {
    final BracketSequenceMakeLogger logger = new BracketSequenceMakeLogger();
    assertTrue(logger.isSequenceCorrect());
    final String validSeq = "(()(()))";
    for (int i = 0; i < validSeq.length(); i++) {
      switch (validSeq.charAt(i)) {
        case '(':
          logger.blockStart("");
          break;
        case ')':
          logger.blockFinish("");
          break;
      }
    }
    assertTrue(logger.isSequenceCorrect());


    logger.resetSequence();
    assertTrue(logger.isSequenceCorrect());
    final String badSeq1 = "(()))";
    for (int i = 0; i < badSeq1.length(); i++) {
      switch (badSeq1.charAt(i)) {
        case '(':
          logger.blockStart("");
          break;
        case ')':
          logger.blockFinish("");
          break;
      }
    }
    assertFalse(logger.isSequenceCorrect());


    logger.resetSequence();
    assertTrue(logger.isSequenceCorrect());
    final String badSeq2 = "(()()";
    for (int i = 0; i < badSeq2.length(); i++) {
      switch (badSeq2.charAt(i)) {
        case '(':
          logger.blockStart("");
          break;
        case ')':
          logger.blockFinish("");
          break;
      }
    }
    assertFalse(logger.isSequenceCorrect());
  }
}
