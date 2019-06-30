package apitest;

import java.io.*;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

public class PreferenceTest {

    public static void main(String[] args) throws IOException, BackingStoreException, InvalidPreferencesFormatException {
        Preferences prefs = Preferences.userRoot().node("stave");
//        prefs.putBoolean("key0", true);
//        prefs.putInt("key1", 11);
//
//        try(OutputStream out = new BufferedOutputStream(new FileOutputStream(
//                "E:\\04文档\\陈宇欣\\流式细胞仪\\软件项目树测试\\prefs.xml"))) {
//            prefs.exportSubtree(out);
//        }

//        try(InputStream in = new BufferedInputStream(new FileInputStream(
//                "E:\\04文档\\陈宇欣\\流式细胞仪\\软件项目树测试\\prefs.xml"))) {
//            Preferences.importPreferences(in);
//        }
//        prefs = null;
//        prefs = Preferences.userRoot().node("stave");
        System.out.println(prefs.getBoolean("key0", false));
    }
}
