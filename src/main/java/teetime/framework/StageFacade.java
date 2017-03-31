/**
 * Copyright © 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime-framework.github.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package teetime.framework;

import java.util.List;

import teetime.framework.exceptionHandling.AbstractExceptionListener;
import teetime.framework.exceptionHandling.TerminateException;

/**
 * Used to access the package-private methods of {@link AbstractStage}.
 *
 * @author Christian Wulf (chw)
 *
 */
public final class StageFacade {

	public static final StageFacade INSTANCE = new StageFacade();

	private StageFacade() {
		// singleton instance
	}

	public void abort(final AbstractStage stage) {
		stage.abort();
	}

	public TerminationStrategy getTerminationStrategy(final AbstractStage stage) {
		return stage.getTerminationStrategy();
	}

	public Thread getOwningThread(final AbstractStage stage) {
		return stage.getOwningThread();
	}

	public ConfigurationContext getOwningContext(final AbstractStage stage) {
		return stage.getOwningContext();
	}

	public void setOwningThread(final AbstractStage stage, final Thread newThread) {
		stage.setOwningThread(newThread);
	}

	public void setExceptionHandler(final AbstractStage stage, final AbstractExceptionListener exceptionHandler) {
		stage.setExceptionHandler(exceptionHandler);
	}

	public void setOwningContext(final AbstractStage stage, final ConfigurationContext context) {
		stage.setOwningContext(context);
	}

	public AbstractExceptionListener getExceptionListener(final AbstractStage stage) {
		return stage.getExceptionListener();
	}

	public boolean shouldBeTerminated(final AbstractStage stage) {
		return stage.shouldBeTerminated();
	}

	public void runStage(final AbstractStage stage) {
		try {
			while (!stage.shouldBeTerminated()) {
				stage.executeByFramework();
			}
		} catch (TerminateException e) {
			stage.abort();
			stage.getOwningContext().abortConfigurationRun();
		}
	}

	public void runStage(final AbstractStage stage, final int numOfExecutions) {
		for (int i = 0; i < numOfExecutions; i++) {
			stage.executeByFramework();
		}
	}

	public List<InputPort<?>> getInputPorts(final AbstractStage stage) {
		return stage.getInputPorts();
	}

	public List<OutputPort<?>> getOutputPorts(final AbstractStage stage) {
		return stage.getOutputPorts();
	}

}
