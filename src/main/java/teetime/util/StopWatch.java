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

import java.util.concurrent.TimeUnit;

public final class StopWatch {

	private long startTimeInNs;
	private long endTimeInNs;

	public void start() {
		this.startTimeInNs = System.nanoTime();
	}

	public void end() {
		this.endTimeInNs = System.nanoTime();
	}

	public long getDurationInNs() {
		return this.endTimeInNs - this.startTimeInNs;
	}

	public long getDurationInMs() {
		return getDuration(TimeUnit.MILLISECONDS);
	}

	public long getDuration(final TimeUnit timeUnit) {
		return timeUnit.convert(getDurationInNs(), TimeUnit.NANOSECONDS);
	}
}
