/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package teetime.framework.exceptionHandling;

import teetime.framework.Stage;

public class TestListener extends AbstractExceptionListener {

	public static int exceptionInvoked = 0;

	public TestListener() {
		TestListener.exceptionInvoked = 0;
	}

	@Override
	public FurtherExecution onStageException(final Exception e, final Stage throwingStage) {
		exceptionInvoked++;
		if (exceptionInvoked == 2) {
			return FurtherExecution.TERMINATE;
		} else {
			return FurtherExecution.CONTINUE;
		}
	}

}
