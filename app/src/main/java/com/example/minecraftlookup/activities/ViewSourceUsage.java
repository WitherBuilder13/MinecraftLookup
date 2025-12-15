package com.example.minecraftlookup.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.minecraftlookup.R;
import com.example.minecraftlookup.adapters.ObjectAdapter;
import com.example.minecraftlookup.objects.MCObject;
import com.example.minecraftlookup.objects.SourceUsage;
import com.example.minecraftlookup.objects.SourceUsageType;
import com.example.minecraftlookup.util.DatabaseHelper;
import com.example.minecraftlookup.util.ReturnsToHomePageActivity;

import java.util.ArrayList;

public class ViewSourceUsage extends ReturnsToHomePageActivity {

    TextView tv_title, tv_objectName, tv_objectTypeProject, tv_type, tv_description, tv_lastChange;
    ListView lv_objects;
    Button btn_edit;
    ImageButton btn_back, btn_home;

    DatabaseHelper db;
    ObjectAdapter adapter_lv_objects;
    ArrayList<MCObject> list_objects;

    int sourceUsageID;
    boolean isSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_source_usage);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tv_title = findViewById(R.id.VSU_tv_title);
        tv_objectName = findViewById(R.id.VSU_tv_objectName);
        tv_objectTypeProject = findViewById(R.id.VSU_tv_objectTypeProject);
        tv_type = findViewById(R.id.VSU_tv_type);
        tv_description = findViewById(R.id.VSU_tv_description);
        tv_lastChange = findViewById(R.id.VSU_tv_lastChange);

        lv_objects = findViewById(R.id.VSU_lv_objects);

        btn_edit = findViewById(R.id.VSU_btn_edit);
        btn_back = findViewById(R.id.VSU_btn_back);
        btn_home = findViewById(R.id.VSU_btn_home);

        db = new DatabaseHelper(this);

        sourceUsageID = getIntent().getIntExtra("sourceUsageID", -1);
        isSource = getIntent().getBooleanExtra("isSource", true);

        list_objects = new ArrayList<>();

        adapter_lv_objects = new ObjectAdapter(this, list_objects);

        lv_objects.setAdapter(adapter_lv_objects);

        tv_title.setText(isSource ? "Source" : "Usage");

        initButtonClickListeners();
        initListViewClickListener();
    }

    @Override
    protected void onResume() {
        super.onResume();

        fillInInfo();
    }

    private void initButtonClickListeners() {
        btn_edit.setOnClickListener(v -> {
            Intent intent = new Intent(ViewSourceUsage.this, EditSourceUsage.class);
            intent.putExtra("sourceUsageID", sourceUsageID);
            intent.putExtra("isSource", isSource);
            startActivity(intent);
        });
        btn_back.setOnClickListener(v -> finish());
        btn_home.setOnClickListener(v -> home());
    }

    private void initListViewClickListener() {
        lv_objects.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(ViewSourceUsage.this, ViewObject.class);
            intent.putExtra("objectID", list_objects.get(position).getId());
            startActivity(intent);
        });
    }

    private void fillInInfo() {
        SourceUsage sourceUsage = db.findSourceUsage(sourceUsageID, isSource);
        MCObject object = db.findObject(sourceUsage.getObject());
        SourceUsageType type = db.findSourceUsageType(sourceUsage.getType());

        tv_objectName.setText(object.getName());
        tv_objectTypeProject.setText(object.getType() + " | " + object.getProject());
        tv_type.setText(type.getName() + " | " + type.getProject());
        tv_description.setText(sourceUsage.getDescription());
        tv_lastChange.setText("Last Change: " + db.findUser(sourceUsage.getLatestContributor()));

        list_objects.clear();
        list_objects.addAll(db.getAllRelevantObjects(sourceUsageID, isSource));
        adapter_lv_objects.notifyDataSetChanged();
    }
}