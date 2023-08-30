package com.technovision.fortress.util;

public class StringUtils {

    public static boolean isAlpha(String s) {
        if (s == null) {
            return false;
        }
        for (char c : s.toCharArray()) {
            if (!Character.isLetter(c)) {
                return false;
            }
        }
        return true;
    }

    public static String toSafeString(String text, int maxChars, String context) throws CKException {
        text = text.replace(" ", "_");
        text = text.replace("\"", "");
        text = text.replace("\'", "");
        if (text.length() > maxChars) {
            throw new CKException(context + " must be under "+maxChars+" characters.");
        }
        if (!StringUtils.isAlpha(text)) {
            throw new CKException(context + " can only contain letters [A-Z].");
        }
        return text;
    }
}
