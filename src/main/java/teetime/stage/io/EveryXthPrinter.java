package teetime.stage.io;

import java.util.List;

import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.Stage;
import teetime.framework.TerminationStrategy;
import teetime.framework.pipe.IPipeFactory;
import teetime.framework.pipe.PipeFactoryRegistry;
import teetime.framework.pipe.PipeFactoryRegistry.PipeOrdering;
import teetime.framework.pipe.PipeFactoryRegistry.ThreadCommunication;
import teetime.framework.signal.ISignal;
import teetime.framework.validation.InvalidPortConnection;
import teetime.stage.EveryXthStage;
import teetime.stage.basic.distributor.CopyByReferenceStrategy;
import teetime.stage.basic.distributor.Distributor;

public final class EveryXthPrinter<T> extends Stage {

	private final Distributor<T> distributor;

	public EveryXthPrinter(final int threshold) {
		distributor = new Distributor<T>();
		EveryXthStage<T> everyXthStage = new EveryXthStage<T>(threshold);
		Printer<Integer> printer = new Printer<Integer>();

		IPipeFactory pipeFactory = PipeFactoryRegistry.INSTANCE.getPipeFactory(ThreadCommunication.INTRA, PipeOrdering.ARBITRARY, false);
		pipeFactory.create(distributor.getNewOutputPort(), everyXthStage.getInputPort());
		pipeFactory.create(everyXthStage.getOutputPort(), printer.getInputPort());

		distributor.setStrategy(new CopyByReferenceStrategy<T>());
	}

	@Override
	protected void executeWithPorts() {
		distributor.executeWithPorts();
	}

	@Override
	public void validateOutputPorts(final List<InvalidPortConnection> invalidPortConnections) {
		distributor.validateOutputPorts(invalidPortConnections);
	}

	@Override
	protected void onSignal(final ISignal signal, final InputPort<?> inputPort) {
		distributor.onSignal(signal, inputPort);
	}

	@Override
	protected TerminationStrategy getTerminationStrategy() {
		return distributor.getTerminationStrategy();
	}

	@Override
	protected void terminate() {
		distributor.terminate();
	}

	@Override
	protected boolean shouldBeTerminated() {
		return distributor.shouldBeTerminated();
	}

	public InputPort<T> getInputPort() {
		return distributor.getInputPort();
	}

	public OutputPort<T> getNewOutputPort() {
		return distributor.getNewOutputPort();
	}

}
