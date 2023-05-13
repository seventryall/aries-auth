package com.star.aries.auth.common.util;


public class ByteUtil {

    /**
     * 字节转换为浮点
     *
     * @param b 字节（至少4个字节）
     * @return
     */
    public static float byteToFloat(byte[] b) {
        int num = 0;
        int j = 0;
        for (int i = 0; i < 4; i++) {
            num |= (b[i] & 0xff) << j * 8;
            j++;
        }
        return Float.intBitsToFloat(num);
    }

    /**
     * 字节转换为浮点
     *
     * @param b     字节（至少4个字节）
     * @param index 开始位置
     * @return
     */
    public static float byteToFloat(byte[] b, int index) {
        int num = 0;
        int j = 0;
        for (int i = index; i < 4 + index; i++) {
            num |= (b[i] & 0xff) << j * 8;
            j++;
        }
        return Float.intBitsToFloat(num);
    }

    /**
     * int整数转换为4字节的byte数组
     *
     * @param a 整数
     * @return byte数组
     */
    public static byte[] intToByteArray(int a) {
        return new byte[]{
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }

    /**
     * @param type      类型
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return : byte[]
     */
    public static byte[] getByteByHistory(int type, Long startTime, Long endTime) {
        return new byte[]{
                (byte) type,
                (byte) (startTime & 0xFF),
                (byte) ((startTime >> 8) & 0xFF),
                (byte) ((startTime >> 16) & 0xFF),
                (byte) ((startTime >> 24) & 0xFF),
                (byte) (endTime & 0xFF),
                (byte) ((endTime >> 8) & 0xFF),
                (byte) ((endTime >> 16) & 0xFF),
                (byte) ((endTime >> 24) & 0xFF),
        };
    }

    /**
     * @param type 类型
     * @param time 开始时间
     * @return : byte[]
     */
    public static byte[] getByteByCheckDateTime(int type, Long time) {
        return new byte[]{
                (byte) type,
                (byte) (time & 0xFF),
                (byte) ((time >> 8) & 0xFF),
                (byte) ((time >> 16) & 0xFF),
                (byte) ((time >> 24) & 0xFF)
        };
    }


    /**
     * 字节转换为整形
     *
     * @param b 字节（至少4个字节）
     * @return
     */
    public static int byteToInt(byte[] b, int index, int size) {
        int res = 0;
        switch (size) {
            case 1:
                res = b[index] & 0xFF;
                break;
            case 2:
                res = b[index] & 0xFF |
                        (b[index + 1] & 0xFF) << 8;
                break;
            case 3:
                res = b[index] & 0xFF |
                        (b[index + 1] & 0xFF) << 8 |
                        (b[index + 2] & 0xFF) << 16;
                break;
            case 4:
                res = b[index] & 0xFF |
                        (b[index + 1] & 0xFF) << 8 |
                        (b[index + 2] & 0xFF) << 16 |
                        (b[index + 3] & 0xFF) << 24;
                break;
            default:
        }
        return res;
    }

    public static String byteToHex(byte[] bytes) {
        StringBuilder buf = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            buf.append(String.format("%02x", new Integer(b & 0xff)));
        }
        return buf.toString();
    }

    public static String byteToHex(byte[] bytes, int index, int size) {
        StringBuilder buf = new StringBuilder(size * 2);
        for (int i = index; i < index + size; i++) {
            byte b = bytes[i];
            buf.append(String.format("%02x", new Integer(b & 0xff)));
        }
        return buf.toString();
    }
}
