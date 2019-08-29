package com.cloudbric.cryptobric.customview;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cloudbric.cryptobric.R;

import java.util.List;

import lombok.NonNull;

public class CategoryDropdownAdapter extends RecyclerView.Adapter<CategoryDropdownAdapter.CategoryViewHolder> {

    private List<Category> categories;
    private CategorySelectedListener categorySelectedListener;
    Context mContext;

    public CategoryDropdownAdapter(List<Category> categories) {
        super();
        this.categories = categories;
    }


    public void setCategorySelectedListener(CategoryDropdownAdapter.CategorySelectedListener categorySelectedListener) {
        this.categorySelectedListener = categorySelectedListener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.dropdown_category, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, final int position) {
        final Category category = categories.get(position);
        holder.tvCategory.setText(category.category);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (categorySelectedListener != null) {
                    categorySelectedListener.onCategorySelected(position, category);
                    
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView tvCategory;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvCategory);
        }
    }

    public interface CategorySelectedListener {
        void onCategorySelected(int position, Category category);
    }

}