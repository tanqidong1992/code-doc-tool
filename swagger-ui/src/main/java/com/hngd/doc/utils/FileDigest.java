package com.hngd.doc.utils;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

public class FileDigest {

	private static final Logger logger = LoggerFactory.getLogger(FileDigest.class);

	public static String sha256Hex(MultipartFile file) throws NoSuchAlgorithmException {
		byte[] bytes = sha256(file);
		return byteToHex(bytes);
	}

	public static byte[] sha256(MultipartFile file) throws NoSuchAlgorithmException {
		byte[] buffer = new byte[4096];
		int temp = 0;
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		try (InputStream in = file.getInputStream()) {
			while ((temp = in.read(buffer, 0, 4096)) > 0) {
				digest.update(buffer, 0, temp);
			}
			byte[] sha256 = digest.digest();
			return sha256;
		} catch (IOException e) {
			logger.error("", e);
		}
		return null;
	}

	public static String sha256Hex(byte[] data) throws NoSuchAlgorithmException {
		byte[] bytes = sha256(data);
		return byteToHex(bytes);
	}

	public static byte[] sha256(byte[] data) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		digest.update(data, 0, data.length);
		byte[] sha256 = digest.digest();
		return sha256;

	}

	public static String byteToHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			sb.append(Integer.toHexString(bytes[i] & 0xff));
		}
		return sb.toString();
	}
}
