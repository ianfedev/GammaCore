package net.seocraft.commons.core.utils;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileManagerUtils {

    public static void compressFile(File inputDirectory, File outputZip) throws IOException {
        outputZip.getParentFile().mkdirs();
        List<File> listFiles = new ArrayList<>();
        listFiles(listFiles, inputDirectory);

        ZipOutputStream zipOutputStream = new ZipOutputStream(
                new FileOutputStream(outputZip));

        createZipFile(listFiles, inputDirectory, zipOutputStream);
    }

    private static void createZipFile(List<File> listFiles, File inputDirectory, ZipOutputStream zipOutputStream) throws IOException {

        for (File file : listFiles) {
            if (!file.getName().equalsIgnoreCase("map.zip")) {
                String filePath = file.getCanonicalPath();
                int lengthDirectoryPath = inputDirectory.getCanonicalPath().length();
                int lengthFilePath = file.getCanonicalPath().length();
                String zipFilePath = filePath.substring(lengthDirectoryPath + 1, lengthFilePath);
                ZipEntry zipEntry = new ZipEntry(zipFilePath);
                zipOutputStream.putNextEntry(zipEntry);
                FileInputStream inputStream = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int length;
                while ((length = inputStream.read(bytes)) >= 0) {
                    zipOutputStream.write(bytes, 0, length);
                }

                zipOutputStream.closeEntry();
            } else {
                file.delete();
            }
        }
        zipOutputStream.close();
    }

    private static List<File> listFiles(List<File> listFiles, File inputDirectory) throws IOException {
        File[] allFiles = inputDirectory.listFiles();
        for (File file : Objects.requireNonNull(allFiles)) {
            if (file.isDirectory()) {
                listFiles(listFiles, file);
            } else {
                listFiles.add(file);
            }
        }
        return listFiles;
    }

    public static String fileToBase64StringConversion(File file) throws IOException {
        byte[] encodeFile = Base64.getEncoder().encode(
                Files.readAllBytes(file.toPath())
        );
        return new String(encodeFile);
    }

}