package com.ncpp.asistenteexpedientes.util;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogDony {
    public static PrintWriter writer;

    public static void write(String text) {
        try {
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            SimpleDateFormat formatterFile = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
            String ruta_logs = Constants.RUTA_ARCHIVOS_CONF+"\\logsdonny\\";
            writer = new PrintWriter(ruta_logs + formatterFile.format(date) + ".txt", "UTF-8");
            //writer = new PrintWriter("D:\\logsdony\\" + formatterFile.format(date) + ".txt", "UTF-8");

            writer.write("[" + formatter.format(date) + "] " + text);
            writer.close();
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }

    }
}
