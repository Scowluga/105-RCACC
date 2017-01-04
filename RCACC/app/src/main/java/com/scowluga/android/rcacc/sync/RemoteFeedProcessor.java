package com.scowluga.android.rcacc.sync;


import com.scowluga.android.rcacc.Message.Message;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class RemoteFeedProcessor {

    private static final DateFormat DATE_FORMAT_SHORT = new SimpleDateFormat("yyyy-MM-dd");
    private static final DateFormat DATE_FORMAT_LONG = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private static String SPLIT_DELIMITER = "[|]";
    private static char DELIMITER = '|';

    private static String GRAND_DELIMITER = "XXXXX";

    public static List<Message> decode(InputStream in) throws IOException, ParseException {

        List<Message> entries = new ArrayList<>();

        if (in == null) {
            return entries;
        }

        InputStreamReader inputStreamReader = new InputStreamReader ( in ) ;

        char[] inputBuffer= new char[3001];
        String s="";
        int charRead;

        while ((charRead=inputStreamReader.read(inputBuffer))>0) {
            // char to string conversion
            String readstring=String.copyValueOf(inputBuffer,0,charRead);
            s +=readstring;
        }
        inputStreamReader.close();

        List<String> messages = new ArrayList<>(Arrays.asList(s.split(GRAND_DELIMITER)));

        for (String message : messages) {
            entries.add(decodeMessage(message)); //decode it

        }
        return entries;
    }

    public static java.io.File encode (List<Message> messages) throws IOException {
        File file = File.createTempFile("105 RCACC", ".txt");
        FileOutputStream outputStream = new FileOutputStream(file);

        for (Message m: messages) {
            String write = encodeMessage(m); //encode it
            outputStream.write(write.getBytes());
        }
        outputStream.close();
        return file;
    }

    private static String encodeMessage (Message m) {
        StringBuilder sb = new StringBuilder();

        sb.append(m.getTitle()).append(DELIMITER);
        sb.append(m.getDate().getTime()).append(DELIMITER);
        sb.append(m.getAudience()).append(DELIMITER);
        sb.append(m.getNotes()).append(GRAND_DELIMITER);

        return sb.toString();
    }

    private static Message decodeMessage (String messageString) {
        String[] attributes = messageString.split(SPLIT_DELIMITER);
        return new Message(attributes[0], new Date(Long.parseLong(attributes[1])), attributes[2], attributes[3]);

    }
}
