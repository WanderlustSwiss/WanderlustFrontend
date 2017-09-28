package eu.wise_iot.wanderlust.utils;

import android.os.Environment;
import android.util.Log;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by fabianschwander on 07.07.17.
 */

public class MyFileManager {
    private static final String TAG = "MyFileManager";

    private File file;
    private String filename;

    public MyFileManager(String filename) {
        this.filename = filename;
    }

    // write File to external Storage
    public static boolean canWriteOnExternalStorage() {
        // get the state of your external storage
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // if storage is mounted return true
            return true;
        }
        return false;
    }

    private void createFileInExternalStorage() {
        // get the path to /Downloads directory
        File downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        // add a new directory path
        File dir = new File(downloads.getAbsolutePath() + "/wanderlust/");
        // create this directory if not already created
        dir.mkdir();

        // create the file in which we will write the contents
        file = new File(dir, filename);
    }

    private void writeToFileInExternalStorage(String data) throws IOException {
        // append to already existing data
        FileWriter fileWriter = new FileWriter(file, true);
        fileWriter.write(data);
        fileWriter.close();
    }

    public void saveDataInFile(String data) {
        if (canWriteOnExternalStorage()) try {
            createFileInExternalStorage();
            writeToFileInExternalStorage(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
