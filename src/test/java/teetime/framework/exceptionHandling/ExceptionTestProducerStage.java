/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://christianwulf.github.io/teetime)
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

import teetime.framework.AbstractProducerStage;
import teetime.framework.InputPort;
import teetime.framework.TerminationStrategy;

public class ExceptionTestProducerStage extends AbstractProducerStage<Object> {

	private static int instances = 0;
	private TerminationStrategy strategy;
	public int numberOfExecutions = 0;
	private final InputPort<Object> input = createInputPort();

	ExceptionTestProducerStage() {
		switch (instances) {
		case 0: {
			strategy = TerminationStrategy.BY_SELF_DECISION;
			break;
		}
		case 1: {
			strategy = TerminationStrategy.BY_INTERRUPT;
			break;
		}
		default: {
			strategy = TerminationStrategy.BY_SELF_DECISION;
		}
		}

		instances++;
	}

	@Override
	protected void execute() {
		getOutputPort().send(new Object());
		if (numberOfExecutions++ >= 10000 && strategy == TerminationStrategy.BY_SELF_DECISION) {
			this.terminate();
		}
	}

	@Override
	public TerminationStrategy getTerminationStrategy() {
		return strategy;
	}

	@Override
	public String getId() {
		if (strategy == TerminationStrategy.BY_INTERRUPT) {
			return "Infinite" + super.getId();
		}
		return "Finite" + super.getId();
	}
}
