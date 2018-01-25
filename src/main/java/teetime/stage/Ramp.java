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
package teetime.stage;

import teetime.stage.basic.AbstractFilter;

/**
 * Sends a sequence of numbers starting from 0 to the value 'n' (not included) received by the input port, i.e., (0,1,2,..,n-1).
 *
 * @author Christian Wulf (chw)
 *
 */
public class Ramp extends AbstractFilter<Integer> {

	@Override
	protected void execute(final Integer element) throws Exception {
		int count = element;
		for (int value = 0; value < count; value++) {
			outputPort.send(value);
		}
	}

}
