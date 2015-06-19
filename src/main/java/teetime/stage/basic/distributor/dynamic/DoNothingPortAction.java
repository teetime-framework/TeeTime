package teetime.stage.basic.distributor.dynamic;

public class DoNothingPortAction<T> implements PortAction<T> {

	@Override
	public void execute(final DynamicDistributor<T> dynamicDistributor) {
		// do nothing for testing purpose
	}

}
