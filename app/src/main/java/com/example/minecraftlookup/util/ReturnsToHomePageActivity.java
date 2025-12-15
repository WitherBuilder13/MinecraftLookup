package com.example.minecraftlookup.util;

import androidx.appcompat.app.AppCompatActivity;

public class ReturnsToHomePageActivity extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();

        if (SessionData.HOME)
            finish();
    }

    protected void home() {
        SessionData.HOME = true;
        finish();
    }
}
