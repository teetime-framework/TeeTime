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

	private boolean executed;

	private final IExceptionListenerFactory factory;

	boolean isExecuted() {
		return executed;
	}

	void setExecuted(final boolean executed) {
		this.executed = executed;
	}

	public IExceptionListenerFactory getFactory() {
		return factory;
	}

	protected Configuration() {
		this(new TerminatingExceptionListenerFactory());
	}

	protected Configuration(final IExceptionListenerFactory factory) {
		this.factory = factory;
	}
}
