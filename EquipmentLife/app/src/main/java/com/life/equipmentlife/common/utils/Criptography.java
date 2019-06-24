package com.life.equipmentlife.common.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Criptography {

    public byte[] criptographInformation(String info){

        MessageDigest md;

        byte[] criptografado = null;

        try {

            md = MessageDigest.getInstance("SHA-1");

            criptografado = md.digest(info.getBytes());

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return criptografado;

    }

}
