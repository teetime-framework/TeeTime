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
package teetime.examples.loopStage;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

import teetime.framework.Analysis;

public class FiniteSignalPassingTest {

	@Test(timeout = 5000)
	// may not run infinitely, so we set an arbitrary timeout that is high enough
	public void testStartSignalDoesNotEndUpInInfiniteLoop() throws Exception {
		boolean exceptionsOccured = false;
		LoopStageAnalysisConfiguration configuration = new LoopStageAnalysisConfiguration();
		Analysis analysis = new Analysis(configuration);
		analysis.init();
		try {
			analysis.start();
		} catch (RuntimeException e) {
			exceptionsOccured = true;
		}
		assertFalse(exceptionsOccured);
	}
}
