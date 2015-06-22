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
package teetime.stage.basic.distributor;

import teetime.framework.OutputPort;
import teetime.framework.Stage;

/**
 * @author Nils Christian Ehmke
 *
 * @since 1.0
 */
public final class CopyByReferenceStrategy implements IDistributorStrategy {

	@Override
	public <T> boolean distribute(final OutputPort<T>[] outputPorts, final T element) {
		for (final OutputPort<T> outputPort : outputPorts) {
			outputPort.send(element);
		}

		return true;
	}

	@Override
	public void onOutputPortRemoved(final Stage stage, final OutputPort<?> removedOutputPort) {
		// do nothing
	}
}
