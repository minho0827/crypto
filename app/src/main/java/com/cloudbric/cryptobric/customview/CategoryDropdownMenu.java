package com.cloudbric.cryptobric.customview;

import android.content.Context;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import com.cloudbric.cryptobric.R;

public class CategoryDropdownMenu extends PopupWindow {
    Context context;
    private RecyclerView rvCategory;
    private CategoryDropdownAdapter dropdownAdapter;

    public CategoryDropdownMenu(Context context){
        super(context);
        this.context = context;
        setupView();
    }

    public void setCategorySelectedListener(CategoryDropdownAdapter.CategorySelectedListener categorySelectedListener) {
        dropdownAdapter.setCategorySelectedListener(categorySelectedListener);
    }

    private void setupView() {
        View view = LayoutInflater.from(context).inflate(R.layout.popup_category, null);

        rvCategory = view.findViewById(R.id.rvCategory);
        rvCategory.setHasFixedSize(true);
        rvCategory.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        rvCategory.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));

        dropdownAdapter = new CategoryDropdownAdapter(Category.generateCategoryList(context));
        rvCategory.setAdapter(dropdownAdapter);

        setContentView(view);
    }
}