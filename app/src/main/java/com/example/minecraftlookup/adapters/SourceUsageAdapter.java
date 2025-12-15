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
import com.example.minecraftlookup.objects.SourceUsage;
import com.example.minecraftlookup.objects.SourceUsageType;
import com.example.minecraftlookup.util.CommonUtils;
import com.example.minecraftlookup.util.DatabaseHelper;

import java.util.ArrayList;

public class SourceUsageAdapter extends BaseAdapter {

    Context context;
    ArrayList<SourceUsage> sourceUsagesArrayList;
    DatabaseHelper db;
    boolean isSource;

    public SourceUsageAdapter(Context c, ArrayList<SourceUsage> sAL, boolean s) {
        context = c;
        sourceUsagesArrayList = sAL;
        db = new DatabaseHelper(c);
        isSource = s;
    }

    @Override
    public int getCount() {
        return sourceUsagesArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return sourceUsagesArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = CommonUtils.viewSetup(view, context, R.layout.adapter_cell_source_usage);

        TextView tv_type = view.findViewById(R.id.ACSU_tv_type);
        TextView tv_relevantObjects = view.findViewById(R.id.ACSU_tv_relevantObjects);

        SourceUsage sourceUsage = sourceUsagesArrayList.get(position);
        StringBuilder listRelevantObjectNames = new StringBuilder();

        boolean first = true;
        for (MCObject object : db.getAllRelevantObjects(sourceUsage.getId(), isSource))
            if (first) {
                listRelevantObjectNames.append(object.getName());
                first = false;
            } else
                listRelevantObjectNames.append(", ").append(object.getName());

        SourceUsageType type = db.findSourceUsageType(sourceUsage.getType());

        tv_type.setText(type.getName() + " | " + type.getProject());
        tv_relevantObjects.setText(listRelevantObjectNames.toString());

        return view;
    }
}
