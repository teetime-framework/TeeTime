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

import teetime.framework.pipe.IPipe;

/**
 *
 * @author Christian Wulf (chw)
 *
 * @param <T>
 *            the type of the elements which this port accepts
 */
public class AbstractPort<T> {

	protected static final Object TERMINATE_ELEMENT = new Object();

	protected IPipe<?> pipe;
	/**
	 * The type of this port.
	 * <p>
	 * <i>Used to validate the connection between two ports at runtime.</i>
	 * </p>
	 */
	private final Class<T> type;
	private final AbstractStage owningStage;
	private final String name;

	protected AbstractPort(final Class<T> type, final AbstractStage owningStage, final String name) {
		super();
		this.type = type;
		this.owningStage = owningStage;
		this.name = name;
	}

	public Class<T> getType() {
		return this.type;
	}

	public AbstractStage getOwningStage() {
		return owningStage;
	}

	public IPipe<?> getPipe() {
		return this.pipe;
	}

	public void setPipe(final IPipe<?> pipe) {
		this.pipe = pipe;
	}

	public String getName() {
		return name;
	}

	@SuppressWarnings("PMD.ConfusingTernary")
	@Override
	public String toString() {
		return (name != null) ? name : super.toString();
	}

}
