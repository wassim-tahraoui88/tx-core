package com.tahraoui.txcore.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class TXFileUtils {

	private static final String
			EXTENSION_JPEG = "jpeg", EXTENSION_JPG = "jpg", EXTENSION_PNG = "png", EXTENSION_GIF = "gif", EXTENSION_BMP = "bmp",
			EXTENSION_MP3 = "mp3", EXTENSION_WAV = "wav", EXTENSION_OGG = "ogg",
			EXTENSION_MP4 = "mp4", EXTENSION_AVI = "avi", EXTENSION_MKV = "mkv",
			EXTENSION_PDF = "pdf", EXTENSION_DOCX = "docx", EXTENSION_TXT = "txt",
			EXTENSION_ZIP = "zip", EXTENSION_RAR = "rar", EXTENSION_TAR = "tar";

	public static String concatPaths(String... paths) {
		var separator = "/";
		var sb = new StringBuilder();
		for (var path : paths) {
			if (!sb.isEmpty() && !sb.toString().endsWith(separator)) sb.append(separator);
			sb.append(path.startsWith(separator) ? path.substring(1) : path);
		}
		return sb.toString();
	}

	/**
	 * Determines the FileType based on the file extension.
	 * @param filename The name of the file.
	 * @return The corresponding FileType.
	 */
	public static FileType getFileType(String filename) {
		var extension = getFileExtension(filename);
		return switch (extension) {
			case EXTENSION_JPEG, EXTENSION_JPG, EXTENSION_PNG, EXTENSION_GIF, EXTENSION_BMP -> FileType.IMAGE;
			case EXTENSION_MP3, EXTENSION_WAV, EXTENSION_OGG -> FileType.AUDIO;
			case EXTENSION_MP4, EXTENSION_AVI, EXTENSION_MKV -> FileType.VIDEO;
			case EXTENSION_PDF, EXTENSION_DOCX, EXTENSION_TXT -> FileType.DOC;
			case EXTENSION_ZIP, EXTENSION_RAR, EXTENSION_TAR -> FileType.ARCHIVE;
			default -> FileType.OTHER;
		};
	}
	/**
	 * Extracts the file extension from the given filename.
	 *
	 * @param filename The filename to extract the extension from.
	 * @return The file extension in lowercase, or an empty string if none exists.
	 */
	public static String getFileExtension(String filename) {
		int lastIndex = filename.lastIndexOf('.');
		if (lastIndex == -1 || lastIndex == filename.length() - 1) return "";
		return filename.substring(lastIndex + 1).toLowerCase();
	}

	/**
	 * Checks if the given file has the specified extension.
	 * @param file The file to check.
	 * @param extension The extension to verify (without the dot).
	 * @return true if the file ends with the specified extension, false otherwise.
	 */
	public static boolean verifyExtension(File file, String extension) { return file.getName().endsWith('.' + extension); }

	/**
	 * Exports the given TXSerializable data to a file.
	 *
	 * @param file The file to export the data to.
	 * @param data The TXSerializable data to export.
	 */
	public static void exportFile(File file, TXSerializable data) {
		try (var stream = new ObjectOutputStream(new FileOutputStream(file))) {
			stream.writeObject(data);
		}
		catch (IOException _) {}
	}

	/**
	 * Imports TXSerializable data from a file.
	 *
	 * @param file The file to import the data from.
	 * @return The imported TXSerializable data, or null if the import failed.
	 */
	public static TXSerializable importFile(File file) {
		try (var stream = new ObjectInputStream(new FileInputStream(file))) {
			var object = stream.readObject();
			if (object instanceof TXSerializable data) return data;
			return null;
		}
		catch (IOException | ClassNotFoundException e) {
			return null;
		}
	}

}