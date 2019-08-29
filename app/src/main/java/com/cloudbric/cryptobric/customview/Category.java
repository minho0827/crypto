package com.cloudbric.cryptobric.customview;

import android.content.Context;

import com.cloudbric.cryptobric.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin2 on 10/Jul/2018.
 */

public class Category {
    public long id;
    public String category;

    public Category(long id, String category) {
        super();
        this.id = id;
        this.category = category;
    }

    public static List<Category> generateCategoryList(Context c) {
        List<Category> categories = new ArrayList<>();
        String[] programming = {c.getResources().getString(R.string.str_all_scan_report),
                c.getResources().getString(R.string.str_crypto_scan_report)};

        for (int i = 0; i < programming.length; i++) {
            categories.add(new Category(i, programming[i]));
        }
        return categories;
    }
}