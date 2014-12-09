package teetime.examples.loopStage;

import teetime.framework.AbstractProducerStage;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;

public class Countdown extends AbstractProducerStage<Void> {

	private final InputPort<Integer> countdownInputPort = this.createInputPort();

	private final OutputPort<Integer> newCountdownOutputPort = this.createOutputPort();

	private final Integer initialCountdown;

	public Countdown(final Integer initialCountdown) {
		this.initialCountdown = initialCountdown;
	}

	@Override
	public void onStarting() throws Exception {
		super.onStarting();
		this.countdownInputPort.getPipe().add(this.initialCountdown);
	}

	@Override
	protected void execute() {
		Integer countdown = this.countdownInputPort.receive();
		if (countdown == 0) {
			outputPort.send(null);
			this.terminate();
		} else {
			newCountdownOutputPort.send(--countdown);
		}
	}

	public InputPort<Integer> getCountdownInputPort() {
		return this.countdownInputPort;
	}

	public OutputPort<Integer> getNewCountdownOutputPort() {
		return this.newCountdownOutputPort;
	}

}
