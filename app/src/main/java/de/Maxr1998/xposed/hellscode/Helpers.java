package de.Maxr1998.xposed.hellscode;

import android.animation.Animator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Helpers {

    private static final char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String shaHash(String toHash) { // from: [ http://stackoverflow.com/a/11978976 ]. Thanks very much!
        String hash = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = toHash.getBytes("UTF-8");
            digest.update(bytes, 0, bytes.length);
            bytes = digest.digest();

            hash = bytesToHex(bytes);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return hash;
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static void animateView(View v) {
        int cx = (v.getLeft() + v.getRight()) / 2;
        int cy = (v.getTop() + v.getBottom()) / 2;
        int finalRadius = v.getHeight();
        Animator reveal = ViewAnimationUtils.createCircularReveal(v, cx, cy, 1, finalRadius);
        v.setVisibility(View.VISIBLE);
        reveal.start();
        Animation hide = new AlphaAnimation(1f, 0f);
        hide.setDuration(500);
        hide.setInterpolator(new FastOutSlowInInterpolator());
        hide.setStartOffset(200);
        v.setAnimation(hide);
        v.animate();
        v.setVisibility(View.INVISIBLE);
    }
}
