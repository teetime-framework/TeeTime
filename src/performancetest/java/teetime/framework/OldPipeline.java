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
package teetime.framework;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Deprecated
public class OldPipeline<FirstStage extends Stage, LastStage extends Stage> extends AbstractCompositeStage {

	protected FirstStage firstStage;
	private final List<LastStage> lastStages = new ArrayList<LastStage>();

	@Override
	public FirstStage getFirstStage() {
		return this.firstStage;
	}

	public void setFirstStage(final FirstStage firstStage) {
		this.firstStage = firstStage;
	}

	public void setLastStage(final LastStage lastStage) {
		this.lastStages.clear();
		this.lastStages.add(lastStage);
	}

	public LastStage getLastStage() {
		return lastStages.get(0);
	}

	@Override
	protected Collection<? extends Stage> getLastStages() {
		return lastStages;
	}

}
