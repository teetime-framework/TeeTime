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

package teetime.framework.core;

class InputPortImpl<S extends IStage, T> extends AbstractPort<S, T> implements IInputPort<S, T> {

	private volatile PortState state = PortState.OPENED;

	private IPortListener portListener;

	public InputPortImpl(final S owningStage) {
		this.setOwningStage(owningStage);
	}

	@Override
	public void setState(final PortState state) {
		this.state = state;
	}

	@Override
	public PortState getState() {
		return this.state;
	}

	@Override
	public void setPortListener(final IPortListener portListener) {
		this.portListener = portListener;
	}

	@Override
	public void close() {
		if (this.portListener == null) {
			throw new NullPointerException("stage: "+this.getOwningStage().getClass().getName()+", port="+this.getIndex());
		}
		this.portListener.onPortIsClosed(this);
	}

}
