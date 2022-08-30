package com.vslc.tools.xml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * Created by chenlele
 * 2018/6/8 11:15
 */
public class XmlUtil {

    public static Document getDocument(File file) {
        SAXReader reader = new SAXReader();
        Document document = null;
        try {
            document = reader.read(file);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return document;
    }

    public static void writeXML(File dest, Document document) {
        try {
            OutputFormat format = new OutputFormat();
            format.setEncoding("utf-8");
            // 创建XMLWriter对象
            XMLWriter writer = new XMLWriter(new FileOutputStream(dest), format);
            //设置不自动进行转义
            writer.setEscapeText(false);
            // 生成XML文件
            writer.write(document);
            //关闭XMLWriter对象
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
