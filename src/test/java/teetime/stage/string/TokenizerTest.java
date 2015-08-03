/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://christianwulf.github.io/teetime)
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
public class TokenizerTest {

	private Tokenizer tokenizer;

	@Before
	public void initializeTokenizer() {
		tokenizer = new Tokenizer(";");
	}

	@Test
	public void tokenizerShouldJustDelaySingleToken() {
		final List<String> results = new ArrayList<String>();

		test(tokenizer).and().send("Hello World").to(tokenizer.getInputPort()).and().receive(results).from(tokenizer.getOutputPort()).start();

		assertThat(results, contains("Hello World"));
	}

	@Test
	public void tokenizerShouldSplitMultipleToken() {
		final List<String> results = new ArrayList<String>();

		test(tokenizer).and().send("Hello;World").to(tokenizer.getInputPort()).and().receive(results).from(tokenizer.getOutputPort()).start();

		assertThat(results, contains("Hello", "World"));
	}

	public static void main(final String[] args) {
		TokenizerTest toker = new TokenizerTest();
		for (int i = 0; i < 1000; i++) {
			toker.initializeTokenizer();
			toker.tokenizerShouldSplitMultipleToken();
		}
	}

}
