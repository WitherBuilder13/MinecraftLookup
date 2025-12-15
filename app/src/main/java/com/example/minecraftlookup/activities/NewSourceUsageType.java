package com.example.minecraftlookup.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.minecraftlookup.util.CommonUtils;
import com.example.minecraftlookup.util.DatabaseHelper;
import com.example.minecraftlookup.R;
import com.example.minecraftlookup.util.ReturnsToHomePageActivity;
import com.example.minecraftlookup.util.SessionData;
import com.example.minecraftlookup.objects.SourceUsageType;

import java.util.ArrayList;

public class NewSourceUsageType extends ReturnsToHomePageActivity {

    Spinner sp_project;
    EditText et_name;
    Button btn_createSourceType;
    TextView tv_error_sourceTypeExists;
    ImageButton btn_cancel, btn_home, btn_addProject;

    DatabaseHelper db;
    ArrayAdapter<String> adapter_sp_project;
    ArrayList<String> list_projectIDs;

    int selection_sp_project;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_source_usage_type);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        et_name = findViewById(R.id.NSUT_et_name);

        sp_project = findViewById(R.id.NSUT_sp_project);

        btn_createSourceType = findViewById(R.id.NSUT_btn_createSourceUsageType);
        btn_addProject = findViewById(R.id.NSUT_btn_addProject);
        btn_cancel = findViewById(R.id.NSUT_btn_cancel);
        btn_home = findViewById(R.id.NSUT_btn_home);

        tv_error_sourceTypeExists = findViewById(R.id.NSUT_tv_error_sourceTypeExists);


        db = new DatabaseHelper(this);

        list_projectIDs = new ArrayList<>();

        adapter_sp_project = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list_projectIDs);

        sp_project.setAdapter(adapter_sp_project);

        selection_sp_project = 0;

        CommonUtils.hideErrors(tv_error_sourceTypeExists);

        initButtonClickListeners();
        initSpinnerChangeListener();
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateLists();
    }

    private void initButtonClickListeners() {
        btn_createSourceType.setOnClickListener(v -> createSourceType());
        btn_cancel.setOnClickListener(v -> finish());
        btn_home.setOnClickListener(v -> home());
        btn_addProject.setOnClickListener(v -> startActivity(new Intent(NewSourceUsageType.this, NewProjectID.class)));
    }

    private void initSpinnerChangeListener() {
        sp_project.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selection_sp_project = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void updateLists() {
        list_projectIDs.clear();
        list_projectIDs.addAll(db.getAllStringIDs(DatabaseHelper.PROJECT_IDS));
        adapter_sp_project.notifyDataSetChanged();
        sp_project.setSelection(selection_sp_project);
    }

    private void createSourceType() {
        CommonUtils.hideErrors(tv_error_sourceTypeExists);

        String sourceType = et_name.getText().toString();
        String project = list_projectIDs.get(sp_project.getSelectedItemPosition());

        if (!db.sourceUsageTypeExists(sourceType, project)) {
            SourceUsageType st = new SourceUsageType();
            st.setName(sourceType);
            st.setProject(project);
            st.setLatestContributor(SessionData.getLoggedInUser());

            db.addSourceUsageTypeToDB(st);
            db.addSourceUsageTypeContribution(db.findLatestID(DatabaseHelper.SOURCE_USAGE_TYPES), SessionData.getLoggedInUser());

            New.close();
            finish();
        } else
            CommonUtils.showError(tv_error_sourceTypeExists);
    }
}