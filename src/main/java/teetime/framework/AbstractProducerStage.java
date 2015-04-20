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

/**
 * The <code>ProducerStage</code> produces at least one element at each execution.<br>
 *
 * @author Christian Wulf
 *
 * @param <O>
 *            the type of the default output port
 *
 */
public abstract class AbstractProducerStage<O> extends AbstractStage {

	protected final OutputPort<O> outputPort = this.createOutputPort();

	public final OutputPort<O> getOutputPort() {
		return this.outputPort;
	}

	@Override
	public void executeStage() {
		try {
			this.execute();
		} catch (Exception e) {
			final FurtherExecution furtherExecution = this.exceptionHandler.onStageException(e, this);
			if (furtherExecution == FurtherExecution.TERMINATE) {
				throw new StageException(e, this);
			}
		}
	}

	@Override
	public TerminationStrategy getTerminationStrategy() {
		return TerminationStrategy.BY_SELF_DECISION;
	}

	protected abstract void execute();

}
