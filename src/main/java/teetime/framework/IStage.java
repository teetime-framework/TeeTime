package teetime.framework;

import java.util.List;

import teetime.framework.validation.InvalidPortConnection;

@Deprecated
public interface IStage {

	public String getId();

	public IStage getParentStage();

	public void setParentStage(IStage parentStage, int index);

	/**
	 *
	 * @param invalidPortConnections
	 *            <i>(Passed as parameter for performance reasons)</i>
	 */
	public void validateOutputPorts(List<InvalidPortConnection> invalidPortConnections);
}
