package teetime.framework.scheduling.globaltaskpool;

import java.util.concurrent.Phaser;

public class CountDownAndUpLatch {

	private final Phaser phaser = new Phaser();

	private final Object lock = new Object();
	private int counter;

	public void countUp() {
		// phaser.register();
		synchronized (lock) {
			counter++;
		}
	}

	public void countDown() {
		// phaser.arriveAndDeregister();
		// System.out.println(String.format("(CountDownAndUpLatch) Unarrived: %s, Registered: %s", phaser.getUnarrivedParties(), phaser.getRegisteredParties()));
		synchronized (lock) {
			counter--;
			if (counter == 0) {
				lock.notifyAll();
			}
		}
		System.out.println(String.format("(CountDownAndUpLatch) %s countDown: %s", Thread.currentThread(), counter));
	}

	public void await() {
		// phaser.arriveAndAwaitAdvance();
		synchronized (lock) {
			while (counter > 0) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					throw new IllegalStateException(e);
				}
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
