package com.example.minecraftlookup.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.minecraftlookup.R;
import com.example.minecraftlookup.activities.MainActivity;
import com.example.minecraftlookup.objects.SourceUsageType;
import com.example.minecraftlookup.objects.Toggleable;
import com.example.minecraftlookup.util.CommonUtils;
import com.example.minecraftlookup.util.HasFilteredList;

import java.util.ArrayList;

public class SourceUsageTypeCheckboxAdapter extends BaseAdapter {
    Context context;
    ArrayList<Toggleable<SourceUsageType>> toggleSourceUsageTypeArrayList;
    HasFilteredList activity;

    public SourceUsageTypeCheckboxAdapter(Context c, ArrayList<Toggleable<SourceUsageType>> tSUTAL, HasFilteredList a) {
        context = c;
        toggleSourceUsageTypeArrayList = tSUTAL;
        activity = a;
    }

    @Override
    public int getCount() {
        return toggleSourceUsageTypeArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return toggleSourceUsageTypeArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = CommonUtils.viewSetup(view, context, R.layout.adapter_cell_source_usage_type_checkbox);

        CheckBox cb_main = view.findViewById(R.id.ACSUTC_cb_main);
        TextView tv_name = view.findViewById(R.id.ACSUTC_tv_name);
        TextView tv_project = view.findViewById(R.id.ACSUTC_tv_project);

        Toggleable<SourceUsageType> tSUT = toggleSourceUsageTypeArrayList.get(position);

        tv_name.setText(tSUT.getEntry().getName());
        cb_main.setChecked(tSUT.isToggled());

        tv_project.setText(tSUT.getEntry().getProject());

        cb_main.setOnCheckedChangeListener(null);
        cb_main.setOnCheckedChangeListener((buttonView, isChecked) -> {
            tSUT.setToggled(isChecked);
            toggleSourceUsageTypeArrayList.set(position, tSUT);
            activity.updateFilteredListAdapter();
        });

        return view;
    }


}
