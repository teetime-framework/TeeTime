package teetime.stage.basic.distributor.dynamic;


public interface PortAction<T> {

	public abstract void execute(final DynamicDistributor<T> dynamicDistributor);

}
