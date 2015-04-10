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
package teetime.framework;

import teetime.framework.pipe.IPipe;

public abstract class AbstractPort<T> {

	protected IPipe pipe;
	/**
	 * The type of this port.
	 * <p>
	 * <i>Used to validate the connection between two ports at runtime.</i>
	 * </p>
	 */
	protected Class<T> type;

	public IPipe getPipe() {
		return this.pipe;
	}

	public void setPipe(final IPipe pipe) {
		this.pipe = pipe;
	}

	public Class<T> getType() {
		return this.type;
	}

	public void setType(final Class<T> type) {
		this.type = type;
	}
}
