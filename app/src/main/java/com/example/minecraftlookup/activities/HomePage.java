package com.example.minecraftlookup.activities;

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
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.minecraftlookup.adapters.CheckboxAdapter;
import com.example.minecraftlookup.objects.Toggleable;
import com.example.minecraftlookup.util.HasFilteredList;
import com.example.minecraftlookup.util.CommonUtils;
import com.example.minecraftlookup.util.DatabaseHelper;
import com.example.minecraftlookup.adapters.ObjectAdapter;
import com.example.minecraftlookup.R;
import com.example.minecraftlookup.util.SessionData;
import com.example.minecraftlookup.objects.MCObject;

import java.util.ArrayList;

public class HomePage extends AppCompatActivity implements HasFilteredList {

    TextView tv_hello;
    EditText et_search;
    ListView lv_types, lv_projects, lv_contributors, lv_objects;
    CheckBox cb_typeAll, cb_projectAll, cb_contributorAll;
    Button btn_users, btn_new, btn_resetSearch;
    ImageButton btn_account;

    DatabaseHelper db;
    CheckboxAdapter adapter_lv_types, adapter_lv_projects, adapter_lv_contributors;
    ObjectAdapter adapter_lv_objects;
    ArrayList<MCObject> list_objects;
    ArrayList<Toggleable<String>> list_types, list_projects, list_contributors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tv_hello = findViewById(R.id.HP_tv_hello);

        et_search = findViewById(R.id.HP_et_search);

        lv_types = findViewById(R.id.HP_lv_types);
        lv_projects = findViewById(R.id.HP_lv_projects);
        lv_contributors = findViewById(R.id.HP_lv_contributors);
        lv_objects = findViewById(R.id.HP_lv_objects);

        cb_typeAll = findViewById(R.id.HP_cb_typeAll);
        cb_projectAll = findViewById(R.id.HP_cb_projectAll);
        cb_contributorAll = findViewById(R.id.HP_cb_contributorAll);

        btn_account = findViewById(R.id.HP_btn_account);
        btn_users = findViewById(R.id.HP_btn_users);
        btn_new = findViewById(R.id.HP_btn_new);
        btn_resetSearch = findViewById(R.id.HP_btn_resetSearch);


        db = new DatabaseHelper(this);

        list_objects = new ArrayList<>();
        list_types = new ArrayList<>();
        list_projects = new ArrayList<>();
        list_contributors = new ArrayList<>();

        adapter_lv_objects = new ObjectAdapter(this, list_objects);
        adapter_lv_types = new CheckboxAdapter(this, list_types, this);
        adapter_lv_projects = new CheckboxAdapter(this, list_projects, this);
        adapter_lv_contributors = new CheckboxAdapter(this, list_contributors, this);

        lv_objects.setAdapter(adapter_lv_objects);
        lv_types.setAdapter(adapter_lv_types);
        lv_projects.setAdapter(adapter_lv_projects);
        lv_contributors.setAdapter(adapter_lv_contributors);

        initButtonClickListeners();
        initListViewCLickListener();
        initCheckboxListeners();
        initTextChangedListener();

    }

    @Override
    protected void onResume() {
        super.onResume();

        SessionData.HOME = false;

        if (SessionData.getLoggedInUser() == -1)
            finish();

        setGreeting();
        resetSearch();
    }

    private void initButtonClickListeners() {
        btn_account.setOnClickListener(v -> startActivity(new Intent(HomePage.this, AccountInfo.class)));
        btn_users.setOnClickListener(v -> startActivity(new Intent(HomePage.this, UserSearch.class)));
        btn_new.setOnClickListener(v -> startActivity(new Intent(HomePage.this, New.class)));
        btn_resetSearch.setOnClickListener(v -> resetSearch());
    }

    private void initListViewCLickListener() {
        lv_objects.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(HomePage.this, ViewObject.class);
            intent.putExtra("objectID", list_objects.get(position).getId());
            startActivity(intent);
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
        list_objects.clear();
        list_objects.addAll(db.findObjects(
                getAllToggleEntries(list_types, true), getAllToggleEntries(list_projects, true), getAllToggleEntries(list_contributors, true), et_search.getText().toString()
        ));
        adapter_lv_objects.notifyDataSetChanged();
    }

    private void resetSearch() {
        CommonUtils.clearEditTexts(et_search);
        CommonUtils.resetCheckboxes(cb_typeAll, cb_projectAll, cb_contributorAll);

        CommonUtils.resetCheckboxAdapter(adapter_lv_types, list_types, createCheckedListFromCheckbox(db.getAllStringIDs(DatabaseHelper.OBJECT_TYPES), cb_typeAll));
        CommonUtils.resetCheckboxAdapter(adapter_lv_projects, list_projects, createCheckedListFromCheckbox(db.getAllStringIDs(DatabaseHelper.PROJECT_IDS), cb_projectAll));
        CommonUtils.resetCheckboxAdapter(adapter_lv_contributors, list_contributors, createCheckedListFromCheckbox(db.findUsers(""), cb_contributorAll), true);

        list_objects.clear();
        list_objects.addAll(db.getRecentObjects(SessionData.getLoggedInUser()));
        list_objects.addAll(addRemaining());
        adapter_lv_objects.notifyDataSetChanged();
    }

    private ArrayList<MCObject> addRemaining() {
        ArrayList<MCObject> currentList = new ArrayList<>(list_objects);
        ArrayList<Integer> currentIDs = new ArrayList<>();

        for (MCObject object : currentList)
            currentIDs.add(object.getId());

        ArrayList<MCObject> allObjects = db.getAllObjects();
        ArrayList<MCObject> remaining = new ArrayList<>();

        for (MCObject object : allObjects)
            if (!currentIDs.contains(object.getId()))
                remaining.add(object);

        return remaining;
    }

    private void setGreeting() {
        tv_hello.setText("Hello, " + db.findUser(SessionData.getLoggedInUser()));
    }
}