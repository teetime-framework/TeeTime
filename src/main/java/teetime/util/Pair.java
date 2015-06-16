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
package teetime.util;

/**
 * @deprecated since 1.2
 */
@Deprecated
// See http://stackoverflow.com/questions/156275/what-is-the-equivalent-of-the-c-pairl-r-in-java
public final class Pair<F, S> {

	private final F first;
	private final S second;

	public Pair(final F first, final S second) {
		this.first = first;
		this.second = second;
	}

	public static <F, S> Pair<F, S> of(final F first, final S second) {
		return new Pair<F, S>(first, second);
	}

	public F getFirst() {
		return this.first;
	}

	public S getSecond() {
		return this.second;
	}

}
