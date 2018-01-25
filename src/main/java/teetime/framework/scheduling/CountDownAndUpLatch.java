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
package teetime.framework.scheduling;

public class CountDownAndUpLatch {

	// private final Phaser phaser = new Phaser();

	private final Object lock = new Object();
	private int counter;

	/**
	 * Increases the counter by 1 in a thread-safe manner.
	 */
	public void countUp() {
		// phaser.register();
		synchronized (lock) {
			counter++;
		}
	}

	/**
	 * Decreases the counter by 1 in a thread-safe manner.
	 * <p>
	 * Notifies all waiting threads if the counter becomes zero.
	 */
	public void countDown() {
		// phaser.arriveAndDeregister();
		synchronized (lock) {
			counter--;
			if (counter == 0) {
				lock.notifyAll();
			}
		}
	}

	/**
	 * Waits for the counter to become non-positive.
	 *
	 * @throws InterruptedException
	 */
	public void await() throws InterruptedException {
		// phaser.arriveAndAwaitAdvance();
		synchronized (lock) {
			while (counter > 0) {
				lock.wait();
			}
		}
	}

	public int getCurrentCount() {
		// return phaser.getUnarrivedParties();
		synchronized (lock) {
			return counter;
		}
	}

}
