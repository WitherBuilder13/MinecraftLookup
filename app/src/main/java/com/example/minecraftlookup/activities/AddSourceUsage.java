package com.example.minecraftlookup.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.minecraftlookup.objects.SourceUsage;
import com.example.minecraftlookup.objects.SourceUsageType;
import com.example.minecraftlookup.util.DatabaseHelper;
import com.example.minecraftlookup.adapters.ObjectAdapter;
import com.example.minecraftlookup.R;
import com.example.minecraftlookup.objects.MCObject;
import com.example.minecraftlookup.util.ReturnsToHomePageActivity;
import com.example.minecraftlookup.util.SessionData;

import java.util.ArrayList;

public class AddSourceUsage extends ReturnsToHomePageActivity {

    TextView tv_title, tv_objectName, tv_objectTypeProject;
    Spinner sp_type;
    EditText et_description;
    Button btn_objects, btn_addSourceUsage;
    ListView lv_objects;
    ImageButton btn_addType, btn_home, btn_cancel;

    DatabaseHelper db;
    ArrayAdapter<String> adapter_sp_type;
    ObjectAdapter adapter_lv_objects;
    ArrayList<String> list_sourceUsageTypeNames;
    ArrayList<SourceUsageType> list_sourceUsageTypes;
    ArrayList<MCObject> list_objects, objectList;

    int objectID, selection_sp_type;
    boolean isSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_source_usage);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tv_title = findViewById(R.id.ASU_tv_title);
        tv_objectName = findViewById(R.id.ASU_tv_objectName);
        tv_objectTypeProject = findViewById(R.id.ASU_tv_objectTypeProject);

        sp_type = findViewById(R.id.ASU_sp_type);

        et_description = findViewById(R.id.ASU_et_description);

        btn_objects = findViewById(R.id.ASU_btn_objects);
        btn_addSourceUsage = findViewById(R.id.ASU_btn_addSourceUsage);
        btn_cancel = findViewById(R.id.ASU_btn_cancel);
        btn_addType = findViewById(R.id.ASU_btn_addType);
        btn_home = findViewById(R.id.ASU_btn_home);

        lv_objects = findViewById(R.id.ASU_lv_objects);


        db = new DatabaseHelper(this);

        list_sourceUsageTypes = new ArrayList<>();
        list_sourceUsageTypeNames = new ArrayList<>();
        list_objects = new ArrayList<>();

        adapter_sp_type = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list_sourceUsageTypeNames);
        adapter_lv_objects = new ObjectAdapter(this, list_objects);

        sp_type.setAdapter(adapter_sp_type);

        selection_sp_type = 0;

        lv_objects.setAdapter(adapter_lv_objects);

        objectID = getIntent().getIntExtra("objectID", -1);
        isSource = getIntent().getBooleanExtra("isSource", true);

        objectList = new ArrayList<>();

        if (isSource) {
            tv_title.setText("Add Source");
            btn_addSourceUsage.setText("Add Source");
        } else {
            tv_title.setText("Add Usage");
            btn_addSourceUsage.setText("Add Usage");
        }

        initButtonClickListeners();
        initListViewClickListener();
        initSpinnerChangeListener();
        setInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateInfo();
    }

    private void initButtonClickListeners() {
        btn_objects.setOnClickListener(v -> {
            Intent intent = new Intent(AddSourceUsage.this, ObjectSearch.class);
            intent.putExtra("objectListSent", list_objects);
            launcher.launch(intent);
        });
        btn_addSourceUsage.setOnClickListener(v -> addSourceUsage());
        btn_addType.setOnClickListener(v -> startActivity(new Intent(AddSourceUsage.this, NewSourceUsageType.class)));
        btn_cancel.setOnClickListener(v -> finish());
        btn_home.setOnClickListener(v -> home());
    }

    private void initListViewClickListener() {
        lv_objects.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(AddSourceUsage.this, ViewObject.class);
            intent.putExtra("objectID", list_objects.get(position).getId());
            startActivity(intent);
        });
    }

    private void initSpinnerChangeListener() {
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

    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
            Intent data = result.getData();
            objectList = (ArrayList<MCObject>) data.getSerializableExtra("objectListReturned");
        }
    });

    private void updateInfo() {
        list_objects.clear();
        list_objects.addAll(objectList);
        adapter_lv_objects.notifyDataSetChanged();

        list_sourceUsageTypes.clear();
        list_sourceUsageTypeNames.clear();

        list_sourceUsageTypes.addAll(db.getAllSourceUsageTypes());

        for (SourceUsageType sourceUsageType : list_sourceUsageTypes)
            list_sourceUsageTypeNames.add(sourceUsageType.getName() + " | " + sourceUsageType.getProject());

        adapter_sp_type.notifyDataSetChanged();

        sp_type.setSelection(selection_sp_type);
    }

    private void setInfo() {
        MCObject object = db.findObject(objectID);

        tv_objectName.setText(object.getName());
        tv_objectTypeProject.setText(object.getType() + " | " + object.getProject());
    }

    private void addSourceUsage() {
        int type = list_sourceUsageTypes.get(sp_type.getSelectedItemPosition()).getId();
        String description = et_description.getText().toString();

        SourceUsage sourceUsage = new SourceUsage();

        sourceUsage.setObject(objectID);
        sourceUsage.setType(type);
        sourceUsage.setDescription(description);
        sourceUsage.setLatestContributor(SessionData.getLoggedInUser());

        db.addSourceUsageToDB(sourceUsage, isSource);
        db.addSourceUsageContribution(db.findLatestID(isSource ? DatabaseHelper.SOURCES : DatabaseHelper.USAGES), SessionData.getLoggedInUser(), isSource);

        for (MCObject object : list_objects)
            db.addObjectSourceUsageRelationship(db.findLatestID(isSource ? DatabaseHelper.SOURCES : DatabaseHelper.USAGES), object.getId(), isSource);

        finish();
    }
}