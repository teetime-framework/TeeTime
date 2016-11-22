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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import teetime.framework.test.StageTester;

public class File2SeqOfWordsTest {

	@Test
	public void testExecute() throws Exception {
		final File2SeqOfWords stage = new File2SeqOfWords(14);
		final List<String> outputSeqOfWords = new ArrayList<String>();
		StageTester.test(stage).send(Arrays.asList(new File("./src/test/resources/data/input.txt"))).to(stage.getInputPort()).and().receive(outputSeqOfWords)
				.from(stage.getOutputPort()).start();
		assertEquals(outputSeqOfWords.get(0), "Lorem ipsum");
	}

}
