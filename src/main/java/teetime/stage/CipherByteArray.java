package teetime.stage;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import teetime.framework.ConsumerStage;
import teetime.framework.OutputPort;

public class CipherByteArray extends ConsumerStage<byte[]> {

	private final OutputPort<byte[]> outputPort = this.createOutputPort();
	private Cipher cipher = null;

	public enum CipherMode {
		ENCRYPT, DECRYPT
	}

	public CipherByteArray(final String password, final CipherMode mode) {
		final byte[] salt = { 't', 'e', 's', 't' };
		SecretKeySpec skeyspec = null;

		KeySpec keySpec = new PBEKeySpec(password.toCharArray(),
				salt,
				1024, 128);

		SecretKey secretKey = null;

		try {
			secretKey = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1").
					generateSecret(keySpec);
		} catch (InvalidKeySpecException e1) {
			throw new IllegalStateException(e1);
		} catch (NoSuchAlgorithmException e1) {
			throw new IllegalStateException(e1);
		}

		skeyspec = new SecretKeySpec(secretKey.getEncoded(), "AES");

		try {
			this.cipher = Cipher.getInstance(skeyspec.getAlgorithm());
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		} catch (NoSuchPaddingException e) {
			throw new IllegalStateException(e);
		}

		try {
			if (mode == CipherMode.ENCRYPT) {
				this.cipher.init(Cipher.ENCRYPT_MODE, skeyspec);
			} else {
				this.cipher.init(Cipher.DECRYPT_MODE, skeyspec);
			}
		} catch (InvalidKeyException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	protected void execute(final byte[] element) {

		byte[] output = null;

		try {
			output = this.cipher.doFinal(element);
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.send(this.outputPort, output);
	}

	public OutputPort<? extends byte[]> getOutputPort() {
		return this.outputPort;
	}

}
