/*
 * Created on 26.11.2010
 *
 * Copyright(c) 2010 - 2099 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.qcrest.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Provides methods for converting a file to a byte array (Base64 encoded) and vice versa.
 * 
 * @author mibu
 */
public final class FileByteConverter {

    /**
     * Protected constructor.
     */
    private FileByteConverter() {
    }

    /**
     * Copies the complete file content into a Base64 encoded byte array.
     * 
     * @param file The file to copy.
     * 
     * @return A array of bytes containing the files content.
     * 
     * @throws java.io.IOException An I/O error occurred.
     */
    public static byte[] getBytesFromFile(final File file) throws IOException {
        final BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
                file));

        final ByteArrayOutputStream bout = new ByteArrayOutputStream();

        try {
            int counter;

            while ((counter = bis.read()) != -1) {
                bout.write(counter);
            }

        } finally {
            if (bis != null) {
                bis.close();
            }
            if (bout != null) {
                bout.close();
            }
        }

        return Base64.encodeBase64(bout.toByteArray());
    }

    /**
     * Creates a new file with the given name and its content copied from the given Base64 encoded byte array.
     *
     * @param name The created files name.
     *
     * @param data Byte array of files content.
     *
     * @return A new created temporary file.
     *
     * @throws java.io.IOException An I/O error occurred.
     */
    public static File getFileFromBytes(final String name, final byte[] data) throws IOException {
        final File out = new File(System.getProperty("java.io.tmpdir") + File.pathSeparator + "QcTest", name);
        FileUtils.writeByteArrayToFile(out, data);
        return out;
    }
}
