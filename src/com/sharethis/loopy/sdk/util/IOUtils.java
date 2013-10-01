package com.sharethis.loopy.sdk.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Jason Polites
 */
public class IOUtils {

	/**
	 * Pipes input bytes to output.
	 * @param in The IN stream
	 * @param out The OUT stream
	 * @param bufferSize The buffer size
	 * @return The number of bytes written.
	 * @throws IOException
	 */
	public static long pipe(InputStream in, OutputStream out, int bufferSize) throws IOException {
		int read;
		long total = 0L;
		byte[] buffer = new byte[bufferSize];
		while((read = in.read(buffer)) >= 0) {
			total+=read;
			out.write(buffer, 0, read);
		}
		out.flush();
		return total;
	}

	public static String read(InputStream in, int bufferSize) throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		pipe(in, bout, bufferSize);
		return new String(bout.toByteArray(), "utf-8");
	}

}
