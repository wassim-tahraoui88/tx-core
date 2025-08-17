package com.tahraoui.txcore.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class TXFileUtils {

	public static boolean verifyExtension(File file, String extension) { return file.getName().endsWith('.' + extension); }

	public static void exportFile(File file, TXSerializable data) {
		try (var stream = new ObjectOutputStream(new FileOutputStream(file))) {
			stream.writeObject(data);
		}
		catch (IOException _) {}

	}
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
