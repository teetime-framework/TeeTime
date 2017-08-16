package teetime.framework;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class BreadthFirstTraverser {

	public void traverse(final AbstractStage startStage, final ITraverserVisitor traverserVisitor) {
		final Set<AbstractStage> visitedStages = new HashSet<AbstractStage>();
		final Queue<AbstractStage> queue = new LinkedList<>();

		queue.add(startStage);
		visitedStages.add(startStage);

		while (!queue.isEmpty()) {
			AbstractStage stage = queue.remove();
			traverserVisitor.visit(startStage);

			for (OutputPort<?> outputPort : stage.getOutputPorts()) {
				AbstractStage targetStage = outputPort.getPipe().getTargetPort().getOwningStage();

				if (!visitedStages.contains(targetStage)) {
					queue.add(targetStage);
					visitedStages.add(startStage);
				}

				traverserVisitor.visit(outputPort);
			}
		}
	}
}
