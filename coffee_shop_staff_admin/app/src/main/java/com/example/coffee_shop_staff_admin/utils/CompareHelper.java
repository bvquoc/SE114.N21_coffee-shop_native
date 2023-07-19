package com.example.coffee_shop_staff_admin.utils;

import java.text.Normalizer;

public class CompareHelper {
    public static boolean compareContainTextUnicode(String s1, String s2)
    {
        String normalizedStr1 = normalize(s1);
        String normalizedStr2 = normalize(s2);

        return normalizedStr1.contains(normalizedStr2);
    }

    private static String normalize(String s)
    {
        // Normalize Unicode characters, remove diacritical marks (accents)
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        // Remove special characters (except alphanumeric and space)
         return temp.replaceAll("[^a-zA-Z\\d\\s]", "");
    }
}
