package org.fightjc.xybot.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Util {

    public static int bytes2Int (byte[] bytes) {
        int int1 = bytes[0] & 0xff;
        int int2 = (bytes[1] & 0xff) << 8;
        int int3 = (bytes[2] & 0xff) << 16;
        int int4 = (bytes[3] & 0xff) << 24;
        return int1 | int2 | int3 | int4;
    }

    public static byte[] getMd5Byte(String str) {
        byte[] md5bt = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md5bt = md.digest(str.getBytes());
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return md5bt;
    }
}
