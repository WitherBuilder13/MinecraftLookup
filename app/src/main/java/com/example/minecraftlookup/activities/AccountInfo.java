package com.example.minecraftlookup.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.minecraftlookup.util.DatabaseHelper;
import com.example.minecraftlookup.R;
import com.example.minecraftlookup.util.SessionData;

public class AccountInfo extends AppCompatActivity {

    EditText et_username;
    Button btn_updateInfo, btn_logOut, btn_deleteAccount;
    TextView tv_error_usernameNotAvailable;
    ImageButton btn_cancel;

    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        et_username = findViewById(R.id.AI_et_username);

        btn_updateInfo = findViewById(R.id.AI_btn_updateInfo);
        btn_logOut = findViewById(R.id.AI_btn_logOut);
        btn_cancel = findViewById(R.id.AI_btn_cancel);
        btn_deleteAccount = findViewById(R.id.AI_btn_deleteAccount);

        tv_error_usernameNotAvailable = findViewById(R.id.AI_tv_error_usernameNotAvailable);


        db = new DatabaseHelper(this);

        initButtonClickListeners();

        fillInInfo();
    }

    private void initButtonClickListeners() {
        btn_updateInfo.setOnClickListener(v -> {
            tv_error_usernameNotAvailable.setVisibility(View.INVISIBLE);

            String username = et_username.getText().toString();

            if (!db.userExists(username) /* username is not taken */ || db.findUser(SessionData.getLoggedInUser()).equals(username) /* username matches the current user */) {
                db.updateUserInDB(SessionData.getLoggedInUser(), username);
                finish();
            } else
                tv_error_usernameNotAvailable.setVisibility(View.VISIBLE);
        });
        btn_logOut.setOnClickListener(v -> {
            SessionData.setLoggedInUser(-1);
            finish();
        });
        btn_cancel.setOnClickListener(v -> finish());
        btn_deleteAccount.setOnClickListener(v -> {
            db.deleteUserFromDB(SessionData.getLoggedInUser());
            SessionData.setLoggedInUser(-1);
            finish();
        });
    }

    private void fillInInfo() {
        et_username.setText(db.findUser(SessionData.getLoggedInUser()));
    }
}