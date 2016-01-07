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
import static teetime.framework.test.StageTester.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import teetime.stage.CipherStage.CipherMode;

/**
 * @author Nils Christian Ehmke
 */
public class CipherStageTest {

	@Test
	public void decryptShouldInvertEncryption() {
		final CipherStage encryptStage = new CipherStage("somePassword", CipherMode.ENCRYPT);
		final CipherStage decryptStage = new CipherStage("somePassword", CipherMode.DECRYPT);

		final byte[] inputBytes = new byte[] { 1, 2, 3, 4, 5 };
		final List<byte[]> encryptedBytes = new ArrayList<byte[]>();
		final List<byte[]> decryptedBytes = new ArrayList<byte[]>();

		test(encryptStage).and().send(inputBytes).to(encryptStage.getInputPort()).and().receive(encryptedBytes).from(encryptStage.getOutputPort()).start();
		test(decryptStage).and().send(encryptedBytes).to(decryptStage.getInputPort()).and().receive(decryptedBytes).from(decryptStage.getOutputPort()).start();

		assertThat(decryptedBytes, contains(inputBytes));
	}

}
