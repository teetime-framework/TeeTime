package teetime.framework.scheduling.globaltaskqueue;

import teetime.framework.AbstractPort;
import teetime.framework.AbstractStage;
import teetime.framework.ITraverserVisitor;
import teetime.framework.Traverser.VisitorBehavior;
import teetime.framework.pipe.DummyPipe;

class LevelIndexVisitor implements ITraverserVisitor {

	private int maxLevelIndex;

	@Override
	public VisitorBehavior visit(final AbstractStage stage) {
		return VisitorBehavior.CONTINUE_FORWARD;
	}

	@Override
	public VisitorBehavior visit(final AbstractPort<?> port) {
		AbstractStage sourceStage = port.getOwningStage();
		AbstractStage targetStage = port.getPipe().getTargetPort().getOwningStage();

		int levelIndex = Math.max(targetStage.getLevelIndex(), sourceStage.getLevelIndex() + 1);
		targetStage.setLevelIndex(levelIndex);

		maxLevelIndex = Math.max(maxLevelIndex, levelIndex);

		return VisitorBehavior.CONTINUE_FORWARD;
	}

	@Override
	public void visit(final DummyPipe pipe, final AbstractPort<?> port) {
		// do nothing
	}

	public int getMaxLevelIndex() {
		return maxLevelIndex;
	}

}
