/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://christianwulf.github.io/teetime)
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
package teetime.util;

public final class ThreadThrowableContainer {

	private final Thread first;
	private final Throwable second;

	public ThreadThrowableContainer(final Thread first, final Throwable second) {
		this.first = first;
		this.second = second;
	}

	public static ThreadThrowableContainer of(final Thread first, final Throwable second) {
		return new ThreadThrowableContainer(first, second);
	}

	public Thread getThread() {
		return this.first;
	}

	public Throwable getThrowable() {
		return this.second;
	}

	@Override
	public String toString() {
		return second.getClass().getName() + " in " + getThread() + ": " + second.getLocalizedMessage();
	}

}
