package org.firstinspires.ftc.teamcode.util;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CSVLogger {
    private FileWriter writer;
    private boolean headerWritten = false;

    /**
     * Creates a CSV logger that saves to /sdcard/FIRST/data/
     * Files are named with timestamp: telemetry_2026-01-31_15-30-45.csv
     */
    public CSVLogger(String prefix) {
        try {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US).format(new Date());
            String filename = "/sdcard/FIRST/data/" + prefix + "_" + timestamp + ".csv";
            writer = new FileWriter(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Write CSV header (call once before logging data)
     */
    public void writeHeader(String... headers) {
        if (writer == null || headerWritten) return;
        try {
            writer.write(String.join(",", headers) + "\n");
            headerWritten = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Write a row of data
     */
    public void writeRow(Object... values) {
        if (writer == null) return;
        try {
            StringBuilder row = new StringBuilder();
            for (int i = 0; i < values.length; i++) {
                row.append(values[i]);
                if (i < values.length - 1) row.append(",");
            }
            row.append("\n");
            writer.write(row.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Close the file (call when OpMode ends)
     */
    public void close() {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
