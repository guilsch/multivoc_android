package com.guilsch.multivoc;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Scanner;
import java.util.function.Predicate;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Deck extends ArrayList<Card> {

    Deck() {
        super(1);
    }

    public void init() {
        // Adapted from
        // https://howtodoinjava.com/java/library/readingwriting-excel-files-in-java-poi-tutorial/
        try {
            // Create date formater
            SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
            System.out.println(Param.getDataPath());
            FileInputStream file = new FileInputStream(new File(Param.getDataPath()));

            // Create Workbook instance holding reference to excel file
            XSSFWorkbook workbook = new XSSFWorkbook(file);

            // Get first/desired sheet from the workbook
            XSSFSheet sheet = workbook.getSheetAt(0);

            // Iterate through each rows one by one
            Iterator<Row> rowIterator = sheet.iterator();
            Row header = rowIterator.next();

            int indexItem1 = utils.getHeaderIndex(header, "Item 1");
            int indexItem2 = utils.getHeaderIndex(header, "Item 2");
            int indexState = utils.getHeaderIndex(header, "State");
            int indexPack = utils.getHeaderIndex(header, "Pack");
            int indexNextPracticeDate = utils.getHeaderIndex(header, "Next Date");
            int indexRepetitions = utils.getHeaderIndex(header, "Repetitions");
            int indexEasinessFactor = utils.getHeaderIndex(header, "Easiness Factor");
            int indexInterval = utils.getHeaderIndex(header, "Interval");

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                Cell currentCell = row.getCell(0);

                if (currentCell != null && row.getCell(indexState).getNumericCellValue() != Param.INVALID) {

                    String item1 = row.getCell(indexItem1).getStringCellValue();
                    String item2 = row.getCell(indexItem2).getStringCellValue();
                    int state = (int) row.getCell(indexState).getNumericCellValue();
                    String pack = row.getCell(indexPack).getStringCellValue();
                    Date nextPracticeDate = formatter.parse(row.getCell(indexNextPracticeDate).getStringCellValue());
                    int repetitions = (int) row.getCell(indexRepetitions).getNumericCellValue();
                    float easinessFactor = (float) row.getCell(indexEasinessFactor).getNumericCellValue();
                    int interval = (int) row.getCell(indexInterval).getNumericCellValue();

                    this.add(new Card(item1, item2, state, pack, nextPracticeDate, repetitions, easinessFactor, interval));

                }
            }

            file.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void flip() {
        Iterator<Card> cardIterator = this.iterator();
          
        while(cardIterator.hasNext()){
            Card card = cardIterator.next();
            Scanner scanner = new Scanner(System.in);
            String a = "";
            int quality = -1;

            // Question side
            System.out.println(card.getItem1());
            do {
                System.out.println("Press a to see answer : ");
                a = scanner.nextLine();
            }
            while (a.compareTo("a") != 0);
            
            // Answer side
            System.out.println(card.getItem2());
            do {
                System.out.println("Enter quality of your answer (0-5) : ");
                quality = Integer.parseInt(scanner.nextLine());
            }
            while (quality < 0 || quality > 5);

            // scanner.close();            

            // Process algo and store data
            MemoAlgo.SuperMemo2(card, quality);
            card.updateDatabase(card.getItem1());
            
        }
    }

    public void showCards() {
        Iterator<Card> cardIterator = this.iterator();

        while(cardIterator.hasNext()){
            Card card = cardIterator.next();
            System.out.println("Item 1 : " + card.getItem1() + 
            "  |   Item 2 : " + card.getItem2() + 
            "  |   State : " + card.getState() +
            "  |   Pack : " + card.getPack() + 
            "  |   Next practice date : " + card.getNextPracticeDate() + 
            "  |   Repetitions : " + card.getRepetitions() + 
            "  |   Easiness factor : " + card.getEasinessFactor() + 
            "  |   Interval : " + card.getInterval());
        }
    }

    public void keepNFirst(int N) {
        if (this.size() > N) {
            this.subList(0, N);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void filter() {

        Iterator<Card> iterator = this.iterator();
        Predicate<Card> pred = x -> x.getInterval() > 3;

        while (iterator.hasNext()) {
            Card card = iterator.next();

            if (!pred.test(card)) {
                iterator.remove();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void filterToTrain() {

        Iterator<Card> iterator = this.iterator();
        Predicate<Card> pred = x -> x.getNextPracticeDate().compareTo(utils.giveDate()) < 0 && x.getState() == Param.ACTIVE ;

        while (iterator.hasNext()) {
            Card card = iterator.next();

            if (!pred.test(card)) {
                iterator.remove();
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void filterToLearn() {

        Iterator<Card> iterator = this.iterator();
        Predicate<Card> pred = x -> x.getState() == Param.TO_LEARN;

        while (iterator.hasNext()) {
            Card card = iterator.next();

            if (!pred.test(card)) {
                iterator.remove();
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void filterActive() {

        Iterator<Card> iterator = this.iterator();
        Predicate<Card> pred = x -> x.getState() == Param.ACTIVE;

        while (iterator.hasNext()) {
            Card card = iterator.next();

            if (!pred.test(card)) {
                iterator.remove();
            }
        }

    }

}