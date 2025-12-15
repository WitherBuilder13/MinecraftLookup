package com.example.minecraftlookup.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.minecraftlookup.util.CommonUtils;
import com.example.minecraftlookup.util.DatabaseHelper;
import com.example.minecraftlookup.R;
import com.example.minecraftlookup.util.ReturnsToHomePageActivity;

import java.util.ArrayList;

public class UserSearch extends ReturnsToHomePageActivity {

    EditText et_search;
    ListView lv_users;
    ImageButton btn_back, btn_home;

    DatabaseHelper db;
    ArrayAdapter<String> adapter_lv_users;
    ArrayList<String> list_users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        et_search = findViewById(R.id.US_et_search);

        btn_back = findViewById(R.id.US_btn_back);
        btn_home = findViewById(R.id.US_btn_home);

        lv_users = findViewById(R.id.US_lv_users);


        db = new DatabaseHelper(this);

        list_users = new ArrayList<>();

        adapter_lv_users = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list_users);

        lv_users.setAdapter(adapter_lv_users);

        initButtonClickListener();
        initListViewClickListener();
        initTextChangedListener();
    }

    @Override
    protected void onResume() {
        super.onResume();

        CommonUtils.clearEditTexts(et_search);
        updateAdapter();
    }

    private void initButtonClickListener() {
        btn_back.setOnClickListener(v -> finish());
        btn_home.setOnClickListener(v -> home());
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
                updateAdapter();
            }
        });
    }

    private void initListViewClickListener() {
        lv_users.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(UserSearch.this, ContributionSearch.class);
            intent.putExtra("userID", db.findUserID(list_users.get(position)));
            startActivity(intent);
        });
    }

    private void updateAdapter() {
        list_users.clear();
        list_users.addAll(db.findUsers(et_search.getText().toString()));
        list_users.remove(CommonUtils.DUMMY_USER);
        adapter_lv_users.notifyDataSetChanged();
    }
}