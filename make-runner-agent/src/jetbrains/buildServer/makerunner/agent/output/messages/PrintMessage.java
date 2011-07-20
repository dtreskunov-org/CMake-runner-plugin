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

package jetbrains.buildServer.makerunner.agent.output.messages;

import jetbrains.buildServer.makerunner.agent.util.Logger;
import jetbrains.buildServer.makerunner.agent.util.parser.Context;
import jetbrains.buildServer.makerunner.agent.util.parser.ParsedMessage;
import org.jetbrains.annotations.NotNull;

/**
 * @author Vladislav.Rassokhin
 */
public class PrintMessage implements ParsedMessage {
  private final String myPrintingLine;
  private final PrintType myPrintType;

  public PrintMessage(@NotNull final String description, @NotNull final PrintType verboseType) {
    myPrintingLine = description;
    myPrintType = verboseType;
  }

  public void apply(@NotNull final Context context) {
    print(context.getLogger(), getPrintingLine());
  }

  public String getPrintingLine() {
    return myPrintingLine;
  }

  public PrintType getPrintType() {
    return myPrintType;
  }

  protected final void print(@NotNull final Logger logger, @NotNull final String line) {
    switch (myPrintType) {
      case NONE:
        break;
      case INFO:
        logger.info(line);
        break;
      case MESSAGE:
        logger.message(line);
        break;
      case WARNING:
        logger.warning(line);
        break;
      case ERROR:
        logger.error(line);
        break;
    }
  }

  public enum PrintType {
    NONE,
    INFO,
    WARNING,
    ERROR,
    MESSAGE,
  }
}
