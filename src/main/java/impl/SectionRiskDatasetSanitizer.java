package impl;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SectionRiskDatasetSanitizer {


    static String inputCsvOriginal = "myTest.csv";
    static String outputCsvSanitizedBefore1 = "aOutputCsvSanitizedBefore1.csv";
    static String outputCsvSanitizedAfter1 = "bOutputCsvSanitizedAfter1.csv";

    static String folderAbsolutePath = "C:\\Users\\olive\\Desktop\\AI\\ai-trabalho24V5\\target\\classes\\sampleRobots\\SectionRiskWriterRobot.data\\";
    static long minimumTimeBeforeGotShotRegistry = 20;


    public static void main(String[] args) throws Exception {


        SectionRiskDatasetSanitizer.filterRowsBefore1(folderAbsolutePath, inputCsvOriginal, outputCsvSanitizedBefore1, minimumTimeBeforeGotShotRegistry);
        SectionRiskDatasetSanitizer.filterRowsAfter1(folderAbsolutePath, outputCsvSanitizedBefore1, outputCsvSanitizedAfter1, minimumTimeBeforeGotShotRegistry);


    }


    public static void filterRowsBefore1(String folderAbsolutePath, String inputCsv, String outputCsv, long minimumTimeBeforeGotShotRegistry) throws IOException {

        //List of the splited lines of the csv
        List<String[]> originalRows = new ArrayList<>();

        // Read the CSV file
        {
            int maxNumberOfElementsPerRow = -1;
            int minNumberOfElementsPerRow = 9999;

            try (BufferedReader br = new BufferedReader(new FileReader(folderAbsolutePath + inputCsv))) {
                String line;
                //For each line of the csv
                while ((line = br.readLine()) != null) {
                    //Split the line in different values
                    String[] parts = line.split(",");
                    //Save the Splitted line in the rows list
                    originalRows.add(parts);
                    if (parts.length > maxNumberOfElementsPerRow) {
                        maxNumberOfElementsPerRow = parts.length;
                    }
                    if (parts.length < minNumberOfElementsPerRow) {
                        minNumberOfElementsPerRow = parts.length;
                    }
                }
            }
            System.out.println("NumberOfRows: " + originalRows.size());
            System.out.println("MaxNumberOfElementsPerRow: " + maxNumberOfElementsPerRow);
            System.out.println("MinNumberOfElementsPerRow: " + minNumberOfElementsPerRow);
        }

        //Print Atributes
        {
            int counter = 0;
            for (String element : originalRows.get(0)) {
                System.out.println(element + "\t" + counter);
                counter++;
            }
            System.out.println();
        }


        // List to keep track of rows to keep
        List<String[]> filteredRows = new ArrayList<>();

        //AddHeaders
        filteredRows.add(originalRows.get(0));
        // For each row starting in 1 (line next to header)
        for (int originalLine = 1; originalLine < originalRows.size(); originalLine++) {

            //For each row
            int timeRowPositon = 0;
            int resultRowPosition = originalRows.get(originalLine).length - 1;
            long time = Long.parseLong(originalRows.get(originalLine)[timeRowPositon]);
            int result = Integer.parseInt(originalRows.get(originalLine)[resultRowPosition]);

            //If result is 0
            if (result == 0) {

                //Add the line to filtered
                filteredRows.add(originalRows.get(originalLine));
                System.out.println("Added a 0 result");
            } else {

                //If result is 1
                System.out.println("Found a 1 Result");
                //Add the line to filtered
                filteredRows.add(originalRows.get(originalLine));

                //Remove the previous 5 lines with 0 results
                {
                    //For each line = i-1 until 5 rows before
                    for (int previousLine = originalLine - 1; previousLine >= (originalLine - 5); previousLine--) {

                        //If i'm on the header, break cycle
                        if (previousLine == 0) {
                            System.out.println("I'm on the header, break");
                            break;
                        }

                        //Check the time and result of the previous line
                        long prevTime = Long.parseLong(originalRows.get(previousLine)[timeRowPositon]);
                        int prevResult = Integer.parseInt(originalRows.get(previousLine)[resultRowPosition]);

                        //If the result is 0 and time is until 30 milissecunts
                        if (prevResult == 0 && (time - prevTime) <= minimumTimeBeforeGotShotRegistry) {
                            System.out.println("Removed a line");
                            filteredRows.remove(previousLine);
                            originalRows.remove(previousLine);
                            originalLine--; // Adjust the current index because we removed a row
                        } else if ((time - prevTime) > minimumTimeBeforeGotShotRegistry) {
                            System.out.println("Went 5 lines back, break cycly");
                            break;
                        }
                    }
                }
            }
        }

        // Write the filtered rows to a new CSV file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(folderAbsolutePath + outputCsv))) {
            for (String[] row : filteredRows) {
                bw.write(String.join(",", row));
                bw.newLine();
            }
        }
    }

    public static void filterRowsAfter1(String folderAbsolutePath, String inputCsv, String outputCsv, long minimumTimeBeforeGotShotRegistry) throws IOException {

        //List of the splited lines of the csv
        List<String[]> originalRows = new ArrayList<>();

        // Read the CSV file
        {
            int maxNumberOfElementsPerRow = -1;
            int minNumberOfElementsPerRow = 9999;

            try (BufferedReader br = new BufferedReader(new FileReader(folderAbsolutePath + inputCsv))) {
                String line;
                //For each line of the csv
                while ((line = br.readLine()) != null) {
                    //Split the line in different values
                    String[] parts = line.split(",");
                    //Save the Splitted line in the rows list
                    originalRows.add(parts);
                    if (parts.length > maxNumberOfElementsPerRow) {
                        maxNumberOfElementsPerRow = parts.length;
                    }
                    if (parts.length < minNumberOfElementsPerRow) {
                        minNumberOfElementsPerRow = parts.length;
                    }
                }
            }
            System.out.println("NumberOfRows: " + originalRows.size());
            System.out.println("MaxNumberOfElementsPerRow: " + maxNumberOfElementsPerRow);
            System.out.println("MinNumberOfElementsPerRow: " + minNumberOfElementsPerRow);
        }

        //Print Atributes
        {
            int counter = 0;
            for (String element : originalRows.get(0)) {
                System.out.println(element + "\t" + counter);
                counter++;
            }
            System.out.println();
        }


        Collections.reverse(originalRows);

        // List to keep track of rows to keep
        List<String[]> filteredRows = new ArrayList<>();

        // For each row starting in 1 (line next to header)
        for (int originalLine = 0; originalLine < originalRows.size() - 1; originalLine++) {

            //For each row
            int timeRowPositon = 0;
            int resultRowPosition = originalRows.get(originalLine).length - 1;
            long time = Long.parseLong(originalRows.get(originalLine)[timeRowPositon]);
            int result = Integer.parseInt(originalRows.get(originalLine)[resultRowPosition]);

            //If result is 0
            if (result == 0) {

                //Add the line to filtered
                filteredRows.add(originalRows.get(originalLine));
                System.out.println("Added a 0 result");
            } else {

                //If result is 1
                System.out.println("Found a 1 Result");
                //Add the line to filtered
                filteredRows.add(originalRows.get(originalLine));

                //Remove the previous 5 lines with 0 results
                {
                    //For each line = i-1 until 5 rows before
                    for (int previousLine = originalLine - 1; previousLine >= (originalLine - 5); previousLine--) {

                        //If i'm on the header, break cycle
                        if (previousLine == 0) {
                            System.out.println("I'm on the header, break");
                            break;
                        }

                        //Check the time and result of the previous line
                        long postTime = Long.parseLong(originalRows.get(previousLine)[timeRowPositon]);
                        int postResult = Integer.parseInt(originalRows.get(previousLine)[resultRowPosition]);

                        //If the result is 0 and time is until 30 milissecunts
                        if (postResult == 0 && (postTime - time) <= minimumTimeBeforeGotShotRegistry) {
                            System.out.println("Removed a line");
                            filteredRows.remove(previousLine);
                            originalRows.remove(previousLine);
                            originalLine--; // Adjust the current index because we removed a row
                        } else if ((postTime - time) > minimumTimeBeforeGotShotRegistry) {
                            System.out.println("Went 5 lines back, break cycly");
                            break;
                        }
                    }
                }
            }
        }
        //AddHeaders
        filteredRows.add(originalRows.get(originalRows.size() - 1));

        //Reverse again
        Collections.reverse(filteredRows);

        // Write the filtered rows to a new CSV file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(folderAbsolutePath + outputCsv))) {
            for (String[] row : filteredRows) {
                bw.write(String.join(",", row));
                bw.newLine();
            }
        }
    }

}





