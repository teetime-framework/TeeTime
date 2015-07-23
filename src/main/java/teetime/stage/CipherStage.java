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
package teetime.stage;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import teetime.framework.AbstractConsumerStage;
import teetime.framework.OutputPort;

public final class CipherStage extends AbstractConsumerStage<byte[]> {

	private final OutputPort<byte[]> outputPort = this.createOutputPort();
	private Cipher cipher;

	public enum CipherMode {
		ENCRYPT, DECRYPT
	}

	public CipherStage(final String password, final CipherMode mode) {
		this(password, mode, new byte[] { 't', 'e', 's', 't' });
	}

	public CipherStage(final String password, final CipherMode mode, final byte[] salt) {
		final KeySpec keySpec = new PBEKeySpec(password.toCharArray(),
				salt,
				1024, 128);

		SecretKey secretKey = null;

		try {
			secretKey = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1").
					generateSecret(keySpec);
		} catch (final InvalidKeySpecException e1) {
			throw new IllegalStateException(e1);
		} catch (final NoSuchAlgorithmException e1) {
			throw new IllegalStateException(e1);
		}

		final SecretKeySpec skeyspec = new SecretKeySpec(secretKey.getEncoded(), "AES");

		try {
			this.cipher = Cipher.getInstance(skeyspec.getAlgorithm());
		} catch (final NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		} catch (final NoSuchPaddingException e) {
			throw new IllegalStateException(e);
		}

		final int convertedMode = (mode == CipherMode.ENCRYPT) ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE;
		try {
			this.cipher.init(convertedMode, skeyspec);
		} catch (final InvalidKeyException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	protected void execute(final byte[] element) {
		try {
			byte[] outputBytes = this.cipher.doFinal(element);
			this.outputPort.send(outputBytes);
		} catch (IllegalBlockSizeException e) {
			throw new IllegalStateException(e);
		} catch (BadPaddingException e) {
			throw new IllegalStateException(e);
		}
	}

	public OutputPort<byte[]> getOutputPort() {
		return this.outputPort;
	}

}
