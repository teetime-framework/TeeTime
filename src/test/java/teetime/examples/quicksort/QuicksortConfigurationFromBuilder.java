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
package teetime.examples.quicksort;

import java.util.List;

import teetime.framework.*;
import teetime.stage.CollectorSink;
import teetime.stage.InitialElementProducer;
import teetime.stage.quicksort.QuicksortProblem;
import teetime.stage.quicksort.QuicksortSolution;

/**
 * Same configuration as {@link QuicksortConfiguration} but created with a {@link ConfigurationBuilder}.
 *
 * @author Sören Henning
 *
 */
public class QuicksortConfigurationFromBuilder extends Configuration {

	public QuicksortConfigurationFromBuilder(final List<QuicksortProblem> inputs, final List<QuicksortSolution> results) {
		// set up stages
		InitialElementProducer<QuicksortProblem> initialElementProducer = new InitialElementProducer<QuicksortProblem>(inputs);
		DivideAndConquerStage<QuicksortProblem, QuicksortSolution> quicksortStage = new DivideAndConquerStage<QuicksortProblem, QuicksortSolution>(2);
		CollectorSink<QuicksortSolution> collectorSink = new CollectorSink<QuicksortSolution>(results);
		quicksortStage.declareActive();

		// connect ports
		this.from(initialElementProducer).to(quicksortStage).end(collectorSink);
	}
}
