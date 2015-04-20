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
package teetime.util.list;

public final class CircularList<T> {

	private static final class Node<T> {
		T value;
		Node<T> next;
	}

	private Node<T> headNode;
	private Node<T> lastNode;

	private Node<T> currentNode;

	public void add(final T value) {
		Node<T> newNode = new Node<T>();
		newNode.value = value;

		if (this.headNode == null) { // newNode is the first node
			this.headNode = this.lastNode = newNode;
			this.currentNode = newNode;
		}

		this.lastNode.next = newNode;
		newNode.next = this.headNode;
	}

	public T getNext() {
		T value = this.currentNode.value;
		this.currentNode = this.currentNode.next;
		return value;
	}
}
