package com.example.minecraftlookup.activities;

import static com.example.minecraftlookup.objects.Contribution.ContributionTypes.*;

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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.minecraftlookup.adapters.ContributionAdapter;
import com.example.minecraftlookup.objects.Contribution;
import com.example.minecraftlookup.util.CommonUtils;
import com.example.minecraftlookup.util.DatabaseHelper;
import com.example.minecraftlookup.R;
import com.example.minecraftlookup.util.ReturnsToHomePageActivity;
import com.example.minecraftlookup.util.SessionData;

import java.util.ArrayList;

public class ContributionSearch extends ReturnsToHomePageActivity {

    TextView tv_contributions;
    EditText et_search;
    CheckBox cb_objects, cb_sources, cb_usages, cb_sourceUsageTypes;
    ListView lv_contributions;
    Button btn_resetSearch;
    ImageButton btn_back, btn_home;

    DatabaseHelper db;
    ArrayList<Contribution> list_contributions;
    ContributionAdapter adapter_lv_contributions;
    ArrayList<Contribution.ContributionTypes> list_enabledContributionTypes;

    int userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contribution_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tv_contributions = findViewById(R.id.CS_tv_contributions);

        et_search = findViewById(R.id.CS_et_search);

        cb_objects = findViewById(R.id.CS_cb_objects);
        cb_sources = findViewById(R.id.CS_cb_sources);
        cb_usages = findViewById(R.id.CS_cb_usages);
        cb_sourceUsageTypes = findViewById(R.id.CS_cb_sourceUsageTypes);

        lv_contributions = findViewById(R.id.CS_lv_contributions);

        btn_resetSearch = findViewById(R.id.CS_btn_resetSearch);
        btn_back = findViewById(R.id.CS_btn_back);
        btn_home = findViewById(R.id.CS_btn_home);


        db = new DatabaseHelper(this);

        list_contributions = new ArrayList<>();

        adapter_lv_contributions = new ContributionAdapter(this, list_contributions);

        lv_contributions.setAdapter(adapter_lv_contributions);

        list_enabledContributionTypes = new ArrayList<>();

        userID = getIntent().getIntExtra("userID", -1);

        initButtonClickListeners();
        initCheckboxListeners();
        initTextChangedListener();
        initListViewClickListener();
    }

    @Override
    protected void onResume() {
        super.onResume();

        tv_contributions.setText(db.findUser(userID) + "'s Contrubtions");
        resetSearch();
    }

    private void initButtonClickListeners() {
        btn_resetSearch.setOnClickListener(v -> resetSearch());
        btn_back.setOnClickListener(v -> finish());
        btn_home.setOnClickListener(v -> home());
    }

    private void initCheckboxListeners() {
        cb_objects.setOnCheckedChangeListener((buttonView, isChecked) -> updateEnabledContributionTypes(isChecked, OBJECT));
        cb_sources.setOnCheckedChangeListener((buttonView, isChecked) -> updateEnabledContributionTypes(isChecked, SOURCE));
        cb_usages.setOnCheckedChangeListener((buttonView, isChecked) -> updateEnabledContributionTypes(isChecked, USAGE));
        cb_sourceUsageTypes.setOnCheckedChangeListener((buttonView, isChecked) -> updateEnabledContributionTypes(isChecked, SOURCE_USAGE_TYPE));
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
                updateContributionsList();
            }
        });
    }

    private void initListViewClickListener() {
        lv_contributions.setOnItemClickListener((parent, view, position, id) -> {
            Contribution contribution = list_contributions.get(position);

            switch (contribution.getType()) {
                case OBJECT: {
                    Intent intent = new Intent(ContributionSearch.this, ViewObject.class);
                    intent.putExtra("objectID", contribution.getId());
                    startActivity(intent);
                    break;
                } case SOURCE: {
                    Intent intent = new Intent(ContributionSearch.this, ViewSourceUsage.class);
                    intent.putExtra("sourceUsageID", contribution.getId());
                    intent.putExtra("isSource", true);
                    startActivity(intent);
                    break;
                } case USAGE: {
                    Intent intent = new Intent(ContributionSearch.this, ViewSourceUsage.class);
                    intent.putExtra("sourceUsageID", contribution.getId());
                    intent.putExtra("isSource", false);
                    startActivity(intent);
                    break;
                }
            }
        });
    }

    private void updateEnabledContributionTypes(boolean isChecked, Contribution.ContributionTypes type) {
        if (isChecked) {
            if (!list_enabledContributionTypes.contains(type))
                list_enabledContributionTypes.add(type);
        } else
            list_enabledContributionTypes.remove(type);

        updateContributionsList();
    }

    private void updateContributionsList() {
        list_contributions.clear();
        list_contributions.addAll(db.findContributions(userID, list_enabledContributionTypes, et_search.getText().toString()));
        adapter_lv_contributions.notifyDataSetChanged();
    }

    private void resetSearch() {
        CommonUtils.clearEditTexts(et_search);
        CommonUtils.resetCheckboxes(cb_objects, cb_sources, cb_usages, cb_sourceUsageTypes);

        updateContributionsList();
    }
}