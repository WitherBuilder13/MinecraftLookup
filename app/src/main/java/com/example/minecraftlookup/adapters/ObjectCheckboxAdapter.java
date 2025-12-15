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
import com.example.minecraftlookup.objects.MCObject;
import com.example.minecraftlookup.objects.Toggleable;
import com.example.minecraftlookup.util.CommonUtils;
import com.example.minecraftlookup.util.HasFilteredList;

import java.util.ArrayList;

public class ObjectCheckboxAdapter extends BaseAdapter {

    Context context;
    ArrayList<Toggleable<MCObject>> toggleMCObjectArrayList;
    HasFilteredList activity;

    boolean enableListener = true;

    public ObjectCheckboxAdapter(Context c, ArrayList<Toggleable<MCObject>> tOAL, HasFilteredList a) {
        context = c;
        toggleMCObjectArrayList = tOAL;
        activity = a;
    }

    @Override
    public int getCount() {
        return toggleMCObjectArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return toggleMCObjectArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = CommonUtils.viewSetup(view, context, R.layout.adapter_cell_object_checkbox);

        CheckBox cb_main = view.findViewById(R.id.ACOC_cb_main);
        TextView tv_name = view.findViewById(R.id.ACOC_tv_name);
        TextView tv_typeProject = view.findViewById(R.id.ACOC_tv_typeProject);

        Toggleable<MCObject> to = toggleMCObjectArrayList.get(position);
        MCObject object = to.getEntry();

        tv_name.setText(object.getName());
        cb_main.setChecked(to.isToggled());

        tv_typeProject.setText(object.getType() + " | " + object.getProject());

        cb_main.setOnCheckedChangeListener(null);
        cb_main.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (enableListener) {
                to.setToggled(isChecked);
                toggleMCObjectArrayList.set(position, to);
                activity.updateFilteredListAdapter();

                enableListener = false;
                buttonView.postDelayed(() -> enableListener = true, 100);
            }
        });

        return view;
    }
}
