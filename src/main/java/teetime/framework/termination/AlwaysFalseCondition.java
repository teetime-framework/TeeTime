package teetime.framework.termination;

import teetime.framework.AbstractStage;
import teetime.framework.Traverser.EndOfTraverse;

class AlwaysFalseCondition implements EndOfTraverse {

	@Override
	public boolean isMet(final AbstractStage stage) {
		return false;
	}

}
