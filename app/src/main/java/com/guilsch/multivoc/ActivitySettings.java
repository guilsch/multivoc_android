package com.guilsch.multivoc;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.documentfile.provider.DocumentFile;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ActivitySettings extends AppCompatActivity {

    private static final int REQUEST_CODE_IMPORT_DATA_FILE = 123;
    private static final int SUCCESS = 10;
    private static final int COPY_FAILED = 11;
    private static final int DIR_FAILED = 12;
    private static final int NO_DATA_FILE_FOUND = 13;

    private int cptFiles = 0;

    private View importDataFileButton;
    private TextView langDirectionFreqSaveButton;
    private TextView langDirectionFreqIndicator;
    private DiscreteSeekBar langDirectionFreqSeekBar;
    private ConstraintLayout backLayout;
    private Switch automaticSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        backLayout = findViewById(R.id.back_layout);
        backLayout.setOnClickListener(v -> onBackPressed());

//       Import data file
        importDataFileButton = findViewById(R.id.import_data_file_button);
        importDataFileButton.setOnClickListener(view -> importDataFile());

//      Language direction frequency
        langDirectionFreqSaveButton = findViewById(R.id.lang_direction_save_button);
        langDirectionFreqSeekBar = findViewById(R.id.lang_direction_freq_seekBar);
        langDirectionFreqIndicator = findViewById(R.id.lang_direction_freq_indicator);

        langDirectionFreqIndicator.setText(Param.LANG_DIRECTION_FREQ + "/10");
        langDirectionFreqSeekBar.setProgress(Param.LANG_DIRECTION_FREQ);
        langDirectionFreqSaveButton.setOnClickListener(view -> langDirectionFreqSaveClick());

        // Automatic switch
        automaticSpeech = findViewById(R.id.play_speech_switch);
        automaticSpeech.setChecked(Param.AUTOMATIC_SPEECH);
        automaticSpeech.setOnClickListener(v -> automaticSpeechSwitch());

    }

    ///// Import data files
    /**
     * Launch intent to open document tree to choose folder for import
     */
    public void importDataFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, REQUEST_CODE_IMPORT_DATA_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // When importDataFile intent is launched, after document tree
        if (requestCode == REQUEST_CODE_IMPORT_DATA_FILE && resultCode == RESULT_OK && data != null) {
            DialogConfirmImportDataFile importDialog = new DialogConfirmImportDataFile(this);

            importDialog.setOnDismissListener(new DialogConfirmImportDataFile.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (importDialog.getDecision() == Param.CONFIRM_IMPORT) {
                        int ans = proceedTheImportation(requestCode, resultCode, data);
                        informImportationStatus(ans);
                    }
                }
            });

            importDialog.show();
        }


    }

    /**
     * Get folder to import files and import
     * @param requestCode
     * @param resultCode
     * @param data
     * @return
     */
    private int proceedTheImportation(int requestCode, int resultCode, Intent data) {

        // Get folder
        Uri selectedUri = data.getData();
        DocumentFile pickedDir = DocumentFile.fromTreeUri(this, selectedUri);

        // List files (optional)
        listFiles(pickedDir);

        // Import files
        int ans = copyFilesWithPrefix(pickedDir);

        return ans;
    }

    /**
     * Inform the user if importation was made correctly or not
     * @param ans
     */
    private void informImportationStatus(int ans) {
        switch (ans) {
            case SUCCESS:
                Utils.showToast(ActivitySettings.this, getString(R.string.toast_msg_data_file_imported));
                break;

            case NO_DATA_FILE_FOUND:
                Utils.showToast(ActivitySettings.this, getString(R.string.toast_msg_data_import_no_file_found));
                break;

            case COPY_FAILED:
                Utils.showToast(ActivitySettings.this, getString(R.string.toast_msg_data_import_copy_failed));
                break;

            case DIR_FAILED:
                Utils.showToast(ActivitySettings.this, getString(R.string.toast_msg_data_import_dir_failed));
                break;

            default:
                Utils.showToast(ActivitySettings.this, getString(R.string.toast_msg_data_import_error));
                break;
        }
    }

    /**
     * List files in a given directory
     * @param directory
     */
    private void listFiles(DocumentFile directory) {
        if (directory != null && directory.isDirectory()) {
            for (DocumentFile file : directory.listFiles()) {
                if (file.isFile()) {
                    System.out.println("File : " + file.getName());
                } else if (file.isDirectory()) {
                    System.out.println("Directory : " + file.getName());
                }
            }
        }
    }

    /**
     * Check if directory is correct and ask to copy the prefix-filtered files
     * @param pickedDir
     * @return
     */
    private int copyFilesWithPrefix(DocumentFile pickedDir) {
        if (pickedDir != null && pickedDir.isDirectory()) {
            int ans;
            cptFiles = 0;

            File appDirectory = new File(Param.FOLDER_PATH);

            if (!appDirectory.exists()) {
                appDirectory.mkdirs();
            }

            for (DocumentFile file : pickedDir.listFiles()) {
                if (file.isFile() && file.getName().startsWith(Param.FILE_NAME_PREFIX)) {
                    File destinationFile = new File(appDirectory, file.getName());
                    ans = copyFile(file.getUri(), destinationFile);

                    if (ans == SUCCESS) {cptFiles += 1;}
                    else {
                        return COPY_FAILED;
                    }
                }
            }

            if(cptFiles == 0) {
                return NO_DATA_FILE_FOUND;
            }

            return SUCCESS;
        }

        return DIR_FAILED;
    }

    /**
     * Copy files in the application directory. Overwrites files already in the application directory
     * @param sourceUri
     * @param destinationFile
     * @return
     */
    private int copyFile(Uri sourceUri, File destinationFile) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(sourceUri);
            OutputStream outputStream = new FileOutputStream(destinationFile);

            // Copy content of the file
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            inputStream.close();
            outputStream.close();

            System.out.println("File copied : " + destinationFile.getAbsolutePath());
            return SUCCESS;

        } catch (IOException e) {
            e.printStackTrace();
            return COPY_FAILED;
        }
    }

    ///// Lang direction

    public void langDirectionFreqSaveClick() {
        Param.LANG_DIRECTION_FREQ = langDirectionFreqSeekBar.getProgress();
        Pref.savePreference(ActivitySettings.this, Param.LANG_DIRECTION_FREQ_KEY, Param.LANG_DIRECTION_FREQ);
        langDirectionFreqIndicator.setText(Param.LANG_DIRECTION_FREQ + "/10");
    }

    ///// Automatic speech

    public void automaticSpeechSwitch() {
        Param.AUTOMATIC_SPEECH = !Param.AUTOMATIC_SPEECH;
        Pref.savePreference(ActivitySettings.this, Param.AUTOMATIC_SPEECH_KEY, Param.AUTOMATIC_SPEECH);
    }

    @Override
    public void onBackPressed() {
        Intent menuActivity = new Intent(getApplicationContext(), ActivityMenu.class);
        startActivity(menuActivity);
        finish();
    }
}