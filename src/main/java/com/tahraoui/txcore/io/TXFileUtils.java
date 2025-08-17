package com.tahraoui.txcore.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class TXFileUtils {

	/**
	 * Checks if the given file has the specified extension.
	 *
	 * @param file      The file to check.
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
