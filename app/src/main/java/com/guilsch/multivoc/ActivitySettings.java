package com.guilsch.multivoc;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.documentfile.provider.DocumentFile;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

public class ActivitySettings extends AppCompatActivity {

    private static final int REQUEST_CODE_IMPORT_DATA_FILE = 123;

    private static final int COPY_SUCCESS = 10;
    private static final int COPY_FAILED = 11;

    private static final int IMPORT_SUCCESS = 20;
    private static final int IMPORT_NO_DATA_FILE_FOUND = 21;
    private static final int IMPORT_COPY_FAILED = 22;
    private static final int IMPORT_DIR_FAILED = 23;

    private static final int EXPORT_SUCCESS = 30;
    private static final int EXPORT_NO_DATA_FILE_FOUND = 31;
    private static final int EXPORT_COPY_FAILED = 32;
    private static final int EXPORT_DIR_FAILED = 33;

    private static final int DELETE_SUCCESS = 40;
    private static final int DELETE_FAILED = 41;
    private static final int DELETE_NO_DATA_FILE_FOUND = 42;

    private int cptFiles = 0;
    private int fileStatus;

    private View importDataFileButton;
    private View exportDataFileButton;
    private View resetDataFileButton;
    private TextView langDirectionFreqSaveButton;
    private TextView langDirectionFreqIndicator;
    private DiscreteSeekBar langDirectionFreqSeekBar;
    private ConstraintLayout backLayout;
    private Switch automaticSpeech;
    private ProgressBar importBar;
    private View blockingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        backLayout = findViewById(R.id.back_layout);
        backLayout.setOnClickListener(v -> onBackPressed());

//       Import data file
        importDataFileButton = findViewById(R.id.import_data_file_button);
        importDataFileButton.setOnClickListener(view -> launchImportTreeSelector());

//       Export data file
        exportDataFileButton = findViewById(R.id.export_data_file_button);
        exportDataFileButton.setOnClickListener(view -> export());

//       Reset data file
        resetDataFileButton = findViewById(R.id.reset_data_file_button);
        resetDataFileButton.setOnClickListener(view -> reset());

//        Progress bar
        importBar = findViewById(R.id.progressBar);
        importBar.setVisibility(View.INVISIBLE);
        blockingView = findViewById(R.id.blockingView);
        blockingView.setVisibility(View.INVISIBLE);

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

        // Variables
        fileStatus = COPY_SUCCESS;

    }

    /**
     * Inform the user if file manipulation was made correctly or not
     */
    private void informFileStatus() {
        switch (fileStatus) {
            case IMPORT_SUCCESS:
                Utils.showToast(ActivitySettings.this, getString(R.string.toast_msg_data_file_imported));
                break;

            case IMPORT_NO_DATA_FILE_FOUND:
                Utils.showToast(ActivitySettings.this, getString(R.string.toast_msg_data_import_no_file_found));
                break;

            case IMPORT_COPY_FAILED:
                Utils.showToast(ActivitySettings.this, getString(R.string.toast_msg_data_import_copy_failed));
                break;

            case IMPORT_DIR_FAILED:
                Utils.showToast(ActivitySettings.this, getString(R.string.toast_msg_data_import_dir_failed));
                break;

            case EXPORT_SUCCESS:
                Utils.showToast(ActivitySettings.this, getString(R.string.toast_msg_data_file_exported));
                break;

            case EXPORT_NO_DATA_FILE_FOUND:
                Utils.showToast(ActivitySettings.this, getString(R.string.toast_msg_data_export_no_file_found));
                break;

            case EXPORT_COPY_FAILED:
                Utils.showToast(ActivitySettings.this, getString(R.string.toast_msg_data_export_copy_failed));
                break;

            case EXPORT_DIR_FAILED:
                Utils.showToast(ActivitySettings.this, getString(R.string.toast_msg_data_export_dir_failed));
                break;

            case DELETE_SUCCESS:
                Utils.showToast(ActivitySettings.this, getString(R.string.toast_msg_data_delete_success));
                break;

            case DELETE_FAILED:
                Utils.showToast(ActivitySettings.this, getString(R.string.toast_msg_data_delete_failed));
                break;

            case DELETE_NO_DATA_FILE_FOUND:
                Utils.showToast(ActivitySettings.this, getString(R.string.toast_msg_data_delete_no_file_found));
                break;

            default:
                Utils.showToast(ActivitySettings.this, getString(R.string.toast_msg_data_error));
                break;
        }
    }

    /**
     * Reload data in the file and update global deck
     */
    private class ReloadDataTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            // This method is called on the UI thread before the background task starts
            importBar.setVisibility(View.VISIBLE);
            blockingView.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // This method is called on a background thread, so it won't block the UI thread
            System.out.println("Loading the new data");

            Utils.prepareDataFile();
            Utils.saveTempDataFile();
            Utils.initGlobalDeck();

            System.out.println("Loading the new data end");

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            importBar.setVisibility(View.INVISIBLE);
            blockingView.setVisibility(View.INVISIBLE);

            informFileStatus();
        }
    }

    ///// Import data files
    /**
     * Launch intent to open document tree to choose folder for import
     */
    public void launchImportTreeSelector() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, REQUEST_CODE_IMPORT_DATA_FILE);
    }

    /**
     * Manage import intent answer
     * @param requestCode
     * @param resultCode
     * @param data
     */
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
                        fileStatus = proceedTheImportation(requestCode, resultCode, data);
                        if (fileStatus == IMPORT_SUCCESS){
                            new ReloadDataTask().execute();
                        }
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
        int ans = importAllFiles(pickedDir);

        return ans;
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
    private int importAllFiles(DocumentFile pickedDir) {
        if (pickedDir != null && pickedDir.isDirectory()) {
            int ansImport;
            cptFiles = 0;

            File appDirectory = new File(Param.FOLDER_PATH);

            if (!appDirectory.exists()) {
                appDirectory.mkdirs();
            }

            for (DocumentFile file : pickedDir.listFiles()) {
                if (file.isFile() && file.getName().startsWith(Param.FILE_NAME_PREFIX)) {
                    File destinationFile = new File(appDirectory, file.getName());
                    ansImport = copyIndividualFileWithUri(file.getUri(), destinationFile);

                    if (ansImport == COPY_SUCCESS) {cptFiles += 1;}
                    else {
                        return IMPORT_COPY_FAILED;
                    }
                }
            }

            if(cptFiles == 0) {
                return IMPORT_NO_DATA_FILE_FOUND;
            }

            return IMPORT_SUCCESS;
        }

        return IMPORT_DIR_FAILED;
    }

    /**
     * Copy files in the application directory. Overwrites files already in the application directory
     * @param sourceUri
     * @param destinationFile
     * @return
     */
    private int copyIndividualFileWithUri(Uri sourceUri, File destinationFile) {
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
            return COPY_SUCCESS;

        } catch (IOException e) {
            e.printStackTrace();
            return COPY_FAILED;
        }
    }


    ///// Reset data file

    /**
     * Launch a dialog to confirm. After confirmation, delete and load new empty file (new file is
     * created in reload data task
     */
    private void reset() {

        DialogDeleteDataFile deleteDialog = new DialogDeleteDataFile(this);

        deleteDialog.setOnDismissListener(new DialogDeleteDataFile.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (deleteDialog.getDecision() == Param.CONFIRM_DELETE_DATA_FILE) {

                    fileStatus = deleteDataFile(Param.DATA_PATH);

                    if (fileStatus == DELETE_SUCCESS){
                        new ReloadDataTask().execute();
                    }
                    else {
                        informFileStatus();
                    }
                }
            }
        });

        deleteDialog.show();
    }

    /**
     * Delete one data file
     * @param path
     * @return
     */
    private int deleteDataFile(String path) {
        File fileToDelete = new File(path);

        if (fileToDelete.exists()) {
            boolean deleted = fileToDelete.delete();

            if (deleted) {
                System.out.println("File was successfully deleted");
                return DELETE_SUCCESS;
            } else {
                System.out.println("Deletion failed");
                return DELETE_FAILED;
            }
        }

        System.out.println("File not found");
        return DELETE_NO_DATA_FILE_FOUND;
    }


    ///// Export data files
    private void export() {
        fileStatus = exportAllFiles();
        informFileStatus();
    }

    public static int exportAllFiles() {

        String destinationDirectoryPath = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .getAbsolutePath();

        File sourceDirectory = new File(Param.FOLDER_PATH);
        File destinationDirectory = new File(destinationDirectoryPath);

        if (!sourceDirectory.exists() || !sourceDirectory.isDirectory()) {
            System.out.println("Source directory doesn't exist or is not valid");
            return EXPORT_DIR_FAILED;
        }

        if (!destinationDirectory.exists()) {
            destinationDirectory.mkdirs();
        }

        // List files
        File[] files = sourceDirectory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().startsWith(Param.FILE_NAME_PREFIX)) {
                    int ans = copyIndividualFileNoOverwrite(file, destinationDirectory);

                    if (ans == COPY_FAILED){
                        return COPY_FAILED;
                    }
                }
            }
        }
        else {
            return EXPORT_NO_DATA_FILE_FOUND;
        }

        return EXPORT_SUCCESS;
    }

    /**
     * Copy application files to download folder without overwriting preexistant ones
     * @param sourceFile
     * @param destinationDirectory
     * @return
     */
    private static int copyIndividualFileNoOverwrite(File sourceFile, File destinationDirectory) {
        String fileName = sourceFile.getName();
        File destinationFile = new File(destinationDirectory, fileName);

        int count = 1;
        while (destinationFile.exists()) {
            String baseName = fileName.substring(0, fileName.lastIndexOf('.'));
            String extension = fileName.substring(fileName.lastIndexOf('.'));
            String newFileName = baseName + "_" + count + extension;
            destinationFile = new File(destinationDirectory, newFileName);
            count++;
        }

        try (FileInputStream inputStream = new FileInputStream(sourceFile);
             FileOutputStream outputStream = new FileOutputStream(destinationFile);
             FileChannel sourceChannel = inputStream.getChannel();
             FileChannel destinationChannel = outputStream.getChannel()) {

            sourceChannel.transferTo(0, sourceChannel.size(), destinationChannel);

            System.out.println("Copy of file : " + sourceFile.getAbsolutePath() + " towards " + destinationFile.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error during the copy : " + e.getMessage());
            return COPY_FAILED;
        }
        return COPY_SUCCESS;
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