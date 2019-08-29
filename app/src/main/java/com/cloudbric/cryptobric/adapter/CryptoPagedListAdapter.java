package com.cloudbric.cryptobric.adapter;

import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cloudbric.cryptobric.R;
import com.cloudbric.cryptobric.data.CryptoData;

public class CryptoPagedListAdapter extends PagedListAdapter<CryptoData, CryptoPagedListAdapter.CryptoViewHolder> {
    private final static String TAG = "CryptoPagedListAdapter";
    private Context mContext;
    private CryptoPagedListListener mListListener;
    private int count = 0;
    private boolean isChecked;

    public CryptoPagedListAdapter(Context c, CryptoPagedListListener listListener) {
        super(DIFF_CALLBACK);
        mContext = c;
        mListListener = listListener;
    }


    public interface CryptoPagedListListener {
        void visibleEditMode(boolean editMode);
        void setItemCount(int count);
    }


    @NonNull
    @Override
    public CryptoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recyclerview_crypto, viewGroup, false);
        return new CryptoViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull CryptoViewHolder viewHolder, int position) {
        final CryptoData cryptoData = getItem(position);

        if (cryptoData != null) {
//            Glide.with(mContext)
//                    .load(cryptoData.getExchangeData())
//                    .apply(new RequestOptions()
//                            .placeholder(R.drawable.placeholder)
//                            .bitmapTransform(new CropCircleTransformation(mContext))
//                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
//                            .skipMemoryCache(true)
//                            .fitCenter())
//                    .transition(withCrossFade())
//                    .into(viewHolder.ivIcon);
        }else {
            Log.d(TAG,"cryptoData is null");
        }
    }
    public class CryptoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CryptoPagedListAdapter mListAdapter;
        ImageView ivIcon;
        TextView tvPacakageName;

        public CryptoViewHolder(@NonNull View itemView, CryptoPagedListAdapter cryptoPagedListAdapter) {
            super(itemView);

            mListAdapter = cryptoPagedListAdapter;
            ivIcon = itemView.findViewById(R.id.iv_icon);
            tvPacakageName = itemView.findViewById(R.id.tv_pacakage_name);
        }

        @Override
        public void onClick(View v) {

        }
    }

    private static DiffUtil.ItemCallback<CryptoData> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<CryptoData>() {
                @Override
                public boolean areItemsTheSame(@NonNull CryptoData oldItem, @NonNull CryptoData newItem) {
                    return false;
                }

                @Override
                public boolean areContentsTheSame(@NonNull CryptoData oldItem, @NonNull CryptoData newItem) {
                    return false;
                }
            };
}
