package com.example.minecraftlookup.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.minecraftlookup.R;
import com.example.minecraftlookup.adapters.CheckboxAdapter;
import com.example.minecraftlookup.adapters.ObjectAdapter;
import com.example.minecraftlookup.adapters.SourceUsageAdapter;
import com.example.minecraftlookup.adapters.SourceUsageTypeCheckboxAdapter;
import com.example.minecraftlookup.objects.MCObject;
import com.example.minecraftlookup.objects.SourceUsage;
import com.example.minecraftlookup.objects.SourceUsageType;
import com.example.minecraftlookup.objects.Toggleable;
import com.example.minecraftlookup.util.CommonUtils;
import com.example.minecraftlookup.util.DatabaseHelper;
import com.example.minecraftlookup.util.HasFilteredList;
import com.example.minecraftlookup.util.ReturnsToHomePageActivity;

import java.util.ArrayList;

public class FindSourceUsages extends ReturnsToHomePageActivity implements HasFilteredList {

    TextView tv_title;
    EditText et_search;
    ListView lv_sourceUsages, lv_objects, lv_types, lv_contributors;
    CheckBox cb_typeAll, cb_contributorAll;
    Button btn_objects, btn_resetSearch;
    ImageButton btn_back, btn_home;

    DatabaseHelper db;
    SourceUsageTypeCheckboxAdapter adapter_lv_types;
    CheckboxAdapter adapter_lv_contributors;
    ObjectAdapter adapter_lv_objects;
    SourceUsageAdapter adapter_lv_sourceUsages;
    ArrayList<Toggleable<SourceUsageType>> list_types;
    ArrayList<Toggleable<String>> list_contributors;
    ArrayList<MCObject> list_objects, objectList;
    ArrayList<SourceUsage> list_sourceUsages;

    int objectID;
    boolean isSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_find_sources_usages);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tv_title = findViewById(R.id.FSU_tv_title);

        et_search = findViewById(R.id.FSU_et_search);

        lv_sourceUsages = findViewById(R.id.FSU_lv_sourceUsages);
        lv_objects = findViewById(R.id.FSU_lv_objects);
        lv_types = findViewById(R.id.FSU_lv_types);
        lv_contributors = findViewById(R.id.FSU_lv_contributors);

        cb_typeAll = findViewById(R.id.FSU_cb_typeAll);
        cb_contributorAll = findViewById(R.id.FSU_cb_contributorAll);

        btn_objects = findViewById(R.id.FSU_btn_objects);
        btn_resetSearch = findViewById(R.id.FSU_btn_resetSearch);
        btn_back = findViewById(R.id.FSU_btn_back);
        btn_home = findViewById(R.id.FSU_btn_home);

        db = new DatabaseHelper(this);

        objectID = getIntent().getIntExtra("objectID", -1);
        isSource = getIntent().getBooleanExtra("isSource", true);

        list_sourceUsages = new ArrayList<>();
        list_objects = new ArrayList<>();
        list_types = new ArrayList<>();
        list_contributors = new ArrayList<>();

        adapter_lv_sourceUsages = new SourceUsageAdapter(this, list_sourceUsages, isSource);
        adapter_lv_objects = new ObjectAdapter(this, list_objects);
        adapter_lv_types = new SourceUsageTypeCheckboxAdapter(this, list_types, this);
        adapter_lv_contributors = new CheckboxAdapter(this, list_contributors, this);

        lv_sourceUsages.setAdapter(adapter_lv_sourceUsages);
        lv_objects.setAdapter(adapter_lv_objects);
        lv_types.setAdapter(adapter_lv_types);
        lv_contributors.setAdapter(adapter_lv_contributors);

        objectList = new ArrayList<>();

        if (isSource) {
            tv_title.setText("Find Sources");
            et_search.setHint("Find Sources...");
        } else {
            tv_title.setText("Find Usages");
            et_search.setHint("Find Usages...");
        }

        initButtonClickListeners();
        initListViewClickListener();
        initCheckboxListener();
        initTextChangedListener();
    }

    @Override
    protected void onResume() {
        super.onResume();

        list_objects.clear();
        list_objects.addAll(objectList);
        adapter_lv_objects.notifyDataSetChanged();
        resetSearch();
    }

    private void initButtonClickListeners() {
        btn_objects.setOnClickListener(v -> {
            Intent intent = new Intent(FindSourceUsages.this, ObjectSearch.class);
            intent.putExtra("objectListSent", list_objects);
            launcher.launch(intent);
        });
        btn_resetSearch.setOnClickListener(v -> resetSearch());
        btn_back.setOnClickListener(v -> finish());
        btn_home.setOnClickListener(v -> home());
    }

    private void initListViewClickListener() {
        lv_sourceUsages.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(FindSourceUsages.this, ViewSourceUsage.class);
            intent.putExtra("sourceUsageID", list_sourceUsages.get(position).getId());
            intent.putExtra("isSource", isSource);
            startActivity(intent);
        });
        lv_objects.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(FindSourceUsages.this, ViewObject.class);
            intent.putExtra("objectID", list_objects.get(position).getId());
            startActivity(intent);
        });
    }

    private void initCheckboxListener() {
        cb_typeAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            CommonUtils.resetCheckboxAdapter(adapter_lv_types, list_types, CommonUtils.createCheckedListFromCheckbox(db.getAllSourceUsageTypes(), cb_typeAll));
            updateFilteredListAdapter();
        });
        cb_contributorAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            CommonUtils.resetCheckboxAdapter(adapter_lv_contributors, list_contributors, CommonUtils.createCheckedListFromCheckbox(db.findUsers(""), cb_contributorAll), true);
            updateFilteredListAdapter();
        });
    }

    private void initTextChangedListener() {
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateFilteredListAdapter();
            }
        });
    }

    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
            Intent data = result.getData();
            objectList = (ArrayList<MCObject>) data.getSerializableExtra("objectListReturned");
        }
    });

    @Override
    public void updateFilteredListAdapter() {
        list_sourceUsages.clear();
        list_sourceUsages.addAll(db.findSourceUsages(
                objectID, list_objects, CommonUtils.getAllToggleEntries(list_types, true), CommonUtils.getAllToggleEntries(list_contributors, true), et_search.getText().toString(), isSource
        ));
        adapter_lv_sourceUsages.notifyDataSetChanged();
    }

    private void resetSearch() {
        CommonUtils.clearEditTexts(et_search);
        CommonUtils.resetCheckboxes(cb_typeAll, cb_contributorAll);

        CommonUtils.resetCheckboxAdapter(adapter_lv_types, list_types, CommonUtils.createCheckedListFromCheckbox(db.getAllSourceUsageTypes(), cb_typeAll));
        CommonUtils.resetCheckboxAdapter(adapter_lv_contributors, list_contributors, CommonUtils.createCheckedListFromCheckbox(db.findUsers(""), cb_contributorAll), true);

        updateFilteredListAdapter();
    }
}