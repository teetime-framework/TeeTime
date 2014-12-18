package teetime.stage.basic.merger;

import org.junit.Assert;
import org.junit.Test;

import teetime.framework.InputPort;
import teetime.framework.signal.StartingSignal;
import teetime.framework.signal.TerminatingSignal;

public class MergerSignalTest {

	private Merger<Integer> merger;
	private InputPort<Integer> firstPort;
	private InputPort<Integer> secondPort;
	private MergerTestingPipe testPipe;

	public void beforeSignalTesting() {
		merger = new Merger<Integer>();

		firstPort = merger.getNewInputPort();
		secondPort = merger.getNewInputPort();

		testPipe = new MergerTestingPipe();
		merger.getOutputPort().setPipe(testPipe);
	}

	@Test
	public void testSameSignal() {
		this.beforeSignalTesting();
		merger.onSignal(new StartingSignal(), firstPort);
		Assert.assertFalse(testPipe.startSent());

		merger.onSignal(new StartingSignal(), secondPort);
		Assert.assertTrue(testPipe.startSent());
	}

	@Test
	public void testDifferentSignals() {
		this.beforeSignalTesting();
		merger.onSignal(new StartingSignal(), firstPort);
		Assert.assertFalse(testPipe.startSent());

		merger.onSignal(new TerminatingSignal(), secondPort);
		Assert.assertFalse(testPipe.startSent());
	}

	@Test
	public void testInterleavedSignals() {
		this.beforeSignalTesting();
		merger.onSignal(new StartingSignal(), firstPort);
		Assert.assertFalse(testPipe.startSent());
		Assert.assertFalse(testPipe.terminateSent());

		merger.onSignal(new TerminatingSignal(), secondPort);
		Assert.assertFalse(testPipe.startSent());
		Assert.assertFalse(testPipe.terminateSent());

		merger.onSignal(new TerminatingSignal(), firstPort);
		Assert.assertFalse(testPipe.startSent());
		Assert.assertTrue(testPipe.terminateSent());

		merger.onSignal(new TerminatingSignal(), firstPort);
		Assert.assertFalse(testPipe.startSent());
		Assert.assertTrue(testPipe.terminateSent());

		merger.onSignal(new StartingSignal(), secondPort);
		Assert.assertTrue(testPipe.startSent());
		Assert.assertTrue(testPipe.terminateSent());
	}

	@Test
	public void testMultipleSignals() {
		this.beforeSignalTesting();
		merger.onSignal(new StartingSignal(), firstPort);
		Assert.assertFalse(testPipe.startSent());

		merger.onSignal(new StartingSignal(), firstPort);
		Assert.assertFalse(testPipe.startSent());

		merger.onSignal(new StartingSignal(), firstPort);
		Assert.assertFalse(testPipe.startSent());

		merger.onSignal(new StartingSignal(), secondPort);
		Assert.assertTrue(testPipe.startSent());
	}
}
