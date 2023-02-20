package com.guilsch.multivoc;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.Date;

public class Card implements Serializable {

    private String item1;
    private String item2;
    private int repetitions;
    private float easinessFactor;
    private int interval;
    private Date nextPracticeDate;
    private int state;
    private String pack;
    private String uuid;
    private int rowIndexInExcel;

    Card (String item1, String item2, int state, String pack, Date nextPracticeDate, int repetitions,
          float easinessFactor, int interval, String uuid, int rowIndexInExcel){
        this.item1 = item1;
        this.item2 = item2;
        this.repetitions = repetitions;
        this.easinessFactor = easinessFactor;
        this.interval = interval;
        this.nextPracticeDate = nextPracticeDate;
        this.state = state;
        this.pack = pack;
        this.uuid = uuid;
        this.rowIndexInExcel = rowIndexInExcel;
    }

    public void updateParameters(Date nextPracticeDate, int repetitions, float easinessFactor, int interval) {
        this.setNextPracticeDate(nextPracticeDate);
        this.setRepetitions(repetitions);
        this.setEasinessFactor(easinessFactor);
        this.setInterval(interval);
    }

    public void updateInDatabase() {
        try {

            FileInputStream inputFile = new FileInputStream(new File(Param.DATA_PATH));
            Workbook workbook = WorkbookFactory.create(inputFile);
            Sheet sheet = workbook.getSheetAt(0);

            Row header = sheet.getRow(sheet.getFirstRowNum());

            int item1Index = utils.getFieldIndex(header, Param.ITEM1_FIELD_NAME);
            int item2Index = utils.getFieldIndex(header, Param.ITEM2_FIELD_NAME);
            int stateIndex = utils.getFieldIndex(header, Param.STATE_FIELD_NAME);
            int packIndex = utils.getFieldIndex(header, Param.PACK_FIELD_NAME);
            int nextPracticeDateIndex = utils.getFieldIndex(header, Param.NEXT_DATE_FIELD_NAME);
            int repetitionsIndex = utils.getFieldIndex(header, Param.REPETITIONS_FIELD_NAME);
            int easinessFactorIndex = utils.getFieldIndex(header, Param.EF_FIELD_NAME);
            int intervalIndex = utils.getFieldIndex(header, Param.INTERVAL_FIELD_NAME);
            int uuidIndex = utils.getFieldIndex(header, Param.UUID_FIELD_NAME);

            Row cardRow = sheet.getRow(this.rowIndexInExcel);
            Cell uuidCell = cardRow.getCell(uuidIndex);

            if (uuidCell.getStringCellValue().compareTo(uuid) == 0) {

                cardRow.getCell(item1Index).setCellValue(this.item1);
                cardRow.getCell(item2Index).setCellValue(this.item2);
                cardRow.getCell(stateIndex).setCellValue(this.state);
                cardRow.getCell(packIndex).setCellValue(this.pack);
                cardRow.getCell(nextPracticeDateIndex).setCellValue(this.nextPracticeDate.toString());
                cardRow.getCell(repetitionsIndex).setCellValue(this.repetitions);
                cardRow.getCell(easinessFactorIndex).setCellValue(this.easinessFactor);
                cardRow.getCell(intervalIndex).setCellValue(this.interval);

                System.out.println("card found");
            }
            else {
                System.out.println("card NOT found");
            }

            inputFile.close();
            FileOutputStream outputStream = new FileOutputStream(Param.DATA_PATH);
            workbook.write(outputStream);
            outputStream.close();
            
            } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addToDatabase() {
        try {

            FileInputStream inputFile = new FileInputStream(new File(Param.DATA_PATH));
            Workbook workbook = WorkbookFactory.create(inputFile);
            Sheet sheet = workbook.getSheetAt(0);

            Row header = sheet.getRow(sheet.getFirstRowNum());
            Row newRow = sheet.createRow(sheet.getLastRowNum() + 1);

            int item1Index = utils.getFieldIndex(header, Param.ITEM1_FIELD_NAME);
            int item2Index = utils.getFieldIndex(header, Param.ITEM2_FIELD_NAME);
            int stateIndex = utils.getFieldIndex(header, Param.STATE_FIELD_NAME);
            int packIndex = utils.getFieldIndex(header, Param.PACK_FIELD_NAME);
            int nextPracticeDateIndex = utils.getFieldIndex(header, Param.NEXT_DATE_FIELD_NAME);
            int repetitionsIndex = utils.getFieldIndex(header, Param.REPETITIONS_FIELD_NAME);
            int easinessFactorIndex = utils.getFieldIndex(header, Param.EF_FIELD_NAME);
            int intervalIndex = utils.getFieldIndex(header, Param.INTERVAL_FIELD_NAME);
            int uuidIndex = utils.getFieldIndex(header, Param.UUID_FIELD_NAME);

            for (int i = 0; i < Param.FIELDS_NB; i++) {
                newRow.createCell(i);
            }

            newRow.getCell(item1Index).setCellValue(this.item1);
            newRow.getCell(item2Index).setCellValue(this.item2);
            newRow.getCell(stateIndex).setCellValue(this.state);
            newRow.getCell(packIndex).setCellValue(this.pack);
            newRow.getCell(nextPracticeDateIndex).setCellValue(this.nextPracticeDate.toString());
            newRow.getCell(repetitionsIndex).setCellValue(this.repetitions);
            newRow.getCell(easinessFactorIndex).setCellValue(this.easinessFactor);
            newRow.getCell(intervalIndex).setCellValue(this.interval);
            newRow.getCell(uuidIndex).setCellValue(this.uuid);

            // If the card is upload for the first time in the database (means that rowIndex is -1),
            // the row index is attributed to the card
            if (this.rowIndexInExcel == -1) {
                this.setRowIndexInExcel(newRow.getRowNum());
            }

            inputFile.close();
            FileOutputStream outputStream = new FileOutputStream(Param.DATA_PATH);
            workbook.write(outputStream);
            outputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void info() {
        System.out.println("Item 1 : " + this.item1);
        System.out.println("Item 2 : " + this.item2);
        System.out.println(String.format("State : %b", this.state));
        System.out.println("Pack : " + this.pack);
        System.out.println(String.format("Next practice : %s", nextPracticeDate.toString()));
        System.out.println(String.format("Repetitions : %d", this.repetitions));
        System.out.println(String.format("Easiness Factor : %.2f", this.easinessFactor));
        System.out.println(String.format("Interval : %d", this.interval));
        System.out.println("UUID : " + this.uuid);

    }

    // Getters

    public String getItem1() {
        return item1;
    }

    public String getItem2() {
        return item2;
    }

    public int getState() {
        return state;
    }

    public String getPack() {
        return pack;
    }

    public int getRepetitions() {
        return repetitions;
    }

    public float getEasinessFactor() {
        return easinessFactor;
    }

    public int getInterval() {
        return interval;
    }

    public Date getNextPracticeDate() {
        return nextPracticeDate;
    }

    public String getUuid() {
        return uuid;
    }

    public int getRowIndexInExcel(){
        return rowIndexInExcel;
    }

    // Setters

    public void setItem1(String item1) {
        this.item1 = item1;
    }

    public void setItem2(String item2) {
        this.item2 = item2;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setPack(String pack) {
        this.pack = pack;
    }

    public void setEasinessFactor(float easinessFactor) {
        this.easinessFactor = easinessFactor;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public void setRepetitions(int repetitions) {
        this.repetitions = repetitions;
    }

    public void setNextPracticeDate(Date nextPracticeDate) {
        this.nextPracticeDate = nextPracticeDate;
    }

    public void setRowIndexInExcel(int rowIndex) {
        this.rowIndexInExcel = rowIndex;
    }
}
