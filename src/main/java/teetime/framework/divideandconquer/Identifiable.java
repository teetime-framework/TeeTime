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
package teetime.framework.divideandconquer;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * An object with an unique ID.
 *
 * @author Robin Mohr
 *
 */
public class Identifiable {

	private static final AtomicInteger ID_GENERATOR = new AtomicInteger();

	private final int identifier;

	protected Identifiable() {
		this.identifier = ID_GENERATOR.incrementAndGet();
	}

	protected Identifiable(final int newID) {
		this.identifier = newID;
	}

	public int getID() {
		return this.identifier;
	}
}
