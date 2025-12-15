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

public class EditObject extends ReturnsToHomePageActivity {

    Spinner sp_project, sp_type;
    EditText et_name;
    Button btn_updateObject, btn_deleteObject;
    TextView tv_error_objectExists;
    ImageButton btn_cancel, btn_home, btn_addProject, btn_addType;

    DatabaseHelper db;
    ArrayAdapter<String> adapter_sp_project, adapter_sp_type;
    ArrayList<String> list_projectIDs, list_objectTypes;

    int objectID, selection_sp_project, selection_sp_type;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_object);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sp_project = findViewById(R.id.EO_sp_project);
        sp_type = findViewById(R.id.EO_sp_type);

        et_name = findViewById(R.id.EO_et_name);

        btn_updateObject = findViewById(R.id.EO_btn_updateObject);
        btn_deleteObject = findViewById(R.id.EO_btn_deleteObject);
        btn_cancel = findViewById(R.id.EO_btn_cancel);
        btn_home = findViewById(R.id.EO_btn_home);
        btn_addProject = findViewById(R.id.EO_btn_addProject);
        btn_addType = findViewById(R.id.EO_btn_addType);

        tv_error_objectExists = findViewById(R.id.EO_tv_error_objectExists);


        db = new DatabaseHelper(this);

        list_projectIDs = new ArrayList<>();
        list_objectTypes = new ArrayList<>();

        adapter_sp_project = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list_projectIDs);
        adapter_sp_type = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list_objectTypes);

        sp_project.setAdapter(adapter_sp_project);
        sp_type.setAdapter(adapter_sp_type);

        CommonUtils.hideErrors(tv_error_objectExists);

        objectID = getIntent().getIntExtra("objectID", -1);

        initButtonClickListeners();
        initSpinnerChangeListeners();
        fillInInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateSpinnerAdapters();
    }

    private void initButtonClickListeners() {
        btn_updateObject.setOnClickListener(v -> updateObject());
        btn_deleteObject.setOnClickListener(v -> deleteObject());
        btn_addProject.setOnClickListener(v -> startActivity(new Intent(EditObject.this, NewProjectID.class)));
        btn_cancel.setOnClickListener(v -> finish());
        btn_home.setOnClickListener(v -> home());
    }

    private void initSpinnerChangeListeners() {
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

    private void updateSpinnerAdapters() {
        list_projectIDs.clear();
        list_projectIDs.addAll(db.getAllStringIDs(DatabaseHelper.PROJECT_IDS));

        list_objectTypes.clear();
        list_objectTypes.addAll(db.getAllStringIDs(DatabaseHelper.OBJECT_TYPES));

        adapter_sp_project.notifyDataSetChanged();
        adapter_sp_type.notifyDataSetChanged();

        sp_project.setSelection(selection_sp_project);
        sp_type.setSelection(selection_sp_type);
    }

    private void updateObject() {
        CommonUtils.hideErrors(tv_error_objectExists);

        String type = list_objectTypes.get(sp_type.getSelectedItemPosition());
        String project = list_projectIDs.get(sp_project.getSelectedItemPosition());
        String name = et_name.getText().toString();

        if (!db.objectExists(type, project, name) && differentFromActiveObject(type, project, name)) {
            MCObject mcObject = db.findObject(objectID);
            int user = SessionData.getLoggedInUser();

            mcObject.setProject(project);
            mcObject.setType(type);
            mcObject.setName(name);
            mcObject.setLatestContributor(user);

            db.updateObjectInDB(mcObject);
            db.addRecentObject(objectID, user);
            db.addObjectContribution(objectID, user);

            finish();
        } else
            CommonUtils.showError(tv_error_objectExists);
    }

    private boolean differentFromActiveObject(String type, String project, String name) {
        MCObject activeObject = db.findObject(objectID);
        return !type.equals(activeObject.getType()) || !project.equals(activeObject.getProject()) || !name.equals(activeObject.getName());
    }

    private void fillInInfo() {
        MCObject activeObject = db.findObject(objectID);

        et_name.setText(activeObject.getName());

        selection_sp_project = list_projectIDs.indexOf(activeObject.getProject());
        selection_sp_type = list_objectTypes.indexOf(activeObject.getType());
    }

    private void deleteObject() {
        db.deleteObjectFromDB(objectID);
        finish();
    }
}