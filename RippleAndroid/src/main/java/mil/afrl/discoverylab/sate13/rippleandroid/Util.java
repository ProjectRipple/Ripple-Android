package mil.afrl.discoverylab.sate13.rippleandroid;

/**
 * Created by James on 6/1/2014.
 */
public class Util {
    public static long convert4BytesToUInt(byte[] bytes) {
        return (long) (bytes[0] & 0xFF) << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
    }

    public static long convert4BytesToUIntLE(byte[] bytes) {
        return (long) (bytes[3] & 0xFF) << 24 | (bytes[2] & 0xFF) << 16 | (bytes[1] & 0xFF) << 8 | (bytes[0] & 0xFF);
    }

    // Temp hack for sequence number to seem correct as a 32 bit value
    public static long convert4BytesToUIntTemp(byte[] bytes) {
        return (long) (bytes[2] & 0xFF) << 24 | (bytes[3] & 0xFF) << 16 | (bytes[0] & 0xFF) << 8 | (bytes[1] & 0xFF);
    }

    public static int convert2BytesToUInt(byte[] bytes){
        return (bytes[0] & 0xFF) << 8 | (bytes[1] & 0xFF);
    }

    public static int convert2BytesToUInt (byte b2, byte b1)
    {
        return (b2 & 0xFF) << 8 | (b1 & 0xFF);
    }

    // from http://stackoverflow.com/questions/140131/convert-a-string-representation-of-a-hex-dump-to-a-byte-array-using-java
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

}
