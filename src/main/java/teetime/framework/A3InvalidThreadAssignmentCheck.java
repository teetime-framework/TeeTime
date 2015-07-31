package teetime.framework;

import java.util.Set;

import teetime.framework.pipe.DummyPipe;
import teetime.framework.pipe.IPipe;

import com.carrotsearch.hppc.ObjectIntHashMap;
import com.carrotsearch.hppc.ObjectIntMap;

public class A3InvalidThreadAssignmentCheck {

	private static final int DEFAULT_COLOR = 0;

	private final Set<Stage> threadableStages;

	public A3InvalidThreadAssignmentCheck(final Set<Stage> threadableStages) {
		this.threadableStages = threadableStages;
	}

	public void check() {
		int color = DEFAULT_COLOR;
		ObjectIntMap<Stage> colors = new ObjectIntHashMap<Stage>();

		for (Stage threadableStage : threadableStages) {
			color++;
			colors.put(threadableStage, color);

			ThreadPainter threadPainter = new ThreadPainter(colors, color, threadableStages);
			threadPainter.check(threadableStage);
		}
	}

	private static class ThreadPainter {

		private final ObjectIntMap<Stage> colors;
		private final int color;
		private final Set<Stage> threadableStages;

		public ThreadPainter(final ObjectIntMap<Stage> colors, final int color, final Set<Stage> threadableStages) {
			super();
			this.colors = colors;
			this.color = color;
			this.threadableStages = threadableStages;
		}

		// TODO consider to implement it as IPipeVisitor(FORWARD)

		public void check(final Stage stage) {
			for (OutputPort<?> outputPort : stage.getOutputPorts()) {
				if (outputPort.pipe != DummyPipe.INSTANCE) {
					Stage nextStage = checkPipe(outputPort.pipe);
					if (nextStage != null) {
						check(nextStage);
					}
				}
			}
		}

		private Stage checkPipe(final IPipe<?> pipe) {
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
				return targetStage;
			}
			return null;
		}

	}
}
