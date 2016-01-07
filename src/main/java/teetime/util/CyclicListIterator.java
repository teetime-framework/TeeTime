/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime-framework.github.io)
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

import java.util.Iterator;
import java.util.List;

/**
 * This iterator infinitely iterates over a list and allows the list to be modified without throwing a <code>ConcurrentMOdificationException</code>.
 *
 * @author Christian Wulf
 *
 * @param <T>
 *            type of the elements contained in the list
 */
public final class CyclicListIterator<T> implements Iterator<T> {

	private final List<T> elements;
	// private Iterator<T> iterator;

	private int currentIndex = 0;

	public CyclicListIterator(final List<T> elements) {
		this.elements = elements;
		// this.iterator = this.list.iterator();
	}

	@Override
	public boolean hasNext() {
		return true;
	}

	@Override
	public T next() {
		this.currentIndex = this.getCurrentIndex();
		final T element = this.elements.get(this.currentIndex);
		this.currentIndex++;
		return element;
		// if (!this.iterator.hasNext()) {
		// this.iterator = this.list.iterator();
		// }
		// return this.iterator.next();

		// the size of the list could have been changed due to
		// <li>an index overflow (then restart from index 0), or
		// <li>an add() or a remove(), so update the index
	}

	@Override
	public void remove() {
		this.currentIndex = this.getCurrentIndex();
		this.elements.remove(this.currentIndex);
		// this.iterator.remove();
	}

	private int getCurrentIndex() {
		return this.currentIndex % this.elements.size();
	}

}
