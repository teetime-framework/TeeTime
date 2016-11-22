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
package teetime.framework;

/**
 *
 * @author Christian Wulf
 *
 * @param <T>
 *            the type of elements to be received
 *
 * @since 1.2
 */
public final class DynamicInputPort<T> extends InputPort<T> {

	private int index;

	DynamicInputPort(final Class<T> type, final AbstractStage owningStage, final int index) {
		super(type, owningStage, null);
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(final int index) {
		this.index = index;
	}

}
