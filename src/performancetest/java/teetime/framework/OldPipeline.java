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

import java.util.List;

import teetime.framework.signal.ISignal;
import teetime.framework.validation.InvalidPortConnection;

@Deprecated
public class OldPipeline<FirstStage extends Stage, LastStage extends Stage> extends Stage {

	protected FirstStage firstStage;
	protected LastStage lastStage;

	public FirstStage getFirstStage() {
		return this.firstStage;
	}

	public void setFirstStage(final FirstStage firstStage) {
		this.firstStage = firstStage;
	}

	public LastStage getLastStage() {
		return this.lastStage;
	}

	public void setLastStage(final LastStage lastStage) {
		this.lastStage = lastStage;
	}

	@Override
	public void executeWithPorts() {
		this.firstStage.executeWithPorts();
	}

	@Override
	public void onSignal(final ISignal signal, final InputPort<?> inputPort) {
		this.firstStage.onSignal(signal, inputPort);
	}

	@Override
	public void validateOutputPorts(final List<InvalidPortConnection> invalidPortConnections) {
		this.lastStage.validateOutputPorts(invalidPortConnections);
	}

	@Override
	public void terminate() {
		firstStage.terminate();
	}

	@Override
	public boolean shouldBeTerminated() {
		return firstStage.shouldBeTerminated();
	}

	@Override
	protected InputPort<?>[] getInputPorts() {
		return firstStage.getInputPorts();
	}

	@Override
	public Thread getOwningThread() {
		return firstStage.getOwningThread();
	}

	@Override
	void setOwningThread(final Thread owningThread) {
		firstStage.setOwningThread(owningThread);
	}

	@Override
	public TerminationStrategy getTerminationStrategy() {
		return firstStage.getTerminationStrategy();
	}

	@Override
	protected boolean isStarted() {
		return firstStage.isStarted();
	}

}
