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
package teetime.framework;

import teetime.framework.exceptionHandling.AbstractExceptionListener.FurtherExecution;
import teetime.framework.exceptionHandling.StageException;

public abstract class AbstractConsumerStage<I> extends AbstractStage {

	protected final InputPort<I> inputPort = this.createInputPort();

	public final InputPort<I> getInputPort() {
		return this.inputPort;
	}

	@Override
	public final void executeStage() {
		final I element = this.getInputPort().receive();
		if (null == element) {
			returnNoElement();
		}

		try {
			this.execute(element);
		} catch (Exception e) {
			final FurtherExecution furtherExecution = exceptionHandler.onStageException(e, this);
			if (furtherExecution == FurtherExecution.TERMINATE) {
				throw new StageException(e, this);
			}
		}
	}

	protected abstract void execute(I element);

}
