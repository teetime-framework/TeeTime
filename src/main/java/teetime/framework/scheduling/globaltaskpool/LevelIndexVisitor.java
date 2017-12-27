package teetime.framework.scheduling.globaltaskpool;

import teetime.framework.AbstractPort;
import teetime.framework.AbstractStage;
import teetime.framework.ITraverserVisitor;
import teetime.framework.StageFacade;
import teetime.framework.Traverser.VisitorBehavior;
import teetime.framework.pipe.DummyPipe;

class LevelIndexVisitor implements ITraverserVisitor {

	private static final StageFacade STAGE_FACADE = StageFacade.INSTANCE;

	private int maxLevelIndex;

	@Override
	public VisitorBehavior visit(final AbstractStage stage) {
		return VisitorBehavior.CONTINUE_FORWARD;
	}

	@Override
	public VisitorBehavior visit(final AbstractPort<?> port) {
		AbstractStage sourceStage = port.getOwningStage();
		AbstractStage targetStage = port.getPipe().getTargetPort().getOwningStage();

		int targetLevelIndex = STAGE_FACADE.getLevelIndex(targetStage);
		int sourceLevelIndex = STAGE_FACADE.getLevelIndex(sourceStage);
		int levelIndex = Math.max(targetLevelIndex, sourceLevelIndex + 1);
		STAGE_FACADE.setLevelIndex(targetStage, levelIndex);

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
