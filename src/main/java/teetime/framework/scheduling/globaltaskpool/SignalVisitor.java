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
package teetime.framework.scheduling.globaltaskpool;

import teetime.framework.AbstractPort;
import teetime.framework.AbstractStage;
import teetime.framework.ITraverserVisitor;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.Traverser.VisitorBehavior;
import teetime.framework.pipe.AbstractSynchedPipe;
import teetime.framework.pipe.DummyPipe;
import teetime.framework.signal.ISignal;

public class SignalVisitor implements ITraverserVisitor {

	private final ISignal signal;

	public SignalVisitor(final ISignal signal) {
		this.signal = signal;
	}

	@Override
	public VisitorBehavior visit(final AbstractStage stage) {
		if (stage.isProducer()) {
			stage.onSignal(signal, null);
		}
		return VisitorBehavior.CONTINUE_FORWARD;
	}

	@Override
	public VisitorBehavior visit(final AbstractPort<?> port) {
		if (!(port instanceof OutputPort)) {
			throw new IllegalStateException("Unexpected port type: " + port.getClass());
		}
		InputPort<?> inputPort = port.getPipe().getTargetPort(); // NOPMD necessary due to potential side effect

		// drain signal from internal signal queue
		AbstractSynchedPipe<?> synchedPipe = (AbstractSynchedPipe<?>) port.getPipe();
		ISignal receivedSignal = synchedPipe.getSignal();
		if (null == receivedSignal) {
			return VisitorBehavior.CONTINUE_FORWARD;
		}
		if (receivedSignal != signal) { // NOPMD must test for identity
			throw new IllegalStateException("Unexpected signal: " + receivedSignal);
		}
		inputPort.getOwningStage().onSignal(receivedSignal, inputPort);
		return VisitorBehavior.CONTINUE_FORWARD;
	}

	@Override
	public void visit(final DummyPipe pipe, final AbstractPort<?> port) {
		// do nothing
	}

}
