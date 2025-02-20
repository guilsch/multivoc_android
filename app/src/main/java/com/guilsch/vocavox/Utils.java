package com.guilsch.vocavox;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

public class Utils {

    //////////////////////
    /// Language utils ///
    //////////////////////

    public static void setUserLanguageParam(){
        Locale userLocale = Locale.getDefault();
        String localeLanguageISO = userLocale.getLanguage();

        if (Arrays.asList(Param.USER_LANGUAGES_ISO).contains(localeLanguageISO)) {
            Param.USER_LANGUAGE_ISO = localeLanguageISO;
            Param.USER_LANGUAGE = Utils.getLanguageNameFromISO(Param.USER_LANGUAGE_ISO);
        }
        else {
            Param.USER_LANGUAGE_ISO = Param.USER_LANGUAGE_ISO_DEFAULT;
            Param.USER_LANGUAGE = Param.USER_LANGUAGE_DEFAULT;
        }
    }

    public static String getLanguageISOName(String language) {
        String languageStringName;

        switch (language) {
            case "English":
                languageStringName = "en";
                break;

            case "Deutsch":
                languageStringName = "de";
                break;

            case "Français":
                languageStringName = "fr";
                break;

            case "Italiano":
                languageStringName = "it";
                break;

            case "Русский":
                languageStringName = "ru";
                break;

            case "Español":
                languageStringName = "es";
                break;

            default:
                languageStringName = "unknown";
                break;
        }

        return languageStringName;
    }

    public static String getLanguageNameFromISO(String languageISO) {
        String languageStringName;

        switch (languageISO) {
            case "en":
                languageStringName = "English";
                break;

            case "de":
                languageStringName = "Deutsch";
                break;

            case "fr":
                languageStringName = "Français";
                break;

            case "it":
                languageStringName = "Italiano";
                break;

            case "ru":
                languageStringName = "Русский";
                break;

            case "es":
                languageStringName = "Español";
                break;

            default:
                languageStringName = "unknown";
                break;
        }

        return languageStringName;
    }

    //////////////////////
    ///// Card utils /////
    //////////////////////

    public static int nextStateForButton (int currentState) {
        int nextState;

        switch (currentState) {

            case Param.ACTIVE:
                nextState = Param.IN_PAUSE;
                break;

            case Param.IN_PAUSE:
                nextState = Param.ACTIVE;
                break;

            default:
                nextState = Param.INACTIVE;
                break;

        }

        return nextState;
    }

    /***
     * When a new card is created in translation or newCard activities, this method add it to the
     * deck and makes the necessary updates
     * @param newCard
     */
    public static void manageCardCreation(Card newCard) {
        Param.GLOBAL_DECK.add(newCard);
        newCard.addToDatabaseOnSeparateThread();
        newCard.info();


        // Update deck data
        Param.CARDS_NB = Param.CARDS_NB + 1;
        if(newCard.getState() == Param.INACTIVE) {
            Param.CARDS_TO_LEARN_NB = Param.CARDS_TO_LEARN_NB + 1;
        }
    }

    public static String getNewUUID() {
        return UUID.randomUUID().toString();
    }


    // Thread mgmt
    /**
     * Update the card in the database on a seperate thread. Don't use it repeatedly (not in train
     * or learn session)
     * @param card
     */
    public static void updateInDatabaseOnSeparateThreadOneShot(Card card) {
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                card.updateInDatabase();
            }
        };

        Thread thread = new Thread(myRunnable);
        thread.start();
    }

    /**
     * Same as previous but use one thread for different updates. Use this method in train or learn
     * activities. Don't forget to close the singleThreadExecutor (use thread shutdown) after that.
     * @param singleThreadExecutor
     * @param card
     */
    public static void updateInDatabaseOnSeparateThreadMultiShot(ExecutorService singleThreadExecutor, Card card) {
        singleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                card.updateInDatabase();
            }
        });
    }

    public static void threadShutdown(ExecutorService singleThreadExecutor) {
        singleThreadExecutor.shutdown();
    }

    //////////////////////
    ///// Date utils /////
    //////////////////////

    public static Date toDate(long nextPracticeTime) {
        Date nextPracticeDate = new Date(nextPracticeTime);
        return (nextPracticeDate);
    }

    public static Date giveCurrentDate() {
        long now = System.currentTimeMillis();
        Date currentDate = toDate(now);
        return (currentDate);

        // A voir avec LocalDate.now() (java.time.LocalDate)
    }

    public static String universalToLocalDate(String dateString, String languageISO) {

        String formattedDate = dateString;

        try {
            SimpleDateFormat originalFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
            SimpleDateFormat localFormat = new SimpleDateFormat("d MMMM yyyy", new Locale(languageISO));
            formattedDate = localFormat.format(originalFormat.parse(dateString));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return formattedDate;
    }

    //////////////////////
    ///// Init utils /////
    //////////////////////

    public static void initParam() {

        // Set user language from locale
        Utils.setUserLanguageParam();

        // Set target languages variables
        Param.TARGET_LANGUAGE_ISO = Utils.getLanguageISOName(Param.TARGET_LANGUAGE);

        // Set Data path
        Param.DATA_FILE = Utils.generateDataFileName();
        Param.setDataPath();

        // Set File ID
        Utils.setFileID();
    }

    /**
     * Initializes the global deck that will be used everywhere in the application
     */
    public static void initGlobalDeck() {
        // Set global deck
        Param.GLOBAL_DECK = new Deck();
        Param.GLOBAL_DECK.init();

        // Get deck data (nb of cards to review...)
        Param.GLOBAL_DECK.updateDeckDataVariables();
    }

    public static void checkFileExistence() {
        if (!(new File(Param.DATA_PATH)).exists()) {
            System.out.println(Param.DATA_PATH + " doesn't exist yet");
            createDataFile();
        }
        else {
            System.out.println(Param.DATA_PATH + " already exists");
        }
    }

    public static Boolean checkCellEmptiness(Cell cell, Row row) {
        if (cell == null) {
            return Boolean.TRUE;
        }
        else if (cell.getCellType() == Cell.CELL_TYPE_BLANK) {
            row.removeCell(cell);
            return Boolean.TRUE;
        }
        else {
            return Boolean.FALSE;
        }
    }

    public static String formatPath(String path) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        if (!path.endsWith("/")) {
            path = path + "/";
        }
        return path;
    }

    public static void printNBCards() {
        System.out.println("Cards to train :" + Param.GLOBAL_DECK.getCardsToReviewNb());
    }

    //////////////////////
    /// File name utils //
    //////////////////////

    public static void setFileID () {
        switch (Param.TARGET_LANGUAGE) {
            case "English":
                Param.FILE_ID = Param.EN_FILE_ID;
                break;

            case "Deutsch":
                Param.FILE_ID = Param.GE_FILE_ID;
                break;

            case "Français":
                Param.FILE_ID = Param.FR_FILE_ID;
                break;

            case "Italiano":
                Param.FILE_ID = Param.IT_FILE_ID;
                break;

            case "Русский":
                Param.FILE_ID = Param.RU_FILE_ID;
                break;

            case "Español":
                Param.FILE_ID = Param.SP_FILE_ID;
                break;

            default:
                Param.FILE_ID = Param.FILE_ID_UNDEFINED;
                break;
        }
    }

    public static String generateDataFileName () {
        return Param.FILE_NAME_PREFIX + Param.TARGET_LANGUAGE_ISO + ".xlsx";
    }

    //////////////////////
    // Data file utils ///
    //////////////////////

    static void createDataFile() {
        // workbook object
        XSSFWorkbook workbook = new XSSFWorkbook();

        // spreadsheet object
        XSSFSheet sheet = workbook.createSheet(Param.USER_LANGUAGE + " - " + Param.TARGET_LANGUAGE + " vocabulary");

        // creating a row object
        XSSFRow header = sheet.createRow(0);

        int cellid = 0;

        for (Object obj : Param.FIELDS) {
            Cell cell = header.createCell(cellid++);
            cell.setCellValue((String)obj);
        }


        // Create file with its parents folders
        File file = new File(Param.DATA_PATH);

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        try {
            FileOutputStream out = new FileOutputStream(file);

            workbook.write(out);
            out.close();

            System.out.println(Param.DATA_PATH + " has been created");
        } catch (IOException e) {
            e.printStackTrace();

            // TODO inform the user
        }
    }

    public static void cleanDataFile() {
        try {
            FileInputStream inputFile = new FileInputStream(new File(Param.DATA_PATH));
            Workbook workbook = WorkbookFactory.create(inputFile);
            Sheet sheet = workbook.getSheetAt(0);

            List<Integer> rowsToRemoveIndex = new ArrayList<Integer>();

            // Iterate through each rows one by one
            Iterator<Row> rowIterator = sheet.iterator();
            Row header = rowIterator.next();
            Row row;

            int item1Index = Utils.getFieldIndex(header, Param.ITEM1_FIELD_NAME);
            int item2Index = Utils.getFieldIndex(header, Param.ITEM2_FIELD_NAME);
            int rowIndex;

            while (rowIterator.hasNext()) {

                row = rowIterator.next();
                Cell item1Cell = row.getCell(item1Index);
                Cell item2Cell = row.getCell(item2Index);

                if (item1Cell == null || item2Cell == null) {
//                    System.out.println("Removed : " + row.getRowNum());
//                    sheet.removeRow(row);
                    rowsToRemoveIndex.add(row.getRowNum());
                }
            }

            Iterator<Integer> rowToRemoveIndexIterator = rowsToRemoveIndex.iterator();
            while (rowToRemoveIndexIterator.hasNext()) {
                rowIndex = rowToRemoveIndexIterator.next();
                sheet.removeRow(sheet.getRow(rowIndex));
                System.out.println("Removed : " + rowIndex);
            }

            inputFile.close();
            FileOutputStream outputStream = new FileOutputStream(Param.DATA_PATH);
            workbook.write(outputStream);
            outputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void prepareDataFile() {

        // Check data file
        checkFileExistence();

        // Prepare file
        try {
            FileInputStream inputFile = new FileInputStream(new File(Param.DATA_PATH));
            Workbook workbook = WorkbookFactory.create(inputFile);
            Sheet sheet = workbook.getSheetAt(0);

            // Iterate through each rows one by one
            Iterator<Row> rowIterator = sheet.iterator();
            Row header = rowIterator.next();
            Cell currentCell;
//            Cell stateCell;

//            int item1Index = Utils.getFieldIndex(header, Param.ITEM1_FIELD_NAME);
//            int item2Index = Utils.getFieldIndex(header, Param.ITEM2_FIELD_NAME);
            int stateIndex = Utils.getFieldIndex(header, Param.STATE_FIELD_NAME);
            int packIndex = Utils.getFieldIndex(header, Param.PACK_FIELD_NAME);
            int nextPracticeDateIndex = Utils.getFieldIndex(header, Param.NEXT_DATE_FIELD_NAME);
            int creationDateIndex = Utils.getFieldIndex(header, Param.CREATION_DATE_FIELD_NAME);
            int repetitionsIndex = Utils.getFieldIndex(header, Param.REPETITIONS_FIELD_NAME);
            int easinessFactorIndex = Utils.getFieldIndex(header, Param.EF_FIELD_NAME);
            int intervalIndex = Utils.getFieldIndex(header, Param.INTERVAL_FIELD_NAME);
            int uuidIndex = Utils.getFieldIndex(header, Param.UUID_FIELD_NAME);

            while (rowIterator.hasNext()) {

                Row row = rowIterator.next();

//                if (checkCellEmptiness(row.getCell(item1Index), row) || checkCellEmptiness(row.getCell(item2Index), row)) {
//                    stateCell = row.createCell(stateIndex);
//                    stateCell.setCellValue(Param.INVALID);
//                }

                currentCell = row.getCell(stateIndex);
                if (checkCellEmptiness(currentCell, row)) {
                    currentCell = row.createCell(stateIndex);
                    currentCell.setCellValue(Param.DEFAULT_STATE);
                }

                currentCell = row.getCell(packIndex);
                if (checkCellEmptiness(currentCell, row)) {
                    currentCell = row.createCell(packIndex);
                    currentCell.setCellValue(Param.DEFAULT_PACK);
                }

                currentCell = row.getCell(nextPracticeDateIndex);
                if (checkCellEmptiness(currentCell, row)) {
                    currentCell = row.createCell(nextPracticeDateIndex);
                    currentCell.setCellValue(Param.DEFAULT_NEXT_DATE.toString());
                }

                currentCell = row.getCell(creationDateIndex);
                if (checkCellEmptiness(currentCell, row)) {
                    currentCell = row.createCell(creationDateIndex);
                    currentCell.setCellValue(Param.DEFAULT_CREATION_DATE.toString());
                }

                currentCell = row.getCell(repetitionsIndex);
                if (checkCellEmptiness(currentCell, row)) {
                    currentCell = row.createCell(repetitionsIndex);
                    currentCell.setCellValue(Param.DEFAULT_REP);
                }

                currentCell = row.getCell(easinessFactorIndex);
                if (checkCellEmptiness(currentCell, row)) {
                    currentCell = row.createCell(easinessFactorIndex);
                    currentCell.setCellValue(Param.DEFAULT_EF);
                }

                currentCell = row.getCell(intervalIndex);
                if (checkCellEmptiness(currentCell, row)) {
                    currentCell = row.createCell(intervalIndex);
                    currentCell.setCellValue(Param.DEFAULT_INTER);
                }

                currentCell = row.getCell(uuidIndex);
                if (checkCellEmptiness(currentCell, row)) {
                    currentCell = row.createCell(uuidIndex);
                    currentCell.setCellValue(Utils.getNewUUID());
                }
            }

            inputFile.close();
            FileOutputStream outputStream = new FileOutputStream(Param.DATA_PATH);
            workbook.write(outputStream);
            outputStream.close();

            Param.DATA_FILE_ERROR = false;

        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("File possibly corrupted : " + e.getMessage());
            e.printStackTrace();
            Param.DATA_FILE_ERROR = true;
            exportSavedFiles();
        }
    }

    /**
     * Create a copy of the data file in case of file corruption
     */
    public static void saveTempDataFile() {

        String tempSavedFilePath = Param.DATA_PATH
                .replace(".xlsx", "_temp.xlsx");
//                .replace(Param.FILE_NAME_PREFIX, "." + Param.FILE_NAME_PREFIX);

        File originalFile = new File(Param.DATA_PATH);
        File tempSavedFile = new File(tempSavedFilePath);

        try {
            if (originalFile.exists()) {
                FileChannel sourceChannel = new FileInputStream(originalFile).getChannel();
                FileChannel destinationChannel = new FileOutputStream(tempSavedFile).getChannel();
                destinationChannel.transferFrom(sourceChannel, 0, sourceChannel.size());

                sourceChannel.close();
                destinationChannel.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Export data file only for the current language. File is paste in the download folder
     */
    public static void exportSavedFiles() {

        System.out.println("Try to recover data");

        String destinationDirectoryPath = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .getAbsolutePath();
        File destinationDirectory = new File(destinationDirectoryPath);

        File sourceFile = new File(Param.DATA_PATH);
        File sourceFileTemp = new File(Param.DATA_PATH.replace(".xlsx", "_temp.xlsx"));

        if (!sourceFile.exists() || !sourceFile.isFile()) {
            System.out.println("Temp source file doesn't exist or is not valid");
        }
        else {
            copyIndividualFileNoOverwrite(sourceFileTemp, destinationDirectory);
        }

        if (!sourceFile.exists() || !sourceFile.isFile()) {
            System.out.println("Source file doesn't exist or is not valid");
        }
        else {
            copyIndividualFileNoOverwrite(sourceFile, destinationDirectory);
        }
    }

    /**
     * Copy data files without overwriting preexistant ones
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
            return 11;
        }
        return 10;
    }

    public static int getFieldIndex(Row header, String field) {

        Iterator<Cell> cellIterator = header.cellIterator();
        int index = -1;

        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();

            if (field.compareTo(cell.getStringCellValue()) == 0) {
                index = cell.getColumnIndex();
            }
        }

        if (index == -1) {
            System.out.println("No field named : " + field);
            index = header.getLastCellNum();
            header.createCell(index).setCellValue(field);
            System.out.println("Added new field : " + field);
        }

        return (index);
    }

    ///////////////////////
    ///// Toast utils /////
    ///////////////////////

    static void showToast(Context context, String message) {

        Toast toast = new Toast(context);

        View view = LayoutInflater.from(context)
                .inflate(R.layout.toast_layout, null);

        TextView tvMessage = view.findViewById(R.id.tvMessage);
        tvMessage.setText(message);

        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(view);
        toast.show();
    }

    ///////////////////////
    /// Connexion utils ///
    ///////////////////////

    public static Boolean checkConnexion(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    ///////////////////////
    ///// Debug utils /////
    ///////////////////////

    public static void writeDebugLine(String line) {
        try {
            FileWriter writer = new FileWriter(Param.FOLDER_PATH + Param.DEBUG_FILE_NAME, true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);

            bufferedWriter.newLine();
            bufferedWriter.write(line);

            bufferedWriter.close();
            writer.close();

            System.out.println("Successfully written in debug file");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    ///////////////////////
    ///// Text utils /////
    ///////////////////////

    @SuppressLint("ClickableViewAccessibility")
    public static void setTextViewTextColorChangeOnTouch(final TextView button, final int pressedColor, final int normalColor) {
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    button.setTextColor(ContextCompat.getColor(button.getContext(), pressedColor));
                }

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    button.setTextColor(ContextCompat.getColor(button.getContext(), normalColor));
                }
                return false;
            }
        });
    }
}