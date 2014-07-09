package com.discoverylab.ripple.android.util;

/**
 * Class to hold static utility functions
 * Created by James on 6/1/2014.
 */
public final class Util {

    private Util(){}

    /**
     * Converts the first 4 bytes of the array to a 4 byte unsigned int(returned as long)
     * @param bytes 4 bytes of unsigned int in Big Endian order
     * @return 4 byte unsigned int as long
     */
    public static long convert4BytesToUIntBE(byte[] bytes) {
        return (long) (bytes[0] & 0xFF) << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
    }

    /**
     * Converts the first 4 bytes of the array to a 4 byte unsigned int(returned as long)
     * @param bytes 4 bytes of unsigned int in Little Endian order
     * @return 4 byte unsigned int as long
     */
    public static long convert4BytesToUIntLE(byte[] bytes) {
        return (long) (bytes[3] & 0xFF) << 24 | (bytes[2] & 0xFF) << 16 | (bytes[1] & 0xFF) << 8 | (bytes[0] & 0xFF);
    }

    /**
     * Converts the first 2 bytes of the array to a 2 byte unsigned int(returned as int)
     * @param bytes 2 bytes of unsigned int in Big Endian order
     * @return 2 byte unsigned int as int
     */
    public static int convert2BytesToUIntBE(byte[] bytes) {
        return (bytes[0] & 0xFF) << 8 | (bytes[1] & 0xFF);
    }


    /**
     * Convert a String of hexidecimal digits to a byte array
     * @param s String of hexidecimal digits
     * @return byte array of values represented by hexidecimal string
     */
    public static byte[] hexStringToByteArray(String s) {
        // from http://stackoverflow.com/questions/140131/convert-a-string-representation-of-a-hex-dump-to-a-byte-array-using-java
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

}
