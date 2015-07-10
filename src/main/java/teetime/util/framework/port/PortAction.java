package teetime.util.framework.port;

import teetime.framework.Stage;

public interface PortAction<T extends Stage> {

	public abstract void execute(final T stage);

}
