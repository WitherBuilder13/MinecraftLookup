package com.example.minecraftlookup.activities;

import static com.example.minecraftlookup.util.CommonUtils.createCheckedList;
import static com.example.minecraftlookup.util.CommonUtils.createCheckedListFromCheckbox;
import static com.example.minecraftlookup.util.CommonUtils.getAllToggleEntries;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.minecraftlookup.R;
import com.example.minecraftlookup.adapters.CheckboxAdapter;
import com.example.minecraftlookup.adapters.ObjectCheckboxAdapter;
import com.example.minecraftlookup.objects.MCObject;
import com.example.minecraftlookup.objects.Toggleable;
import com.example.minecraftlookup.util.CommonUtils;
import com.example.minecraftlookup.util.DatabaseHelper;
import com.example.minecraftlookup.util.HasFilteredList;
import com.example.minecraftlookup.util.ReturnsToHomePageActivity;

import java.util.ArrayList;

public class ObjectSearch extends ReturnsToHomePageActivity implements HasFilteredList {

    EditText et_search;
    ListView lv_types, lv_projects, lv_contributors, lv_objects;
    CheckBox cb_typeAll, cb_projectAll, cb_contributorAll;
    Button btn_ok, btn_new, btn_resetSearch;
    ImageButton btn_cancel, btn_home;

    DatabaseHelper db;
    CheckboxAdapter adapter_lv_types, adapter_lv_projects, adapter_lv_contributors;
    ObjectCheckboxAdapter adapter_lv_objects;
    ArrayList<Toggleable<MCObject>> list_objects;
    ArrayList<Toggleable<String>> list_types, list_projects, list_contributors;

    ArrayList<MCObject> list_enabled_objects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_object_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        et_search = findViewById(R.id.OS_et_search);

        lv_types = findViewById(R.id.OS_lv_types);
        lv_projects = findViewById(R.id.OS_lv_projects);
        lv_contributors = findViewById(R.id.OS_lv_contributors);
        lv_objects = findViewById(R.id.OS_lv_objects);

        cb_typeAll = findViewById(R.id.OS_cb_typeAll);
        cb_projectAll = findViewById(R.id.OS_cb_projectAll);
        cb_contributorAll = findViewById(R.id.OS_cb_contributorAll);

        btn_ok = findViewById(R.id.OS_btn_ok);
        btn_new = findViewById(R.id.OS_btn_new);
        btn_resetSearch = findViewById(R.id.OS_btn_resetSearch);
        btn_cancel = findViewById(R.id.OS_btn_cancel);
        btn_home = findViewById(R.id.OS_btn_home);


        db = new DatabaseHelper(this);

        list_objects = new ArrayList<>();
        list_types = new ArrayList<>();
        list_projects = new ArrayList<>();
        list_contributors = new ArrayList<>();

        adapter_lv_objects = new ObjectCheckboxAdapter(this, list_objects, this);
        adapter_lv_types = new CheckboxAdapter(this, list_types, this);
        adapter_lv_projects = new CheckboxAdapter(this, list_projects, this);
        adapter_lv_contributors = new CheckboxAdapter(this, list_contributors, this);

        lv_objects.setAdapter(adapter_lv_objects);
        lv_types.setAdapter(adapter_lv_types);
        lv_projects.setAdapter(adapter_lv_projects);
        lv_contributors.setAdapter(adapter_lv_contributors);

        list_enabled_objects = new ArrayList<>((ArrayList<MCObject>) getIntent().getSerializableExtra("objectListSent"));
        list_objects.addAll(createCheckedList(list_enabled_objects, true));

        initButtonClickListeners();
        initListViewClickListener();
        initCheckboxListeners();
        initTextChangedListener();
    }

    @Override
    protected void onResume() {
        super.onResume();

        resetSearch();
    }

    private void initButtonClickListeners() {
        btn_ok.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("objectListReturned", list_enabled_objects);
            setResult(AppCompatActivity.RESULT_OK, resultIntent);
            finish();
        });
        btn_new.setOnClickListener(v -> startActivity(new Intent(ObjectSearch.this, NewObject.class)));
        btn_resetSearch.setOnClickListener(v ->  resetSearch());
        btn_cancel.setOnClickListener(v -> finish());
        btn_home.setOnClickListener(v -> home());
    }

    private void initListViewClickListener() {
        lv_objects.setOnItemLongClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(ObjectSearch.this, ViewObject.class);
            intent.putExtra("objectID", list_objects.get(position).getEntry().getId());
            startActivity(intent);

            return true;
        });
    }

    private void initCheckboxListeners() {
        cb_typeAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            CommonUtils.resetCheckboxAdapter(adapter_lv_types, list_types, createCheckedListFromCheckbox(db.getAllStringIDs(DatabaseHelper.OBJECT_TYPES), cb_typeAll));
            updateFilteredListAdapter();
        });
        cb_projectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            CommonUtils.resetCheckboxAdapter(adapter_lv_projects, list_projects, createCheckedListFromCheckbox(db.getAllStringIDs(DatabaseHelper.PROJECT_IDS), cb_projectAll));
            updateFilteredListAdapter();
        });
        cb_contributorAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            CommonUtils.resetCheckboxAdapter(adapter_lv_contributors, list_contributors, createCheckedListFromCheckbox(db.findUsers(""), cb_contributorAll), true);
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

    @Override
    public void updateFilteredListAdapter() {
        ArrayList<Toggleable<MCObject>> currentToggleList = new ArrayList<>(list_objects);

        list_enabled_objects.clear();

        list_enabled_objects.addAll(getAllToggleEntries(CommonUtils.getCheckedList(db.getAllObjects(), currentToggleList), true));

        ArrayList<MCObject> listDisabledObjects = new ArrayList<>(getAllToggleEntries(CommonUtils.getCheckedList(db.findObjects(
                getAllToggleEntries(list_types, true), getAllToggleEntries(list_projects, true), getAllToggleEntries(list_contributors, true), et_search.getText().toString()
        ), currentToggleList), false));

        ArrayList<Toggleable<MCObject>> finalList = new ArrayList<>();
        finalList.addAll(createCheckedList(list_enabled_objects, true));
        finalList.addAll(createCheckedList(listDisabledObjects, false));

        list_objects.clear();
        list_objects.addAll(finalList);
        adapter_lv_objects.notifyDataSetChanged();
    }

    private void resetSearch() {
        CommonUtils.clearEditTexts(et_search);
        CommonUtils.resetCheckboxes(cb_typeAll, cb_projectAll, cb_contributorAll);

        CommonUtils.resetCheckboxAdapter(adapter_lv_types, list_types, createCheckedListFromCheckbox(db.getAllStringIDs(DatabaseHelper.OBJECT_TYPES), cb_typeAll));
        CommonUtils.resetCheckboxAdapter(adapter_lv_projects, list_projects, createCheckedListFromCheckbox(db.getAllStringIDs(DatabaseHelper.PROJECT_IDS), cb_projectAll));
        CommonUtils.resetCheckboxAdapter(adapter_lv_contributors, list_contributors, createCheckedListFromCheckbox(db.findUsers(""), cb_contributorAll), true);

        updateFilteredListAdapter();
    }
}