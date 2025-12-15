package com.example.minecraftlookup.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.minecraftlookup.util.CommonUtils;
import com.example.minecraftlookup.util.DatabaseHelper;
import com.example.minecraftlookup.R;
import com.example.minecraftlookup.util.SessionData;

public class MainActivity extends AppCompatActivity {

    EditText et_username;
    Button btn_login, btn_createAccount;
    TextView tv_error_accountNotFound, tv_error_emptyField;

    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        et_username = findViewById(R.id.MA_et_username);

        btn_login = findViewById(R.id.MA_btn_login);
        btn_createAccount = findViewById(R.id.MA_btn_createAccount);

        tv_error_accountNotFound = findViewById(R.id.MA_tv_error_accountNotFound);
        tv_error_emptyField = findViewById(R.id.MA_tv_error_emptyField);


        db = new DatabaseHelper(this);
        db.addBuiltinValues();

        initButtonCLickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();

        CommonUtils.hideErrors(tv_error_emptyField, tv_error_accountNotFound);
    }

    private void initButtonCLickListeners() {
        btn_login.setOnClickListener(v -> {
            CommonUtils.hideErrors(tv_error_accountNotFound, tv_error_emptyField);

            String username = et_username.getText().toString();

            if (db.userExists(username) && !username.isEmpty()) {
                SessionData.setLoggedInUser(db.findUserID(username));
                startActivity(new Intent(MainActivity.this, HomePage.class));
            } else if (username.isEmpty())
                CommonUtils.showError(tv_error_emptyField);
            else
                CommonUtils.showError(tv_error_accountNotFound);

            CommonUtils.clearEditTexts(et_username);
        });
        btn_createAccount.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CreateAccount.class)));
    }
}