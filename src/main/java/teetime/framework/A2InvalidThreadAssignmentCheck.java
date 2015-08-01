package teetime.framework;

import java.util.Set;

import teetime.framework.Traverser.VisitorBehavior;
import teetime.framework.pipe.IPipe;

import com.carrotsearch.hppc.ObjectIntHashMap;
import com.carrotsearch.hppc.ObjectIntMap;

public class A2InvalidThreadAssignmentCheck {

	private static final int DEFAULT_COLOR = 0;

	private final Set<Stage> threadableStages;

	public A2InvalidThreadAssignmentCheck(final Set<Stage> threadableStages) {
		this.threadableStages = threadableStages;
	}

	public void check() {
		int color = DEFAULT_COLOR;
		ObjectIntMap<Stage> colors = new ObjectIntHashMap<Stage>();

		for (Stage threadableStage : threadableStages) {
			color++;
			colors.put(threadableStage, color);

			ThreadPainter threadPainter = new ThreadPainter(colors, color, threadableStages);
			Traverser traverser = new Traverser(threadPainter);
			traverser.traverse(threadableStage);
		}
	}

	private static class ThreadPainter implements ITraverserVisitor {

		private final ObjectIntMap<Stage> colors;
		private final int color;
		private final Set<Stage> threadableStages;

		public ThreadPainter(final ObjectIntMap<Stage> colors, final int color, final Set<Stage> threadableStages) {
			super();
			this.colors = colors;
			this.color = color;
			this.threadableStages = threadableStages;
		}

		@Override
		public VisitorBehavior visit(final Stage stage) {
			return VisitorBehavior.CONTINUE;
		}

		@Override
		public VisitorBehavior visit(final AbstractPort<?> port) {
			IPipe<?> pipe = port.getPipe();
			// FIXME line below requires FORWARD. should be independent of the used direction
			Stage targetStage = pipe.getTargetPort().getOwningStage();

			int targetColor = colors.containsKey(targetStage) ? colors.get(targetStage) : DEFAULT_COLOR;

			if (threadableStages.contains(targetStage) && targetColor != color) {
				// do nothing
			} else {
				if (colors.containsKey(targetStage)) {
					if (colors.get(targetStage) != color) {
						throw new IllegalStateException("Crossing threads"); // One stage is connected to a stage of another thread (but not its "headstage")
					}
				}
				colors.put(targetStage, color);
				return VisitorBehavior.CONTINUE;
			}
			return VisitorBehavior.STOP;
		}

	}
}
