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
package teetime.stage;

import java.util.Random;

import teetime.framework.AbstractProducerStage;

public final class RandomIntegerGenerator extends AbstractProducerStage<Integer> {

	private final int i;
	private final Random random = new Random();

	public RandomIntegerGenerator(final int n) {
		i = n;
	}

	@Override
	protected void execute() {
		this.getOutputPort().send(random.nextInt(i));

	}
}
