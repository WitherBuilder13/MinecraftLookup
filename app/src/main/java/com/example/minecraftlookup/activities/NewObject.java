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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.minecraftlookup.util.CommonUtils;
import com.example.minecraftlookup.util.DatabaseHelper;
import com.example.minecraftlookup.R;
import com.example.minecraftlookup.util.ReturnsToHomePageActivity;
import com.example.minecraftlookup.util.SessionData;
import com.example.minecraftlookup.objects.MCObject;

import java.util.ArrayList;

public class NewObject extends ReturnsToHomePageActivity {

    Spinner sp_project, sp_type;
    EditText et_name;
    Button btn_createObject;
    TextView tv_error_objectExists;
    ImageButton btn_cancel, btn_home, btn_addProject, btn_addType;

    DatabaseHelper db;
    ArrayAdapter<String> adapter_sp_project, adapter_sp_type;
    ArrayList<String> list_projectIDs, list_objectTypes;

    int selection_sp_project, selection_sp_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_object);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sp_project = findViewById(R.id.NO_sp_project);
        sp_type = findViewById(R.id.NO_sp_type);

        et_name = findViewById(R.id.NO_et_name);

        btn_createObject = findViewById(R.id.NO_btn_createObject);
        btn_cancel = findViewById(R.id.NO_btn_cancel);
        btn_home = findViewById(R.id.NO_btn_home);
        btn_addProject = findViewById(R.id.NO_btn_addProject);
        btn_addType = findViewById(R.id.NO_btn_addType);

        tv_error_objectExists = findViewById(R.id.NO_tv_error_objectExists);


        db = new DatabaseHelper(this);

        list_projectIDs = new ArrayList<>();
        list_objectTypes = new ArrayList<>();

        adapter_sp_project = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list_projectIDs);
        adapter_sp_type = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list_objectTypes);

        sp_project.setAdapter(adapter_sp_project);
        sp_type.setAdapter(adapter_sp_type);

        selection_sp_project = 0;
        selection_sp_type = 0;

        CommonUtils.hideErrors(tv_error_objectExists);

        initButtonClickListeners();
        initSpinnerChangeListener();
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateLists();
    }

    private void initButtonClickListeners() {
        btn_createObject.setOnClickListener(v -> createObject());
        btn_cancel.setOnClickListener(v -> finish());
        btn_home.setOnClickListener(v -> home());
        btn_addProject.setOnClickListener(v -> startActivity(new Intent(NewObject.this, NewProjectID.class)));
        btn_addType.setOnClickListener(v -> startActivity(new Intent(NewObject.this, NewObjectType.class)));
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
        sp_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selection_sp_type = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void updateLists() {
        list_projectIDs.clear();
        list_objectTypes.clear();

        list_projectIDs.addAll(db.getAllStringIDs(DatabaseHelper.PROJECT_IDS));
        list_objectTypes.addAll(db.getAllStringIDs(DatabaseHelper.OBJECT_TYPES));

        adapter_sp_project.notifyDataSetChanged();
        adapter_sp_type.notifyDataSetChanged();

        sp_project.setSelection(selection_sp_project);
        sp_type.setSelection(selection_sp_type);
    }

    private void createObject() {
        CommonUtils.hideErrors(tv_error_objectExists);

        String type = list_objectTypes.get(sp_type.getSelectedItemPosition());
        String project = list_projectIDs.get(sp_project.getSelectedItemPosition());
        String name = et_name.getText().toString();

        if (!db.objectExists(type, project, name)) {
            MCObject mcObject = new MCObject();
            int user = SessionData.getLoggedInUser();

            mcObject.setType(type);
            mcObject.setProject(project);
            mcObject.setName(name);
            mcObject.setLatestContributor(user);

            db.addObjectToDB(mcObject);
            db.addRecentObject(db.findLatestID(DatabaseHelper.OBJECTS), user);
            db.addObjectContribution(db.findLatestID(DatabaseHelper.OBJECTS), user);

            New.close();
            finish();
        } else
            CommonUtils.showError(tv_error_objectExists);
    }
}