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
package teetime.util;

public abstract class StacklessException extends RuntimeException {

	private static final long serialVersionUID = -9040980547278981254L;

	public StacklessException(final String string) {
		super(string);
	}

	@Override
	public synchronized Throwable fillInStackTrace() { // NOPMD, greatly improves performance when constructing
		return this;
	}

}
