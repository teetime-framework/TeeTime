/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime-framework.github.io)
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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import teetime.framework.exceptionHandling.AbstractExceptionListenerFactory;
import teetime.framework.exceptionHandling.TerminatingExceptionListenerFactory;

/**
 * Represents a configuration of connected stages. Available to be extended.
 *
 * @author Christian Wulf, Nelson Tavares de Sousa
 *
 * @since 2.0
 *
 */
public class Configuration extends CompositeStage {

	private final AbstractExceptionListenerFactory<?> factory;
	private final ConfigurationContext context;
	private final Set<AbstractStage> startStages;

	private boolean initialized;
	private boolean executed;

	public Configuration() {
		this(new TerminatingExceptionListenerFactory());
	}

	public Configuration(final AbstractExceptionListenerFactory<?> factory) {
		this.factory = factory;
		this.context = new ConfigurationContext(this);
		this.startStages = new HashSet<AbstractStage>();
	}

	boolean isInitialized() {
		return initialized;
	}

	void setInitialized(final boolean executed) {
		this.initialized = executed;
	}

	boolean isExecuted() {
		return executed;
	}

	void setExecuted(final boolean executed) {
		this.executed = executed;
	}

	AbstractExceptionListenerFactory<?> getFactory() {
		return factory;
	}

	/**
	 * Register pipes if your configuration only relies on custom pipes and therefore {@link #connectPorts(OutputPort, InputPort)} is never called.
	 *
	 * @param pipe
	 *            A custom pipe instance
	 */
	public void registerCustomPipe(final AbstractPipe<?> pipe) {
		startStages.add(pipe.getSourcePort().getOwningStage()); // memorize all source stages as starting point for traversing
	}

	@Override
	public <T> void connectPorts(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort, final int capacity) {
		startStages.add(sourcePort.getOwningStage()); // memorize all source stages as starting point for traversing
		super.connectPorts(sourcePort, targetPort, capacity);
	}

	ConfigurationContext getContext() {
		return context;
	}

	public Collection<AbstractStage> getStartStages() {
		return startStages;
	}

}
