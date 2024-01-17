/**
 * Copyright © 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime-framework.github.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package teetime.stage.io;

import java.util.ArrayList;
import java.util.List;

import teetime.framework.AbstractStage;
import teetime.framework.CompositeStage;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.stage.EveryXthStage;
import teetime.stage.basic.distributor.Distributor;
import teetime.stage.basic.distributor.strategy.CopyByReferenceStrategy;

public final class EveryXthPrinter<T> extends CompositeStage {

	private final Distributor<T> distributor;
	private final List<AbstractStage> lastStages = new ArrayList<>();

	public EveryXthPrinter(final int threshold) {
		distributor = new Distributor<>(new CopyByReferenceStrategy());
		EveryXthStage<T> everyXthStage = new EveryXthStage<>(threshold);
		Printer<Integer> printer = new Printer<>();

		connectPorts(distributor.getNewOutputPort(), everyXthStage.getInputPort());
		connectPorts(everyXthStage.getOutputPort(), printer.getInputPort());

		lastStages.add(printer);
	}

	public InputPort<T> getInputPort() {
		return distributor.getInputPort();
	}

	public OutputPort<T> getNewOutputPort() {
		return distributor.getNewOutputPort();
	}

	public AbstractStage getFirstStage() {
		return distributor;
	}

}
