package com.hngd.doc.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import com.google.common.io.Files;
import com.hngd.s2m.OpenAPIToMarkdown;


public class PdfConverterTest {

    @Test
    public void test() throws IOException {
        File openAPIFile=new File("data/openapi.json");
        File md=OpenAPIToMarkdown.openAPIToMarkdown(openAPIFile, null, Files.createTempDir());
        String markdown=FileUtils.readFileToString(md, StandardCharsets.UTF_8);
        String css=FileUtils.readFileToString(new File("test-data/main.css"), StandardCharsets.UTF_8);
        String html=PdfConverter.convertToHtml(markdown,css);
        File testHtml=new File("test-out/test.html");
        FileUtils.write(testHtml,html,StandardCharsets.UTF_8.name());
        //Desktop.getDesktop().open(testHtml);
        File pdfFile=new File("test-out/test.pdf");
        OutputStream out=new FileOutputStream(pdfFile);
        PdfConverter.convertToPdf(markdown, out);
        //Desktop.getDesktop().open(pdfFile);
    }
}
