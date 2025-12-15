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

public class NewProjectID extends ReturnsToHomePageActivity {

    EditText et_projectID;
    Button btn_createProjectID;
    TextView tv_projectIDExists;
    ImageButton btn_cancel, btn_home;

    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_project_id);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        et_projectID = findViewById(R.id.NPI_et_projectID);

        btn_createProjectID = findViewById(R.id.NPI_btn_createProjectID);
        btn_cancel = findViewById(R.id.NPI_btn_cancel);
        btn_home = findViewById(R.id.NPI_btn_home);

        tv_projectIDExists = findViewById(R.id.NPI_tv_error_projectIDExists);


        db = new DatabaseHelper(this);

        CommonUtils.hideErrors(tv_projectIDExists);

        initButtonClickListeners();
    }


    private void initButtonClickListeners() {
        btn_createProjectID.setOnClickListener(v -> createProjectID());
        btn_cancel.setOnClickListener(v -> finish());
        btn_home.setOnClickListener(v -> home());
    }

    private void createProjectID() {
        CommonUtils.hideErrors(tv_projectIDExists);

        String projectID = et_projectID.getText().toString();

        if (!db.projectIDExists(projectID)) {
            db.addProjectIDToDB(projectID);
            New.close();
            finish();
        } else
            CommonUtils.showError(tv_projectIDExists);
    }
}