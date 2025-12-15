package com.example.minecraftlookup.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.minecraftlookup.R;
import com.example.minecraftlookup.activities.MainActivity;
import com.example.minecraftlookup.objects.MCObject;
import com.example.minecraftlookup.util.CommonUtils;

import java.util.ArrayList;

public class ObjectAdapter extends BaseAdapter {

    Context context;
    ArrayList<MCObject> objectsArrayList;

    public ObjectAdapter(Context c, ArrayList<MCObject> oAL) {
        context = c;
        objectsArrayList = oAL;
    }

    @Override
    public int getCount() {
        return objectsArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return objectsArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = CommonUtils.viewSetup(view, context, R.layout.adapter_cell_object);

        TextView tv_name = view.findViewById(R.id.ACO_tv_name);
        TextView tv_typeProject = view.findViewById(R.id.ACO_tv_typeProject);

        MCObject object = objectsArrayList.get(position);

        tv_name.setText(object.getName());
        tv_typeProject.setText(object.getType() + " | " + object.getProject());

        return view;
    }
}
