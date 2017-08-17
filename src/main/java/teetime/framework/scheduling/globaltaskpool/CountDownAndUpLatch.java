package teetime.framework.scheduling.globaltaskpool;

import java.util.concurrent.Phaser;

public class CountDownAndUpLatch {

	private final Phaser phaser = new Phaser();

	public void countUp() {
		phaser.register();
	}

	public void countDown() {
		phaser.arriveAndDeregister();
		System.out.println(String.format("(CountDownAndUpLatch) Unarrived: %s, Registered: %s", phaser.getUnarrivedParties(), phaser.getRegisteredParties()));
	}

	public void await() {
		phaser.arriveAndAwaitAdvance();
	}

	public int getCurrentCount() {
		// return phaser.getRegisteredParties();
		return phaser.getUnarrivedParties();
	}

}
