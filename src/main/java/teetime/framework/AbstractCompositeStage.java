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

import java.util.LinkedList;
import java.util.List;

import teetime.framework.signal.ISignal;
import teetime.framework.validation.InvalidPortConnection;
import teetime.util.Connection;

/**
 * Represents a minimal stage that composes several other stages.
 *
 * @since 1.2
 * @author Christian Wulf, Nelson Tavares de Sousa
 *
 * @deprecated This concept is not yet implemented in a correct way. As soon as the concept is stable, we will remove the deprecated tag.
 *
 */
@Deprecated
public abstract class AbstractCompositeStage extends Stage {

	private final List<Connection> connections = new LinkedList<Connection>();

	protected abstract Stage getFirstStage();

	protected <T> void connectPorts(final OutputPort<? extends T> out, final InputPort<T> in) {
		connections.add(new Connection(out, in));
	}

	public List<Connection> getConnections() {
		return connections;
	}

	@Override
	public final void validateOutputPorts(final List<InvalidPortConnection> invalidPortConnections) {
		throw new IllegalStateException("This method must never be called");

	}

	@Override
	protected final void executeStage() {
		throw new IllegalStateException("This method must never be called");
	}

	@Override
	protected final void onSignal(final ISignal signal, final InputPort<?> inputPort) {
		throw new IllegalStateException("This method must never be called");
	}

	@Override
	protected final TerminationStrategy getTerminationStrategy() {
		throw new IllegalStateException("This method must never be called");
	}

	@Override
	protected final void terminate() {
		throw new IllegalStateException("This method must never be called");
	}

	@Override
	protected final boolean shouldBeTerminated() {
		throw new IllegalStateException("This method must never be called");
	}

	@Override
	public final StageState getCurrentState() {
		throw new IllegalStateException("This method must never be called");
	}

	@Override
	protected final InputPort<?>[] getInputPorts() {
		throw new IllegalStateException("This method must never be called");
	}

	@Override
	protected final OutputPort<?>[] getOutputPorts() {
		throw new IllegalStateException("This method must never be called");
	}

	@Override
	public final void onValidating(final List<InvalidPortConnection> invalidPortConnections) {
		throw new IllegalStateException("This method must never be called");
	}

	@Override
	public final void onInitializing() throws Exception {
		throw new IllegalStateException("This method must never be called");
	}

	@Override
	public final void onStarting() throws Exception {
		throw new IllegalStateException("This method must never be called");
	}

	@Override
	public final void onTerminating() throws Exception {
		throw new IllegalStateException("This method must never be called");
	}

}
