/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   24.12.15
 */
package com.bookmate.libs.epub;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public class IOUtils {
    private static final int EOF = -1;

    public static String getPath(String fileName) {
        return fileName == null ? "" :
                (fileName.contains("/") ? fileName.substring(0, fileName.lastIndexOf("/")) : fileName);
    }

    public static String concatPath(String path, String fileName) {
        return path == null ? fileName :
                (path.endsWith("/") ? path + fileName : path + "/" + fileName);
    }

    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }

    // http://stackoverflow.com/a/1264737/190148
    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[16384];

        while ((nRead = input.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();

        return buffer.toByteArray();
    }

    public static String toString(InputStream is, String encoding) throws IOException {
        java.util.Scanner s = new java.util.Scanner(is, encoding);
        return s.useDelimiter("\\A").hasNext() ? s.next() : "";
    }
}
