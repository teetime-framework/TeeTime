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
package teetime.stage;

import teetime.framework.AbstractConsumerStage;
import teetime.framework.OutputPort;

public final class Relay<T> extends AbstractConsumerStage<T> {

	// private final InputPort<T> inputPort = this.createInputPort();
	private final OutputPort<T> outputPort = this.createOutputPort();

	// private AbstractInterThreadPipe cachedCastedInputPipe;

	@Override
	protected void execute(final T element) {
		if (null == element) {
			// if (this.cachedCastedInputPipe.getSignal() instanceof TerminatingSignal) {
			// this.terminate();
			// }
			// Thread.yield();
			// return;
			logger.trace("relay: returnNoElement");
			returnNoElement();
		}
		outputPort.send(element);
	}

	// @Override
	// public void onStarting() throws Exception {
	// super.onStarting();
	// this.cachedCastedInputPipe = (AbstractInterThreadPipe) this.inputPort.getPipe();
	// }

	public OutputPort<T> getOutputPort() {
		return outputPort;
	}

}
