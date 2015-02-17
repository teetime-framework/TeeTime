package teetime.stage.string;

import teetime.framework.AbstractConsumerStage;
import teetime.framework.OutputPort;

/**
 * Receives a string and passes it on to the next stage only with lower case letters.
 *
 * @author Nelson Tavares de Sousa
 *
 */
public class ToLowerCase extends AbstractConsumerStage<String> {

	private final OutputPort<String> outputPort = this.createOutputPort();

	@Override
	protected void execute(final String element) {
		outputPort.send(element.toLowerCase());

	}

}
