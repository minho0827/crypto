package com.cloudbric.cryptobric.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cloudbric.cryptobric.R;
import com.cloudbric.cryptobric.data.VirusScanData;

import java.util.ArrayList;
import java.util.List;

public class CryptoHistoryListAdapter extends RecyclerView.Adapter<CryptoHistoryListAdapter.ViewHolder> {

    Context context;
    List<VirusScanData> mScanHistroyList = new ArrayList<>();
    private final static String TAG = "CryptoHistoryListAdapter";
    private PackageManager mPacakageManager;
    int mDetectedCount;
    static int TYPE_HEADER = 0;
    static int TYPE_CELL = 1;

    public CryptoHistoryListAdapter(Context context, List<VirusScanData> scanHistoryList, int detectedCount) {

        this.context = context;
        mScanHistroyList = scanHistoryList;
        mPacakageManager = context.getPackageManager();
        mDetectedCount = detectedCount;


    }


    public void setDetectedCount(int detectedCount) {
        this.mDetectedCount = detectedCount;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == TYPE_HEADER) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_scan_result_header, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_scan_result_cell, parent, false);
        }
        return new ViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {

        if (viewHolder.isHeader) {
            viewHolder.tvScannedCount.setVisibility(View.VISIBLE);
            viewHolder.tvScannedCount.setText(mScanHistroyList.size() + " items Scanned");

            if (mScanHistroyList.size() == 0) {
                viewHolder.tvEmptyScan.setVisibility(View.VISIBLE);
                viewHolder.tvScannedCount.setVisibility(View.GONE);
                viewHolder.tvEmptyScan.setText(R.string.msg_not_scanned_history);

            } else {
                viewHolder.frameEmptyScan.setVisibility(View.VISIBLE);
                viewHolder.linearScanStatus.setVisibility(View.VISIBLE);
                viewHolder.tvEmptyScan.setVisibility(View.GONE);

                if (isDetected()) {
                    viewHolder.tvScanState.setText("RISK");
                    viewHolder.scannedSatatusImg.setBackgroundResource(R.drawable.risk_img);
                    viewHolder.linearBg.setBackgroundResource(R.drawable.main_bacground_gradient_red_risk);
                    viewHolder.tvScanResultDesc.setText(mDetectedCount + " malicious codes");

                } else {
                    viewHolder.tvScanState.setText("SAFE");
                    viewHolder.scannedSatatusImg.setBackgroundResource(R.drawable.safe_img);
                    viewHolder.tvScanResultDesc.setText("No Threats detected.");
                    viewHolder.linearBg.setBackgroundResource(R.drawable.main_bacground_gradient_safe);

                }
            }
        } else {
            final VirusScanData vScanData = getItem(position);
            if (viewHolder.isCell) {
                if (vScanData == null) {
                    return;
                }
                viewHolder.tvPackageName.setText(vScanData.getAppName());
                viewHolder.tvCompanyName.setText(vScanData.getCompanyName());

                String scanResult = vScanData.getScanResult();
                if ("UNDETECTED".equals(scanResult)) {
                    viewHolder.frameSafe.setVisibility(View.VISIBLE);
                    viewHolder.frameDelete.setVisibility(View.GONE);

                } else if (("DETECTED").equals(scanResult)) {
                    viewHolder.frameDelete.setVisibility(View.VISIBLE);
                    viewHolder.frameSafe.setVisibility(View.GONE);

                } else if ("NO_MATCH".equals(scanResult)) {
                    viewHolder.frameDelete.setVisibility(View.GONE);
                    viewHolder.frameSafe.setVisibility(View.VISIBLE);

                }
                try {
                    if (mPacakageManager != null) {
                        Drawable icon = mPacakageManager.getApplicationIcon(vScanData.getPackageName());
                        viewHolder.ivIcon.setImageDrawable(icon);
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                viewHolder.frameDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        deleteIntent(vScanData.getPackageName());
//                packageEventListener.onPackgeDeleted(vScanData.getPackageName());
                    }
                });

            }
        }


    }


    private boolean isDetected() {
        return mDetectedCount > 0;
    }


    // 앱 삭제 처리
    private void deleteIntent(String packageName) {
        Uri uri = Uri.fromParts("package", packageName, null);
        Intent delIntent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE, uri);
        context.startActivity(delIntent);


    }


    @Override
    public int getItemCount() {
        return mScanHistroyList.size() + 1;
    }


    @SuppressLint("LongLogTag")
    public void setData(List<VirusScanData> scanHistroyList, int detectedCount) {
        this.mScanHistroyList = scanHistroyList;
        this.mDetectedCount = detectedCount;
        Log.i(TAG, "crypto app DataList size : " +
                scanHistroyList.size());
        notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else {
            return TYPE_CELL;
        }
    }


    private VirusScanData getItem(int postion) {
        if (postion < 0) {
            return null;

        } else if (getItemViewType(postion) == TYPE_HEADER) {
            return null;
        } else {
            if (mScanHistroyList.size() <= postion - 1) {
                return null;
            }
            return mScanHistroyList.get(postion - 1);
        }

    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        boolean isCell;
        AppCompatImageView ivIcon;
        AppCompatTextView tvPackageName;
        FrameLayout frameDelete;
        FrameLayout frameSafe;
        AppCompatTextView tvSafe;

        boolean isHeader;
        LinearLayout linearScanStatus;
        FrameLayout frameEmptyScan;
        AppCompatTextView tvScanState;  // safe 표시
        ImageView scannedSatatusImg;
        TextView tvScanResultDesc;
        TextView tvScannedCount;   // 88 items Scanned
        TextView tvEmptyScan;
        LinearLayout linearBg;
        AppCompatTextView tvCompanyName;


        public ViewHolder(View itemView, int viewType) {
            super(itemView);

            if (viewType == TYPE_CELL) {
                isCell = true;
                tvPackageName = itemView.findViewById(R.id.tv_pacakage_name);
                ivIcon = itemView.findViewById(R.id.iv_icon);
                tvSafe = itemView.findViewById(R.id.tv_safe);
                frameSafe = itemView.findViewById(R.id.frame_safe);
                frameDelete = itemView.findViewById(R.id.frame_delete);
                tvCompanyName = itemView.findViewById(R.id.tv_company);

            } else if (viewType == TYPE_HEADER) {
                isHeader = true;
                linearScanStatus = itemView.findViewById(R.id.linear_scan_status);
                frameEmptyScan = itemView.findViewById(R.id.frame_empty_scan);
                tvScanState = itemView.findViewById(R.id.tv_scan_state);
                tvEmptyScan = itemView.findViewById(R.id.tv_empty_scan);
                scannedSatatusImg = itemView.findViewById(R.id.scanned_satatus_img);
                tvScanResultDesc = itemView.findViewById(R.id.tv_scan_result_desc);
                tvScannedCount = itemView.findViewById(R.id.tv_scanned_count);
                linearBg = itemView.findViewById(R.id.linear_bg);

            }
        }

    }


}
