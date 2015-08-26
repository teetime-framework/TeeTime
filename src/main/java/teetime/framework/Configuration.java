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
package teetime.framework;

import teetime.framework.exceptionHandling.IExceptionListenerFactory;
import teetime.framework.exceptionHandling.TerminatingExceptionListenerFactory;

/**
 * Represents a configuration of connected stages. Available to be extended.
 *
 * @author Christian Wulf, Nelson Tavares de Sousa
 *
 * @since 2.0
 *
 */
public abstract class Configuration extends AbstractCompositeStage {

	private final IExceptionListenerFactory<?> factory;
	private final ConfigurationContext context;

	private boolean initialized;
	private boolean executed;
	private Stage startStage;

	protected Configuration() {
		this(new TerminatingExceptionListenerFactory());
	}

	protected Configuration(final IExceptionListenerFactory<?> factory) {
		this.factory = factory;
		this.context = new ConfigurationContext(this);
	}

	boolean isInitialized() {
		return initialized;
	}

	void setInitialized(final boolean executed) {
		this.initialized = executed;
	}

	public boolean isExecuted() {
		return executed;
	}

	public void setExecuted(final boolean executed) {
		this.executed = executed;
	}

	public IExceptionListenerFactory<?> getFactory() {
		return factory;
	}

	protected void registerCustomPipe(final AbstractPipe<?> pipe) {
		startStage = pipe.getSourcePort().getOwningStage(); // memorize an arbitrary stage as starting point for traversing
	}

	@Override
	protected <T> void connectPorts(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort, final int capacity) {
		startStage = sourcePort.getOwningStage(); // memorize an arbitrary stage as starting point for traversing
		super.connectPorts(sourcePort, targetPort, capacity);
	}

	ConfigurationContext getContext() {
		return context;
	}

	Stage getStartStage() {
		return startStage;
	}

}
