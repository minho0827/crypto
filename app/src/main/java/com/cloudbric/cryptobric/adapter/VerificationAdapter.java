package com.cloudbric.cryptobric.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.cloudbric.cryptobric.R;
import com.cloudbric.cryptobric.data.CryptoData;

import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class VerificationAdapter extends RecyclerView.Adapter<VerificationAdapter.ViewHolder> {

    Context mContext;
    List<CryptoData.ResultInfo> mCryptoDataList = new ArrayList<>();
    private final static String TAG = "VerificationAdapter";

    public VerificationAdapter(Context context, List<CryptoData.ResultInfo> cryptoDataList) {

        mContext = context;
        mCryptoDataList = cryptoDataList;

    }

    public void setData(List<CryptoData.ResultInfo> exchangeList) {
        this.mCryptoDataList = exchangeList;
        Log.i(TAG, "cryptoDataList size : " + mCryptoDataList.size());
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_crypto_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {


        final CryptoData.ResultInfo cryptoData = mCryptoDataList.get(position);
        if (cryptoData != null) {
            Glide.with(mContext)
                    .load(cryptoData.getIconImageUrl())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.placeholder)
                            .circleCrop().transform(new RoundedCorners(50)))
                    .transition(withCrossFade())
                    .into(viewHolder.ivIcon);

            viewHolder.tvName.setText(cryptoData.getAppName());
            viewHolder.tvCompany.setText(cryptoData.getCompany());
        }
    }


    @Override
    public int getItemCount() {
        return mCryptoDataList.size();
    }

    public CryptoData.ResultInfo getItemAt(int position) {
        return mCryptoDataList.get(position);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        AppCompatImageView ivIcon;
        AppCompatTextView tvName;
        AppCompatTextView tvCompany;

        public ViewHolder(View view) {
            super(view);
            ivIcon = view.findViewById(R.id.iv_icon);
            tvName = view.findViewById(R.id.tv_name);
            tvCompany = view.findViewById(R.id.tv_company);

        }

    }
}
