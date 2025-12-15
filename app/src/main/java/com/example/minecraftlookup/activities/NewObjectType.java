package com.example.minecraftlookup.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.minecraftlookup.util.CommonUtils;
import com.example.minecraftlookup.util.DatabaseHelper;
import com.example.minecraftlookup.R;
import com.example.minecraftlookup.util.ReturnsToHomePageActivity;

public class NewObjectType extends ReturnsToHomePageActivity {

    EditText et_objectType;
    Button btn_createObjectType;
    TextView tv_error_objectTypeExists;
    ImageButton btn_cancel, btn_home;

    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_object_type);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        et_objectType = findViewById(R.id.NOT_et_objectType);

        btn_createObjectType = findViewById(R.id.NOT_btn_createObjectType);
        btn_cancel = findViewById(R.id.NOT_btn_cancel);
        btn_home = findViewById(R.id.NOT_btn_home);

        tv_error_objectTypeExists = findViewById(R.id.NOT_tv_error_objectTypeExists);


        db = new DatabaseHelper(this);

        CommonUtils.hideErrors(tv_error_objectTypeExists);

        initButtonClickListeners();
    }

    private void initButtonClickListeners() {
        btn_createObjectType.setOnClickListener(v -> createObjectType());
        btn_cancel.setOnClickListener(v -> finish());
        btn_home.setOnClickListener(v -> home());
    }

    private void createObjectType() {
        CommonUtils.hideErrors(tv_error_objectTypeExists);

        String objectType = et_objectType.getText().toString();

        if (!db.objectTypeExists(objectType)) {
            db.addObjectTypeToDB(objectType);
            New.close();
            finish();
        } else
            CommonUtils.showError(tv_error_objectTypeExists);
    }
}