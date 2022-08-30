package com.vslc.tools;

import java.io.UnsupportedEncodingException;

public class BitConverter {

    /**
     * Convert char to byte[]
     *
     * @param x char
     * @return bytes
     */
    public static byte[] toBytes(char x) {
        return toBytes(x, new byte[2], 0);
    }

    /**
     * Convert char to byte[]
     *
     * @param x       char
     * @param bytes   Dest bytes
     * @param bytePos Dest pos
     * @return bytes
     */

    public static byte[] toBytes(char x, byte[] bytes, int bytePos) {
        bytes[bytePos++] = (byte) (x);
        bytes[bytePos] = (byte) (x >> 8);
        return bytes;
    }

    /**
     * 转化到ascii
     * @param bytes
     * @param offset
     * @param dateLen
     * @return
     */
    private static String toAscii(byte[] bytes, int offset, int dateLen) {
        if ((bytes == null) || (bytes.length == 0) || (offset < 0) || (dateLen <= 0)) {
            return null;
        }
        if ((offset >= bytes.length) || (bytes.length - offset < dateLen)) {
            return null;
        }
        String asciiStr = null;
        byte[] data = new byte[dateLen];
        System.arraycopy(bytes, offset, data, 0, dateLen);
        try {
            asciiStr = new String(data, "ISO8859-1");
        } catch (UnsupportedEncodingException e) {
        }
        return asciiStr;
    }

    public static String bytesToAscii(byte[] bytes) {
        return toAscii(bytes, 0, bytes.length);
    }

    /**
     * Convert short to byte[]
     *
     * @param x Short
     * @return bytes
     */
    public static byte[] toBytes(short x) {
        return toBytes(x, new byte[2], 0);
    }

    /**
     * Convert short to byte[]
     *
     * @param x       Short
     * @param bytes   Dest bytes
     * @param bytePos Dest pos
     * @return bytes
     */
    public static byte[] toBytes(short x, byte[] bytes, int bytePos) {
        bytes[bytePos++] = (byte) (x);
        bytes[bytePos] = (byte) (x >> 8);
        return bytes;
    }

    /**
     * Convert int to byte[]
     *
     * @param x int
     * @return bytes
     */
    public static byte[] toBytes(int x) {
        return toBytes(x, new byte[4], 0);
    }

    /**
     * Convert int to byte[]
     *
     * @param x       int
     * @param bytes   Dest bytes
     * @param bytePos Dest pos
     * @return bytes
     */
    public static byte[] toBytes(int x, byte[] bytes, int bytePos) {
        bytes[bytePos++] = (byte) (x);
        bytes[bytePos++] = (byte) (x >> 8);
        bytes[bytePos++] = (byte) (x >> 16);
        bytes[bytePos] = (byte) (x >> 24);
        return bytes;
    }

    /**
     * Convert long to byte[]
     *
     * @param x long
     * @return bytes
     */
    public static byte[] toBytes(long x) {
        return toBytes(x, new byte[8], 0);
    }

    /**
     * Convert long to byte[]
     *
     * @param x       long
     * @param bytes   Dest bytes
     * @param bytePos Dest pos
     * @return bytes
     */
    public static byte[] toBytes(long x, byte[] bytes, int bytePos) {
        bytes[bytePos++] = (byte) (x);
        bytes[bytePos++] = (byte) (x >> 8);
        bytes[bytePos++] = (byte) (x >> 16);
        bytes[bytePos++] = (byte) (x >> 24);
        bytes[bytePos++] = (byte) (x >> 32);
        bytes[bytePos++] = (byte) (x >> 40);
        bytes[bytePos++] = (byte) (x >> 48);
        bytes[bytePos] = (byte) (x >> 56);
        return bytes;
    }

    public static byte[] toBytes(double d) {
        long value = Double.doubleToRawLongBits(d);
        byte[] byteRet = new byte[8];
        for (int i = 0; i < 8; i++) {
            byteRet[i] = (byte) ((value >> 8 * i) & 0xff);
        }
        return byteRet;
    }

        /**
         * Convert byte[] to char
         *
         * @param bytes bytes
         * @return char
         */
    public static char toChar(byte[] bytes) {
        return toChar(bytes, 0);
    }

    /**
     * Convert byte[] to char
     *
     * @param bytes bytes
     * @param index byte start index
     * @return char
     */
    public static char toChar(byte[] bytes, int index) {
        return (char) ((bytes[index + 1] << 8) | (bytes[index] & 0xff));
    }

    /**
     * Convert byte[] to short
     *
     * @param bytes bytes
     * @return short
     */
    public static short toShort(byte[] bytes) {
        return toShort(bytes, 0);
    }

    /**
     * Convert byte[] to short
     *
     * @param bytes bytes
     * @param index byte start index
     * @return short
     */
    public static short toShort(byte[] bytes, int index) {
        return (short) ((bytes[index + 1] << 8) | (bytes[index] & 0xff));
    }

    /**
     * Convert byte[] to int
     *
     * @param bytes bytes
     * @return int
     */
    public static int toInt(byte[] bytes) {
        return toInt(bytes, 0);
    }

    /**
     * Convert byte[] to int
     *
     * @param bytes bytes
     * @param index bytes start index
     * @return int
     */
    public static int toInt(byte[] bytes, int index) {
        return (((bytes[index + 3]) << 24) |
                ((bytes[index + 2] & 0xff) << 16) |
                ((bytes[index + 1] & 0xff) << 8) |
                ((bytes[index] & 0xff)));
    }

    public static int toUShort(byte[] bytes) {
        return toUShort(bytes,0);
    }

    public static int toUShort(byte[] bytes, int index) {
        return (bytes[index+1] & 0xFF) << 8 | (bytes[index] & 0xFF);
    }

    public static long toUInt(byte[] bytes) {
        return toUInt(bytes,0);
    }

    public static long toUInt(byte[] bytes, int index) {
        return (long) (bytes[index+3] & 0xFF) << 24 | (bytes[index+2] & 0xFF) << 16 | (bytes[index+1] & 0xFF) << 8 | (bytes[index] & 0xFF);
    }

    /**
     * Convert byte[] to long
     *
     * @param bytes bytes
     * @return long
     */
    public static long toLong(byte[] bytes) {
        return toLong(bytes, 0);
    }

    /**
     * Convert byte[] to long
     *
     * @param bytes bytes
     * @param index bytes start index
     * @return long
     */
    public static long toLong(byte[] bytes, int index) {
        return ((((long) bytes[index + 7]) << 56) |
                (((long) bytes[index + 6] & 0xff) << 48) |
                (((long) bytes[index + 5] & 0xff) << 40) |
                (((long) bytes[index + 4] & 0xff) << 32) |
                (((long) bytes[index + 3] & 0xff) << 24) |
                (((long) bytes[index + 2] & 0xff) << 16) |
                (((long) bytes[index + 1] & 0xff) << 8) |
                (((long) bytes[index] & 0xff)));
    }

    public static double toDouble(byte[] arr) {
        long value = 0;
        for (int i = 0; i < 8; i++) {
            value |= ((long) (arr[i] & 0xff)) << (8 * i);
        }
        return Double.longBitsToDouble(value);
    }

    public static float toFloat(byte[] arr) {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            value |= ((long) (arr[i] & 0xff)) << (8 * i);
        }
        return Float.intBitsToFloat(value);
    }
}
