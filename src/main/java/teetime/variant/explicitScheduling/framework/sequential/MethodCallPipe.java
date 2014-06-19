/***************************************************************************
 * Copyright 2014 Kieker Project (http://kieker-monitoring.net)
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
 ***************************************************************************/

package teetime.variant.explicitScheduling.framework.sequential;

import java.util.List;

import teetime.variant.explicitScheduling.framework.core.AbstractPipe;
import teetime.variant.explicitScheduling.framework.core.IInputPort;
import teetime.variant.explicitScheduling.framework.core.IOutputPort;
import teetime.variant.explicitScheduling.framework.core.IReservablePipe;
import teetime.variant.explicitScheduling.framework.core.ISink;
import teetime.variant.explicitScheduling.framework.core.ISource;

/**
 * @author Christian Wulf
 * 
 * @since 1.10
 */
public class MethodCallPipe<T> extends AbstractPipe<T> implements IReservablePipe<T> {

	private T storedToken;

	public MethodCallPipe(final T initialToken) {
		this.storedToken = initialToken;
	}

	public MethodCallPipe() {
		this.storedToken = null;
	}

	static public <S0 extends ISource, S1 extends ISink<S1>, T> void connect(final IOutputPort<S0, T> sourcePort, final IInputPort<S1, T> targetPort) {
		final MethodCallPipe<T> pipe = new MethodCallPipe<T>();
		pipe.setSourcePort(sourcePort);
		pipe.setTargetPort(targetPort);
	}

	@Override
	protected void putInternal(final T token) {
		this.storedToken = token;
		this.getTargetPort().getOwningStage().execute();
	}

	@Override
	protected T tryTakeInternal() {
		final T temp = this.storedToken;
		this.storedToken = null;
		return temp;
	}

	@Override
	public T take() {
		return this.tryTake();
	}

	@Override
	public T read() {
		return this.storedToken;
	}

	@Override
	public void putMultiple(final List<T> items) {
		throw new IllegalStateException("Putting more than one element is not possible. You tried to put " + items.size() + " items.");
	}

	@Override
	public List<?> tryTakeMultiple(final int numElementsToTake) {
		throw new IllegalStateException("Taking more than one element is not possible. You tried to take " + numElementsToTake + " items.");
	}

	public void copyAllOtherPipes(final List<MethodCallPipe<T>> pipesOfGroup) {
		// is not needed in a synchronous execution
	}

	@Override
	public boolean isEmpty() {
		return this.storedToken == null;
	}

	@Override
	public void commit() {
		// TODO Auto-generated method stub

	}

	@Override
	public void rollback() {
		// TODO Auto-generated method stub

	}

}
