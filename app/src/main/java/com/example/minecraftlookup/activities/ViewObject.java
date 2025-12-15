package com.example.minecraftlookup.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.minecraftlookup.util.DatabaseHelper;
import com.example.minecraftlookup.R;
import com.example.minecraftlookup.util.ReturnsToHomePageActivity;
import com.example.minecraftlookup.util.SessionData;
import com.example.minecraftlookup.objects.MCObject;

public class ViewObject extends ReturnsToHomePageActivity {

    TextView tv_title, tv_typeAndProject, tv_lastChange;
    Button btn_findSources, btn_findUsages, btn_addSource, btn_addUsage, btn_edit;
    ImageButton btn_back, btn_home;

    DatabaseHelper db;
    int objectID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_object);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tv_title = findViewById(R.id.VO_tv_title);
        tv_typeAndProject = findViewById(R.id.VO_tv_typeAndProject);
        tv_lastChange = findViewById(R.id.VO_tv_lastChange);

        btn_findSources = findViewById(R.id.VO_btn_findSources);
        btn_findUsages = findViewById(R.id.VO_btn_findUsages);
        btn_addSource = findViewById(R.id.VO_btn_addSource);
        btn_addUsage = findViewById(R.id.VO_btn_addUsage);
        btn_edit = findViewById(R.id.VO_btn_edit);
        btn_back = findViewById(R.id.VO_btn_back);
        btn_home = findViewById(R.id.VO_btn_home);

        db = new DatabaseHelper(this);

        objectID = getIntent().getIntExtra("objectID", -1);

        initButtonClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (db.findObject(objectID) == null)
            finish();
        else
            fillInInfo();
    }

    private void initButtonClickListeners() {
        btn_findSources.setOnClickListener(v -> {
            Intent intent = new Intent(ViewObject.this, FindSourceUsages.class);
            intent.putExtra("objectID", objectID);
            intent.putExtra("isSource", true);
            startActivity(intent);
        });
        btn_findUsages.setOnClickListener(v -> {
            Intent intent = new Intent(ViewObject.this, FindSourceUsages.class);
            intent.putExtra("objectID", objectID);
            intent.putExtra("isSource", false);
            startActivity(intent);
        });
        btn_addSource.setOnClickListener(v -> {
            Intent intent = new Intent(ViewObject.this, AddSourceUsage.class);
            intent.putExtra("objectID", objectID);
            intent.putExtra("isSource", true);
            startActivity(intent);
        });
        btn_addUsage.setOnClickListener(v -> {
            Intent intent = new Intent(ViewObject.this, AddSourceUsage.class);
            intent.putExtra("objectID", objectID);
            intent.putExtra("isSource", false);
            startActivity(intent);
        });
        btn_edit.setOnClickListener(v -> {
            Intent intent = new Intent(ViewObject.this, EditObject.class);
            intent.putExtra("objectID", objectID);
            startActivity(intent);
        });
        btn_back.setOnClickListener(v -> finish());
        btn_home.setOnClickListener(v -> home());
    }

    private void fillInInfo() {
        MCObject object = db.findObject(objectID);

        tv_title.setText(object.getName());
        tv_typeAndProject.setText(object.getType() + " | " + object.getProject());
        tv_lastChange.setText(object.getLatestContributor() == 0 ? db.findUser(object.getLatestContributor()) : "Last Change: " + db.findUser(object.getLatestContributor()));

        db.addRecentObject(object.getId(), SessionData.getLoggedInUser());
    }
}