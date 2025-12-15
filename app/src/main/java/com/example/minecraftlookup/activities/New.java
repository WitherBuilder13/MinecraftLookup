package com.example.minecraftlookup.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.minecraftlookup.R;
import com.example.minecraftlookup.util.ReturnsToHomePageActivity;

public class New extends ReturnsToHomePageActivity {

    private static boolean close;

    Button btn_object, btn_objectType, btn_projectID, btn_sourceUsageType;
    ImageButton btn_cancel, btn_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btn_object = findViewById(R.id.N_btn_object);
        btn_objectType = findViewById(R.id.N_btn_objectType);
        btn_projectID = findViewById(R.id.N_btn_projectID);
        btn_sourceUsageType = findViewById(R.id.N_btn_sourceUsageType);
        btn_cancel = findViewById(R.id.N_btn_cancel);
        btn_home = findViewById(R.id.N_btn_home);

        close = false;

        initButtonClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (close)
            finish();
    }

    private void initButtonClickListeners() {
        btn_object.setOnClickListener(v -> startActivity(new Intent(New.this, NewObject.class)));
        btn_objectType.setOnClickListener(v -> startActivity(new Intent(New.this, NewObjectType.class)));
        btn_projectID.setOnClickListener(v -> startActivity(new Intent(New.this, NewProjectID.class)));
        btn_sourceUsageType.setOnClickListener(v -> startActivity(new Intent(New.this, NewSourceUsageType.class)));
        btn_cancel.setOnClickListener(v -> finish());
        btn_home.setOnClickListener(v -> home());
    }

    public static void close() {
        close = true;
    }
}