/*
 * Testerra
 *
 * (C) 2013, Mike Beuthan, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
 *
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
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
