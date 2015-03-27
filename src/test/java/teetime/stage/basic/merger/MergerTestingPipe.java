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
package teetime.stage.basic.merger;

import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.pipe.IPipe;
import teetime.framework.signal.ISignal;
import teetime.framework.signal.StartingSignal;
import teetime.framework.signal.TerminatingSignal;

public class MergerTestingPipe implements IPipe {

	private boolean startSent = false;
	private boolean terminateSent = false;

	public MergerTestingPipe() {}

	@Override
	public void sendSignal(final ISignal signal) {
		if (signal.getClass().equals(StartingSignal.class)) {
			this.startSent = true;
		} else if (signal.getClass().equals(TerminatingSignal.class)) {
			this.terminateSent = true;
		}
	}

	public boolean startSent() {
		return this.startSent;
	}

	public boolean terminateSent() {
		return this.terminateSent;
	}

	public void reset() {
		this.startSent = false;
		this.terminateSent = false;
	}

	@Override
	public boolean add(final Object element) {
		return false;
	}

	@Override
	public boolean addNonBlocking(final Object element) {
		return add(element);
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object removeLast() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputPort<?> getTargetPort() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> void connectPorts(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reportNewElement() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isClosed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasMore() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void waitForStartSignal() throws InterruptedException {
		// TODO Auto-generated method stub
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
	}

}
