package fr.eagleeyestudio.ra;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class Compressor {

	public static byte[] compress(byte[] input, int compressionLevel, boolean GZIPFormat) throws IOException {
		Deflater compressor = new Deflater(compressionLevel, GZIPFormat);

		compressor.setInput(input);

		compressor.finish();

		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		byte[] readBuffer = new byte[input.length];

		while (!compressor.finished()) {
			int readCount = compressor.deflate(readBuffer);
			if (readCount > 0) {
				bao.write(readBuffer, 0, readCount);
			}
		}

		compressor.end();

		return bao.toByteArray();
	}

	public static byte[] decompress(byte[] input, boolean GZIPFormat) throws IOException, DataFormatException {

		Inflater decompressor = new Inflater(GZIPFormat);

		decompressor.setInput(input);

		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		byte[] readBuffer = new byte[input.length];

		while (!decompressor.finished()) {
			int readCount = decompressor.inflate(readBuffer);
			if (readCount > 0) {
				bao.write(readBuffer, 0, readCount);
			}
		}

		decompressor.end();
		return bao.toByteArray();
	}

}
