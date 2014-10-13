package teetime.stage;

import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import teetime.framework.ConsumerStage;
import teetime.framework.OutputPort;

public class CipherByteArray extends ConsumerStage<byte[]> {

	private final OutputPort<byte[]> outputPort = this.createOutputPort();
	private Cipher cipher = null;
	private final byte[] salt = { 't', 'e', 's', 't' };
	private SecretKeySpec skeyspec = null;

	public enum CipherMode {
		ENCRYPT, DECRYPT
	}

	public CipherByteArray(final String password, final CipherMode mode) {

		KeySpec keySpec = new PBEKeySpec(password.toCharArray(),
				salt,
				1024, 128);

		SecretKey secretKey = null;

		try {
			secretKey = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1").
					generateSecret(keySpec);
		} catch (Exception e) {
			e.printStackTrace();
		}

		skeyspec = new SecretKeySpec(secretKey.getEncoded(), "AES");

		try {
			cipher = Cipher.getInstance(skeyspec.getAlgorithm());
			if (mode == CipherMode.ENCRYPT) {
				cipher.init(Cipher.ENCRYPT_MODE, skeyspec);
			} else {
				cipher.init(Cipher.DECRYPT_MODE, skeyspec);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void execute(final byte[] element) {

		byte[] output = null;

		try {
			output = cipher.doFinal(element);
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.send(outputPort, output);
	}

	public OutputPort<? extends byte[]> getOutputPort() {
		return outputPort;
	}

}
