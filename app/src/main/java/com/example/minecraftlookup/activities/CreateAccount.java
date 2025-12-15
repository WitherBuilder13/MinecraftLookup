package com.example.minecraftlookup.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.minecraftlookup.util.CommonUtils;
import com.example.minecraftlookup.util.DatabaseHelper;
import com.example.minecraftlookup.R;

public class CreateAccount extends AppCompatActivity {

    EditText et_username;
    Button btn_createAccount;
    TextView tv_error_usernameNotAvailable;
    ImageButton btn_cancel;

    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        et_username = findViewById(R.id.CA_et_username);

        btn_createAccount = findViewById(R.id.CA_btn_createAccount);
        btn_cancel = findViewById(R.id.CA_btn_cancel);

        tv_error_usernameNotAvailable = findViewById(R.id.CA_tv_error_usernameNotAvailable);


        db = new DatabaseHelper(this);

        initButtonClickListeners();
    }

    private void initButtonClickListeners() {
        btn_createAccount.setOnClickListener(v -> {
            CommonUtils.hideErrors(tv_error_usernameNotAvailable);

            String username = et_username.getText().toString();

            if (!db.userExists(username)) {
                db.addUserToDB(username);
                finish();
            } else
                CommonUtils.showError(tv_error_usernameNotAvailable);
        });
        btn_cancel.setOnClickListener(v -> finish());
    }
}