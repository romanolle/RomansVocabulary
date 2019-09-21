package nf.co.olle.romansvocabulary.ui.io;

import android.os.Environment;

import java.io.File;

public abstract class FileConstants {

    public static final char WORD_SEPARATOR = '|';
    public static final char PARAMETER_SEPARATOR =  ';';
    public static final String NEW_LINE = "\r\n";
    public static final String FOLDER_NAME = "RomansVocabulary";
    public static final File ROOT_FILE = Environment.getExternalStorageDirectory();
    public static final File FOLDER = new File(ROOT_FILE.getAbsolutePath() + File.separator + FOLDER_NAME);

}
