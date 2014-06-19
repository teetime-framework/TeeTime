/***************************************************************************
 * Copyright 2014 Kieker Project (http://kieker-monitoring.net)
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
 ***************************************************************************/
package teetime.variant.explicitScheduling.stage.predicate;

import teetime.variant.explicitScheduling.framework.core.AbstractFilter;
import teetime.variant.explicitScheduling.framework.core.Context;
import teetime.variant.explicitScheduling.framework.core.IInputPort;
import teetime.variant.explicitScheduling.framework.core.IOutputPort;

import com.google.common.base.Predicate;

/**
 * @author Nils Christian Ehmke
 * @param <T>
 * 
 * @since 1.10
 */
public class PredicateFilter<T> extends AbstractFilter<PredicateFilter<T>> {

	public final IInputPort<PredicateFilter<T>, T> inputPort = this.createInputPort();

	public final IOutputPort<PredicateFilter<T>, T> matchingOutputPort = this.createOutputPort();
	public final IOutputPort<PredicateFilter<T>, T> mismatchingOutputPort = this.createOutputPort();

	private Predicate<T> predicate;

	public PredicateFilter(final Predicate<T> predicate) {
		this.setPredicate(predicate);
	}

	public PredicateFilter() {
		super();
	}

	public Predicate<T> getPredicate() {
		return this.predicate;
	}

	public void setPredicate(final Predicate<T> predicate) {
		this.predicate = predicate;
	}

	@Override
	protected boolean execute(final Context<PredicateFilter<T>> context) {
		final T inputObject = context.tryTake(this.inputPort);
		if (inputObject == null) {
			return false;
		}

		if (this.predicate.apply(inputObject)) {
			context.put(this.matchingOutputPort, inputObject);
		} else {
			context.put(this.mismatchingOutputPort, inputObject);
		}

		return true;
	}

}
