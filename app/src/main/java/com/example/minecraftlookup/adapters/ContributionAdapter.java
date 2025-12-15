package com.example.minecraftlookup.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.example.minecraftlookup.R;
import com.example.minecraftlookup.objects.Contribution;
import com.example.minecraftlookup.objects.MCObject;
import com.example.minecraftlookup.objects.SourceUsage;
import com.example.minecraftlookup.objects.SourceUsageType;
import com.example.minecraftlookup.util.CommonUtils;
import com.example.minecraftlookup.util.DatabaseHelper;

import java.util.ArrayList;

public class ContributionAdapter extends BaseAdapter {
    
    Context context;
    ArrayList<Contribution> contributionsArrayList;
    DatabaseHelper db;
    
    public ContributionAdapter(Context c, ArrayList<Contribution> cAL) {
        context = c;
        contributionsArrayList = cAL;
        db = new DatabaseHelper(c);
    }
    
    @Override
    public int getCount() {
        return contributionsArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return contributionsArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = CommonUtils.viewSetup(view, context, R.layout.adapter_cell_contribution);

        TextView tv_contributionType = view.findViewById(R.id.ACC_tv_contributionType);
        TextView tv_name = view.findViewById(R.id.ACC_tv_name);
        TextView tv_typeProject = view.findViewById(R.id.ACC_tv_typeProject);

        Contribution contribution = contributionsArrayList.get(position);

        switch (contribution.getType()) {
            case OBJECT: {
                MCObject object = db.findObject(contribution.getId());

                tv_contributionType.setText("Object");
                tv_name.setText(object.getName());
                tv_typeProject.setText(object.getType() + " | " + object.getProject());

                break;
            }
            case SOURCE: {
                SourceUsage source = db.findSourceUsage(contribution.getId(), true);
                SourceUsageType type = db.findSourceUsageType(source.getType());
                StringBuilder listRelevantObjectNames = new StringBuilder();

                boolean first = true;
                for (MCObject object : db.getAllRelevantObjects(source.getId(), true))
                    if (first) {
                        listRelevantObjectNames.append(object.getName());
                        first = false;
                    } else
                        listRelevantObjectNames.append(", ").append(object.getName());

                tv_contributionType.setText("Source\n(" + db.findObject(source.getObject()).getName() + ")");
                tv_name.setText(listRelevantObjectNames);
                tv_typeProject.setText(type.getName() + " | " + type.getProject());

                break;
            }
            case USAGE:{
                SourceUsage usage = db.findSourceUsage(contribution.getId(), false);
                SourceUsageType type = db.findSourceUsageType(usage.getType());
                StringBuilder listRelevantObjectNames = new StringBuilder();

                boolean first = true;
                for (MCObject object : db.getAllRelevantObjects(usage.getId(), false))
                    if (first) {
                        listRelevantObjectNames.append(object.getName());
                        first = false;
                    } else
                        listRelevantObjectNames.append(", ").append(object.getName());

                tv_contributionType.setText("Usage\n(" + db.findObject(usage.getObject()).getName() + ")");
                tv_name.setText(listRelevantObjectNames);
                tv_typeProject.setText(type.getName() + " | " + type.getProject());

                break;
            }
            case SOURCE_USAGE_TYPE: {
                SourceUsageType sourceUsageType = db.findSourceUsageType(contribution.getId());

                tv_contributionType.setText("Source /\nUsage Type");
                tv_name.setText(sourceUsageType.getName());
                tv_typeProject.setText(sourceUsageType.getProject());
            }
        }
        
        return view;
    }
}
