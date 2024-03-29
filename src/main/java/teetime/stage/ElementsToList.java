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

import java.util.LinkedList;
import java.util.List;

import teetime.stage.basic.AbstractTransformation;

public final class ElementsToList<I> extends AbstractTransformation<I, List<I>> {

	private final int size;

	private final List<I> cachedObjects = new LinkedList<>();

	public ElementsToList(final int size) {
		this.size = size;
	}

	@Override
	protected void execute(final I element) {
		if (cachedObjects.size() < size) {
			this.logger.debug("Received element #" + this.cachedObjects.size());
			this.cachedObjects.add(element);
		} else {
			this.logger.debug("Sending cached element List to output port...");
			this.outputPort.send(cachedObjects);
		}
	}

}
