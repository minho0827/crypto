package com.cloudbric.cryptobric.adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.cloudbric.cryptobric.R;
import com.cloudbric.cryptobric.data.LocalAppData;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class MyAppListAdapter extends RecyclerView.Adapter<MyAppListAdapter.ViewHolder> {
    Context context;
    private PackageManager mPacakageManager;
    private boolean editMode = false;
    List<LocalAppData> mLocalAppList = new ArrayList<>();
    private final static String TAG = "MyAppListAdapter";
    OnCheckedListener onCheckedListener;

    private ArrayList<Integer> mSectionPositions;

    public MyAppListAdapter(Context c, List<LocalAppData> appList) {

        this.context = c;
        mLocalAppList = appList;
        mPacakageManager = this.context.getPackageManager();
        this.onCheckedListener = (OnCheckedListener) c;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_app_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        final LocalAppData localAppData = mLocalAppList.get(position);



        if (localAppData == null) {
            return;
        }


        viewHolder.cb.setOnCheckedChangeListener(null);
        viewHolder.tvPackageName.setText(localAppData.getPackageInfo().applicationInfo.loadLabel(mPacakageManager).toString());

        viewHolder.cb.setChecked(localAppData.isSelected);

        try {
            if (mPacakageManager != null) {
                Drawable icon = mPacakageManager.getApplicationIcon(localAppData.getPackageInfo().packageName);
                viewHolder.ivIcon.setImageDrawable(icon);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        viewHolder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                boolean checked = !viewHolder.cb.isChecked();

                viewHolder.cb.setSelected(isChecked);
                localAppData.isSelected = isChecked;
                Log.d(TAG, "LocalAppData[" + localAppData.getPackageInfo().packageName + "].isSelected(): " + checked);

                onCheckedListener.onItemChecked();
            }
        });
    }


    @Override
    public int getItemCount() {
        return mLocalAppList.size();
    }

    public List<LocalAppData> getSelectedItems() {
        List<LocalAppData> scanList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(mLocalAppList)) {
            for (LocalAppData localAppData : mLocalAppList) {
                if (localAppData.isSelected()) {
                    scanList.add(localAppData);


                }
            }
        }
        return scanList;

    }



//    @Override
//    public int getSectionForPosition(int position) {
//        return 0;
//    }
//
//    @Override
//    public Object[] getSections() {
//        List<String> sections = new ArrayList<>(27);
//
//
//
//        mSectionPositions = new ArrayList<>(26);
//        for (int i = 0, size = mLocalAppList.size(); i < size; i++) {
//
//            LocalAppData localAppData = mLocalAppList.get(i);
//            char c = localAppData.getPackageInfo().applicationInfo.loadLabel(mPacakageManager).toString().charAt(0);
//
//            if( (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
//                String section = String.valueOf(c).toUpperCase();
//                if (!sections.contains(section)) {
//                    sections.add(section);
//                    mSectionPositions.add(i);
//                }
//            }
//
//
//        }
//        return sections.toArray(new String[0]);
//    }
//
//    @Override
//    public int getPositionForSection(int sectionIndex) {
//        return mSectionPositions.get(sectionIndex);
//    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        final AppCompatTextView tvPackageName;
        final CheckBox cb;
        AppCompatImageView ivIcon;


        public ViewHolder(View view) {
            super(view);
            cb = view.findViewById(R.id.btn_check);
            tvPackageName = view.findViewById(R.id.tv_pacakage_name);
            ivIcon = view.findViewById(R.id.iv_icon);
        }

    }

    private void onCheckboxClicked(boolean checked) {
        if (checked) {
            setEditMode(editMode);
        } else {

        }
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
        if (editMode == false) {
//            clerSelection();
        }
    }


    public interface OnCheckedListener {
        void onItemChecked();
    }


    public interface SendApplication {
        void SendApplication(List<String> appList);
    }


}

