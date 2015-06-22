/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime.sourceforge.net)
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
package teetime.stage.io;

import java.util.ArrayList;
import java.util.List;

import teetime.framework.AbstractCompositeStage;
import teetime.framework.ConfigurationContext;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.Stage;
import teetime.stage.EveryXthStage;
import teetime.stage.basic.distributor.CopyByReferenceStrategy;
import teetime.stage.basic.distributor.Distributor;

public final class EveryXthPrinter<T> extends AbstractCompositeStage {

	private final Distributor<T> distributor;
	private final List<Stage> lastStages = new ArrayList<Stage>();

	public EveryXthPrinter(final int threshold, final ConfigurationContext context) {
		super(context);
		distributor = new Distributor<T>(new CopyByReferenceStrategy());
		EveryXthStage<T> everyXthStage = new EveryXthStage<T>(threshold);
		Printer<Integer> printer = new Printer<Integer>();

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

	public Stage getFirstStage() {
		return distributor;
	}

}
