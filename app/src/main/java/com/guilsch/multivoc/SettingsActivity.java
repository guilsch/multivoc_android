package com.guilsch.multivoc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SettingsActivity extends AppCompatActivity {

    private Button folderPathButton;
    private Button folderPathDefaultButton;
    private EditText folderPathText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        folderPathButton = (Button) findViewById(R.id.folder_path_button);
        folderPathDefaultButton = (Button) findViewById(R.id.folder_path_default);
        folderPathText = (EditText) findViewById(R.id.folder_path_text);

        folderPathText.setText(Param.DATA_PATH);

        folderPathButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Param.FOLDER_PATH = folderPathText.getText().toString();
            }
        });

        folderPathDefaultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Param.FOLDER_PATH = Param.FOLDER_PATH_DEFAULT;
                folderPathText.setText(Param.DATA_PATH);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent menuActivity = new Intent(getApplicationContext(), MenuActivity.class);
        startActivity(menuActivity);
        finish();
    }
}