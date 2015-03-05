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

import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertThat;
import static teetime.framework.test.StageTester.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Nils Christian Ehmke
 */
public class ToLowerCaseTest {

	private ToLowerCase filter;

	@Before
	public void initializeFilter() {
		this.filter = new ToLowerCase();
	}

	@Test
	public void toLowerCaseShouldWork() {
		final List<String> results = new ArrayList<String>();

		test(this.filter).and().send("Hello World").to(this.filter.getInputPort()).and().receive(results).from(this.filter.getOutputPort()).start();

		assertThat(results, contains("hello world"));
	}

	@Test
	public void toLowerCaseShouldNotRemoveNonWordCharacters() {
		final List<String> results = new ArrayList<String>();

		test(this.filter).and().send("1 2 3").to(this.filter.getInputPort()).and().receive(results).from(this.filter.getOutputPort()).start();

		assertThat(results, contains("1 2 3"));
	}

}
