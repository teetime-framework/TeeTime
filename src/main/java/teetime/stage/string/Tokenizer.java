/**
 * Copyright (C) 2015 TeeTime (http://teetime.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package teetime.stage.string;

import java.util.StringTokenizer;

import teetime.framework.AbstractConsumerStage;
import teetime.framework.OutputPort;

public final class Tokenizer extends AbstractConsumerStage<String> {

	private final OutputPort<String> outputPort = this.createOutputPort();
	private final String regex;

	public Tokenizer(final String regex) {
		this.regex = regex;
	}

	@Override
	protected void execute(final String element) {
		StringTokenizer st = new StringTokenizer(element, this.regex);
		while (st.hasMoreTokens()) {
			outputPort.send(st.nextToken());
		}
	}

	public OutputPort<String> getOutputPort() {
		return this.outputPort;
	}

}
