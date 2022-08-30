package com.vslc;

import com.vslc.tools.FileUtil;
import com.vslc.tools.SavePath;
import com.vslc.tools.array.ArrDataHandler;
import org.ini4j.Wini;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * Created by chenlele
 * 2018/7/1 15:17
 */
public class ClearApplication {

    private static String initPath = SavePath.iniPath;

    private static final String hospitalPath = initPath + "hospital.ini";

    @Test
    public void clear() {
        try {
            Wini ini = new Wini(new File(hospitalPath));
            Set<String> set = ini.keySet();
            for (String section : set) {
                Map<String, String> map = ini.get(section);
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    String hospPath = SavePath.srcPath + entry.getKey();
                    File hospDir = new File(hospPath);
                    if (hospDir.exists()) egodicHosp(hospDir);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void egodicHosp(File hospDir) {
        File[] inspes = hospDir.listFiles();
        for (File inspeDir : inspes)
            egodicInspe(inspeDir);

    }

    public void egodicInspe(File inspeDir) {
        File[] ses = inspeDir.listFiles();
        for (File seDir : ses) {
            String fileName = seDir.getName();
            if (!fileName.equals("NotDcm") && !fileName.equals("info.xml")) {
                egodicSeries(seDir);
            }
        }
    }

    public void egodicSeries(File seriesDir) {
        File[] types = seriesDir.listFiles();
        for (File typeDir : types) {
            String fileName = typeDir.getName();
            if (fileName.equals("MASK")) deleteMatrix(typeDir);
            else if (fileName.equals("JPG")) deleteDir(typeDir);
            else if (fileName.equals("BINX")) deleteDir(typeDir);
            else if (fileName.equals("temp")) deleteDir(typeDir);
            else if (fileName.equals("mulTemp")) deleteDir(typeDir);
        }
    }

    public void deleteFiles(File typeDir) {
        File[] files = typeDir.listFiles();
        for (File file : files) file.delete();
    }

    public void deleteDir(File typeDir) {
        FileUtil.delete(typeDir);
    }

    public void deleteMatrix(File typeDir) {
        File[] files = typeDir.listFiles();
        for (File file : files) {
            if (file.getName().contains("matrix")) {
                file.delete();
            }
        }
    }
}
