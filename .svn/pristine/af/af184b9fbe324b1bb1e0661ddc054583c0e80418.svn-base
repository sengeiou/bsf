package com.weking.core.newebpay;// JAVA �[�ѱK�d��
//
// DATE 2017/10/12 First Edition
// Author: TigerZZ from SPGATEWAY / PAY2GO

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;

public class AESJAVA {
	private static final String mEngine = "AES";
	private static final String mCrypto5 = "AES/CBC/PKCS5Padding";
	private static final String mCryptoN = "AES/CBC/NoPadding";
	//AES_256_cbc pkcs7
	private static final String ALGORITHM = "AES/CBC/PKCS7Padding";

	private String mKey;
	private String mIv;

	public AESJAVA(String key, String iv) {
		this.mKey = key;
		this.mIv = iv;
	}

	public byte[] encrypt(String content) {
		try {
			SecretKeySpec sks = new SecretKeySpec(mKey.getBytes(Charset.forName("UTF-8")), mEngine);
			IvParameterSpec ivs = new IvParameterSpec(mIv.getBytes(Charset.forName("UTF-8")));
			Cipher cipher = Cipher.getInstance(mCrypto5);

			cipher.init(Cipher.ENCRYPT_MODE, sks, ivs);
			byte[] byteContent = content.getBytes("UTF-8");
			byte[] cryptograph = cipher.doFinal(byteContent);
			return (cryptograph);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String decrypt(byte[] cryptograph) {
		try {
			SecretKeySpec sks = new SecretKeySpec(mKey.getBytes(Charset.forName("UTF-8")), mEngine);
			IvParameterSpec ivs = new IvParameterSpec(mIv.getBytes(Charset.forName("UTF-8")));
			Cipher cipher = Cipher.getInstance(mCryptoN);

			cipher.init(Cipher.DECRYPT_MODE, sks, ivs);
			byte[] content = removePadding(cipher.doFinal(cryptograph));
			return new String(content);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String parseByte2HexStr(byte buf[]) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buf.length; i++) {
			String hex = Integer.toHexString(buf[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex.toUpperCase());
		}
		return sb.toString();
	}

	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
			                      + Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	public byte[] removePadding(byte[] paddedInput) {
		int numPadBytes = paddedInput[paddedInput.length - 1];
		int originalSize = paddedInput.length - numPadBytes;
		byte[] original = new byte[originalSize];
		System.arraycopy(paddedInput, 0, original, 0, originalSize);
		return original;
	}



	//加密
	public static String AES_cbc_encrypt(byte[] srcData,byte[] key,byte[] iv) {
		try {
		SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
		Cipher cipher = Cipher.getInstance(mCrypto5);
		cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv));
		byte[] encData = cipher.doFinal(srcData);
		return parseByte2HexStr(encData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		String sKEY = "abcdefghijklmnopqrstuvwxyz012345";
		String sIV = "abcdefghijklmnop";
		String content = "Java - AES Encryption & Decryption";

		AESJAVA crypto = new AESJAVA(sKEY, sIV);

		System.out.println("��l�r��      :" + content);

		byte[] encryptResult = crypto.encrypt(content);
		System.out.println("�[�K�s�X�r��  :" + crypto.parseByte2HexStr(encryptResult));

		String decryptResult;
		decryptResult = crypto.decrypt(encryptResult);
		System.out.println("�ѱK ��l�[�K :" + decryptResult);
	}
}