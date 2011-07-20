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

import jetbrains.buildServer.makerunner.agent.output.MakeOutputListener;
import jetbrains.buildServer.makerunner.agent.util.LoggerAdapter;
import junit.framework.TestCase;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static jetbrains.buildServer.makerunner.tests.output.MakeOutputGenerator.generateLeaveMessage;
import static jetbrains.buildServer.makerunner.tests.output.MakeOutputGenerator.generateStartingTargetMessage;

/**
 * @author Vladislav.Rassokhin
 */
public class MakeLoggingListenerTest extends TestCase {

  public void testTargetsFolding() throws Exception {
    final BracketSequenceMakeLogger logger = new BracketSequenceMakeLogger();
    final AtomicReference<List<String>> makeTasks = new AtomicReference<List<String>>(Arrays.asList("all", "clean"));
    final MakeOutputListener mll = new MakeOutputListener(logger, makeTasks);

    {
      final File workingDirectory = new File("");
      mll.processStarted("make", workingDirectory);
      mll.onStandardOutput(generateStartingTargetMessage("all", "."));
      mll.onStandardOutput(generateStartingTargetMessage("all", "b"));
      mll.onStandardOutput(generateLeaveMessage("b", 2));
      mll.onStandardOutput(generateStartingTargetMessage("all", "c"));
      mll.onStandardOutput(generateLeaveMessage("c", 2));
      mll.onStandardOutput(generateLeaveMessage(workingDirectory.getName(), 1));
      mll.onStandardOutput(generateStartingTargetMessage("clean", "."));
      mll.onStandardOutput(generateLeaveMessage(workingDirectory.getName(), 1));
      mll.processFinished(0);
    }

    assertTrue(logger.isSequenceCorrect());
    assertEquals("(()())()", logger.getSequence());
  }

  public void testTargetsFoldingDirs() throws Exception {
    final BracketSequenceMakeLogger logger = new BracketSequenceMakeLogger(true) {
      @Override
      public void blockStart(@NotNull final String name) {
        super.blockStart(name);
        myBuilder.append(name.charAt(name.length() - 1));
      }

      @Override
      public void blockFinish(@NotNull final String name) {
        super.blockFinish(name);
        myBuilder.append(name.charAt(name.length() - 1));
      }
    };
    final AtomicReference<List<String>> makeTasks = new AtomicReference<List<String>>(Arrays.asList("all", "clean"));
    final MakeOutputListener mll = new MakeOutputListener(logger, makeTasks);

    {
      final File workingDirectory = new File("");
      mll.processStarted("make", workingDirectory);
      mll.onStandardOutput(generateStartingTargetMessage("all", "."));
      mll.onStandardOutput(generateStartingTargetMessage("all", "b"));
      mll.onStandardOutput(generateLeaveMessage("b", 2));
      mll.onStandardOutput(generateStartingTargetMessage("all", "c"));
      mll.onStandardOutput(generateLeaveMessage("c", 2));
      mll.onStandardOutput(generateLeaveMessage(workingDirectory.getName(), 1));
      mll.onStandardOutput(generateStartingTargetMessage("clean", "."));
      mll.onStandardOutput(generateLeaveMessage(workingDirectory.getName(), 1));
      mll.processFinished(0);
    }

    assertTrue(logger.isSequenceCorrect());
    assertEquals("(/(b)b(c)c)/(/)/", logger.getSequence());
  }

  public void testSubstring() throws Exception {
    class TargetsCollector extends LoggerAdapter {
      final List<String> myTargets = new ArrayList<String>();

      @Override
      public void blockStart(@NotNull final String targetName) {
        myTargets.add(targetName.split(" ")[1]);
      }
    }
    final TargetsCollector tc = new TargetsCollector();
    final List<String> targets = Arrays.asList("all", "clean");
    final AtomicReference<List<String>> makeTasks = new AtomicReference<List<String>>(targets);
    final MakeOutputListener mll = new MakeOutputListener(tc, makeTasks);

    final File workingDir = new File("");
    mll.processStarted("make", workingDir);
    for (final String t : targets) {
      mll.onStandardOutput(generateStartingTargetMessage(t, "."));
      mll.onStandardOutput(generateLeaveMessage(workingDir.getName(), 0));
    }
    mll.processFinished(0);

    assertEquals(targets, tc.myTargets);
  }

}
