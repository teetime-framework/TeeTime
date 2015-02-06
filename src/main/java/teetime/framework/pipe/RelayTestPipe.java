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
package teetime.framework.pipe;

import teetime.framework.AbstractInterThreadPipe;
import teetime.util.ConstructorClosure;

public final class RelayTestPipe<T> extends AbstractInterThreadPipe {

	private int numInputObjects;
	private final ConstructorClosure<T> inputObjectCreator;

	public RelayTestPipe(final int numInputObjects, final ConstructorClosure<T> inputObjectCreator) {
		super(null, null);
		this.numInputObjects = numInputObjects;
		this.inputObjectCreator = inputObjectCreator;
	}

	@Override
	public boolean add(final Object element) {
		return false;
	}

	@Override
	public T removeLast() {
		if (this.numInputObjects == 0) {
			return null;
		} else {
			this.numInputObjects--;
			return this.inputObjectCreator.create();
		}
	}

	@Override
	public boolean isEmpty() {
		return (this.numInputObjects == 0);
	}

	@Override
	public int size() {
		return this.numInputObjects;
	}

	@Override
	public T readLast() {
		return null;
	}

}
