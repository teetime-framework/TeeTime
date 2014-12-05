package teetime.stage.io;

import java.util.List;

import teetime.framework.InputPort;
import teetime.framework.Stage;
import teetime.framework.TerminationStrategy;
import teetime.framework.pipe.IPipeFactory;
import teetime.framework.pipe.PipeFactoryRegistry;
import teetime.framework.pipe.PipeFactoryRegistry.PipeOrdering;
import teetime.framework.pipe.PipeFactoryRegistry.ThreadCommunication;
import teetime.framework.signal.ISignal;
import teetime.framework.validation.InvalidPortConnection;
import teetime.stage.EveryXthStage;

public final class EveryXthPrinter<T> extends Stage {

	private final EveryXthStage<T> everyXthStage;
	private final Printer<T> printer;

	public EveryXthPrinter(final int threshold) {
		everyXthStage = new EveryXthStage<T>(threshold);
		printer = new Printer<T>();

		IPipeFactory pipeFactory = PipeFactoryRegistry.INSTANCE.getPipeFactory(ThreadCommunication.INTRA, PipeOrdering.ARBITRARY, false);
		pipeFactory.create(everyXthStage.getOutputPort(), printer.getInputPort());
	}

	@Override
	protected void executeWithPorts() {
		everyXthStage.executeWithPorts();
	}

	@Override
	public void validateOutputPorts(final List<InvalidPortConnection> invalidPortConnections) {
		printer.validateOutputPorts(invalidPortConnections);
	}

	@Override
	protected void onSignal(final ISignal signal, final InputPort<?> inputPort) {
		everyXthStage.onSignal(signal, inputPort);
	}

	@Override
	protected TerminationStrategy getTerminationStrategy() {
		return everyXthStage.getTerminationStrategy();
	}

	@Override
	protected void terminate() {
		everyXthStage.terminate();
	}

	@Override
	protected boolean shouldBeTerminated() {
		return everyXthStage.shouldBeTerminated();
	}

}
