/**
 * Copyright (C) 2015 TeeTime (http://teetime.sourceforge.net)
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
import java.util.List;

import teetime.framework.pipe.IPipeFactory;
import teetime.framework.pipe.PipeFactoryRegistry;
import teetime.framework.pipe.PipeFactoryRegistry.PipeOrdering;
import teetime.framework.pipe.PipeFactoryRegistry.ThreadCommunication;
import teetime.framework.signal.ISignal;
import teetime.framework.validation.InvalidPortConnection;

/**
 * Represents a minimal stage that composes several other stages.
 *
 * @since 1.1
 * @author Christian Wulf, Nelson Tavares de Sousa
 *
 */
@SuppressWarnings("PMD.AbstractNaming")
public abstract class AbstractCompositeStage extends Stage {

	private static final IPipeFactory INTRA_PIPE_FACTORY = PipeFactoryRegistry.INSTANCE
			.getPipeFactory(ThreadCommunication.INTRA, PipeOrdering.ARBITRARY, false);

	protected abstract Stage getFirstStage();

	protected abstract Collection<? extends Stage> getLastStages();

	@Override
	protected final void executeStage() {
		getFirstStage().executeStage();
	}

	@Override
	protected final void onSignal(final ISignal signal, final InputPort<?> inputPort) {
		getFirstStage().onSignal(signal, inputPort);
	}

	@Override
	protected final TerminationStrategy getTerminationStrategy() {
		return getFirstStage().getTerminationStrategy();
	}

	@Override
	protected final void terminate() {
		getFirstStage().terminate();
	}

	@Override
	protected final boolean shouldBeTerminated() {
		return getFirstStage().shouldBeTerminated();
	}

	@Override
	protected final InputPort<?>[] getInputPorts() {
		return getFirstStage().getInputPorts();
	}

	@Override
	public final StageState getCurrentState() {
		return getFirstStage().getCurrentState();
	}

	@Override
	public final void validateOutputPorts(final List<InvalidPortConnection> invalidPortConnections) {
		for (final Stage s : getLastStages()) {
			s.validateOutputPorts(invalidPortConnections);
		}
	}

	@Override
	final void setOwningThread(final Thread owningThread) {
		getFirstStage().setOwningThread(owningThread);
		super.setOwningThread(owningThread);
	}

	protected static <T> void connectStages(final OutputPort<? extends T> out, final InputPort<T> in) {
		INTRA_PIPE_FACTORY.create(out, in);
	}

	@Override
	public final Thread getOwningThread() {
		return getFirstStage().getOwningThread();
	}

	@Override
	public final void onValidating(final List<InvalidPortConnection> invalidPortConnections) {
		getFirstStage().onValidating(invalidPortConnections);
	}

	@Override
	public final void onStarting() throws Exception {
		getFirstStage().onStarting();
	}

	@Override
	public final void onTerminating() throws Exception {
		getFirstStage().onTerminating();
	}

}