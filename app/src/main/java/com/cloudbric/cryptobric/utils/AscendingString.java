package com.cloudbric.cryptobric.utils;

import java.util.Comparator;

public class AscendingString implements Comparator<String> {

    @Override
    public int compare(String a, String b) {

        return b.compareTo(a);
    }

    @Override
    public Comparator<String> reversed() {
        return null;
    }
}