package teetime.framework.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teetime.framework.OutputPort;

public class StageTestResult {

	private final Map<OutputPort<?>, List<?>> elementsPerPort = new HashMap<>();

	public <T> void add(final OutputPort<T> port, final List<T> outputElements) {
		elementsPerPort.put(port, outputElements);
	}

	@SuppressWarnings("unchecked")
	public <T> List<? extends T> getElementsFrom(final OutputPort<T> outputPort) {
		return (List<T>) elementsPerPort.get(outputPort);
	}

}
