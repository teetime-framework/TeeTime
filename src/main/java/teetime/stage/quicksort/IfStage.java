package teetime.stage.quicksort;

import teetime.framework.*;
import teetime.framework.signal.TerminatingSignal;

class IfStage extends AbstractStage {

	private final InputPort<QuicksortTaskContext> newTaskInputPort = createInputPort(QuicksortTaskContext.class);
	private final InputPort<QuicksortTaskContext> subTaskInputPort = createInputPort(QuicksortTaskContext.class);

	private final OutputPort<QuicksortTaskContext> trueOutputPort = createOutputPort(QuicksortTaskContext.class);
	private final OutputPort<int[]> falseOutputPort = createOutputPort(int[].class);

	// private final Set<QuicksortTaskContext> currentTasks = new HashSet<>();
	private int numCurrentTasks;

	@Override
	protected void execute() {
		QuicksortTaskContext subTask = subTaskInputPort.receive();
		if (subTask != null) {
			send(subTask);
			return;
		}

		QuicksortTaskContext newTask = newTaskInputPort.receive();
		if (newTask != null) {
			// currentTasks.add(newTask);
			numCurrentTasks++;
			send(newTask);
			return;
		}
	}

	private void send(QuicksortTaskContext context) {
		if (context.getTop() >= 0) {
			trueOutputPort.send(context);
		} else {
			falseOutputPort.send(context.getElements());

			// currentTasks.remove(context);
			numCurrentTasks--;

			// if (newTaskInputPort.isClosed() && currentTasks.isEmpty()) {
			if (newTaskInputPort.isClosed() && numCurrentTasks == 0) {
				falseOutputPort.sendSignal(new TerminatingSignal());
			}
		}
	}

	public InputPort<QuicksortTaskContext> getNewTaskInputPort() {
		return newTaskInputPort;
	}

	public InputPort<QuicksortTaskContext> getSubTaskInputPort() {
		return subTaskInputPort;
	}

	public OutputPort<QuicksortTaskContext> getTrueOutputPort() {
		return trueOutputPort;
	}

	public OutputPort<int[]> getFalseOutputPort() {
		return falseOutputPort;
	}

}
