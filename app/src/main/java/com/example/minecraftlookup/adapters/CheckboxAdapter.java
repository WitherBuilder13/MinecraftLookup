package com.example.minecraftlookup.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;


import com.example.minecraftlookup.R;
import com.example.minecraftlookup.objects.Toggleable;
import com.example.minecraftlookup.util.CommonUtils;
import com.example.minecraftlookup.util.HasFilteredList;

import java.util.ArrayList;

public class CheckboxAdapter extends BaseAdapter {

    Context context;
    ArrayList<Toggleable<String>> toggleStringArrayList;
    HasFilteredList activity;

    public CheckboxAdapter(Context c, ArrayList<Toggleable<String>> tSAL, HasFilteredList a) {
        context = c;
        toggleStringArrayList = tSAL;
        activity = a;
    }

    @Override
    public int getCount() {
        return toggleStringArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return toggleStringArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = CommonUtils.viewSetup(view, context, R.layout.adapter_cell_checkbox);

        CheckBox cb_main = view.findViewById(R.id.ACC_cb_main);
        TextView tv_text = view.findViewById(R.id.ACCb_tv_text);

        Toggleable<String> ts = toggleStringArrayList.get(position);

        tv_text.setText(ts.getEntry());
        cb_main.setChecked(ts.isToggled());

        cb_main.setOnCheckedChangeListener(null);
        cb_main.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ts.setToggled(isChecked);
            activity.updateFilteredListAdapter();
        });

        return view;
    }
}
