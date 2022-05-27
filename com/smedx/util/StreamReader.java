package com.smedx.util;

import java.io.InputStream;

public class StreamReader {
    public static byte[] read(InputStream stream) {
        try {
            byte[] data = new byte[stream.available()];
            stream.read(data);
            stream.close();
            return data;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
