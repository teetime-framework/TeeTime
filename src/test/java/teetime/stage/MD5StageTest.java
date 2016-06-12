/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime-framework.github.io)
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
package teetime.stage;

import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import teetime.framework.test.StageTester;

public class MD5StageTest {

	@Test
	public void test() {
		MD5Stage md5Stage = new MD5Stage();

		final String[] inputValues = new String[] { "test", "a", "123", "b", "foo" };
		final String[] hashes = new String[] { "098f6bcd4621d373cade4e832627b4f6",
			"0cc175b9c0f1b6a831c399e269772661",
			"202cb962ac59075b964b07152d234b70",
			"92eb5ffee6ae2fec3ad71c777531578f",
			"acbd18db4cc2f85cedef654fccc4a4d8" };
		final List<String> results = new ArrayList<String>();

		StageTester.test(md5Stage).and()
				.send(inputValues).to(md5Stage.getInputPort()).and()
				.receive(results).from(md5Stage.getOutputPort()).start();
		assertThat(results, contains(hashes));
	}
}
