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
package teetime.stage.string.buffer.handler;

import org.slf4j.Logger;

import teetime.stage.string.buffer.util.KiekerHashMap;

/**
 * @author Christian Wulf
 *
 * @since 1.10
 */
public abstract class AbstractDataTypeHandler<T> {

	protected Logger logger;
	protected KiekerHashMap stringRepository;

	/**
	 * @since 1.10
	 */
	public abstract boolean canHandle(Object object);

	/**
	 * @since 1.10
	 */
	public abstract T handle(T object);

	/**
	 * @since 1.10
	 */
	public void setLogger(final Logger logger) {
		this.logger = logger;
	}

	/**
	 * @since 1.10
	 */
	public void setStringRepository(final KiekerHashMap stringRepository) {
		this.stringRepository = stringRepository;
	}

}
