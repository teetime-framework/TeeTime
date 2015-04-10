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
package teetime.examples.loopStage;

import teetime.framework.AbstractProducerStage;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;

public class Countdown extends AbstractProducerStage<Void> {

	private final InputPort<Integer> countdownInputPort = this.createInputPort();

	private final OutputPort<Integer> newCountdownOutputPort = this.createOutputPort();

	private final Integer initialCountdown;

	public Countdown(final Integer initialCountdown) {
		this.initialCountdown = initialCountdown;
	}

	@Override
	public void onStarting() throws Exception {
		super.onStarting();
		this.countdownInputPort.getPipe().add(this.initialCountdown);
	}

	@Override
	protected void execute() {
		Integer countdown = this.countdownInputPort.receive();
		if (countdown == 0) {
			outputPort.send(null);
			this.terminate();
		} else {
			newCountdownOutputPort.send(--countdown);
		}
	}

	public InputPort<Integer> getCountdownInputPort() {
		return this.countdownInputPort;
	}

	public OutputPort<Integer> getNewCountdownOutputPort() {
		return this.newCountdownOutputPort;
	}

}
