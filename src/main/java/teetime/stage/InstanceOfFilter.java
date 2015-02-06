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

/**
 * @author Jan Waller, Nils Christian Ehmke, Christian Wulf
 *
 */
public final class InstanceOfFilter<I, O> extends AbstractConsumerStage<I> {

	private final OutputPort<O> outputPort = this.createOutputPort();

	private Class<O> type;

	public InstanceOfFilter(final Class<O> type) {
		this.type = type;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void execute(final I element) {
		if (this.type.isInstance(element)) {
			outputPort.send((O) element);
		} else { // swallow up the element
			if (this.logger.isDebugEnabled()) {
				this.logger.info("element is not an instance of " + this.type.getName() + ", but of " + element.getClass());
			}
		}
	}

	public Class<O> getType() {
		return this.type;
	}

	public void setType(final Class<O> type) {
		this.type = type;
	}

	public OutputPort<O> getOutputPort() {
		return this.outputPort;
	}

}
