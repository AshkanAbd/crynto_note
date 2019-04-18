package ir.ashkanabd.cryptonote;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.content.Context;
import android.util.Base64;

import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

public class Encryption {
    private Context context;

    public Encryption(Context context) {
        this.context = context;
    }

    private String readKey() throws Exception {
        ObjectInputStream ois = new ObjectInputStream(context.getAssets().open("k"));
        Map<Integer, Character> map = (HashMap<Integer, Character>) ois.readObject();
        byte[] buf = new byte[map.size()];
        for (Map.Entry<Integer, Character> e : map.entrySet()) {
            buf[e.getKey()] = (byte) ((char) e.getValue());
        }
        return new String(buf);
    }

    private String readInitVector() throws Exception {
        ObjectInputStream ois = new ObjectInputStream(context.getAssets().open("iv"));
        Map<Integer, Character> map = (HashMap<Integer, Character>) ois.readObject();
        byte[] buf = new byte[map.size()];
        for (Map.Entry<Integer, Character> e : map.entrySet()) {
            buf[e.getKey()] = (byte) ((char) e.getValue());
        }
        return new String(buf);
    }

    public String encrypt(String value, final String key) {
        try {
            IvParameterSpec iv = new IvParameterSpec(readInitVector().getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());

            return Base64.encodeToString(encrypted, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String decrypt(String encrypted, final String key) {
        try {
            IvParameterSpec iv = new IvParameterSpec(readInitVector().getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.decode(encrypted, Base64.DEFAULT));

            return new String(original);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public String defaultEncrypt(String value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(readInitVector().getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(readKey().getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());

            return Base64.encodeToString(encrypted, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String defaultDecrypt(String encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(readInitVector().getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(readKey().getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.decode(encrypted, Base64.DEFAULT));

            return new String(original);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}