package com.hngd.doc.utils;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.parboiled.common.FileUtils;

import com.google.common.io.Files;
import com.hngd.doc.core.OpenAPIFileManager;
import com.hngd.s2m.OpenAPIToMarkdown;

import io.swagger.v3.oas.models.OpenAPI;

public class PdfConverterTest {

    @Test
    public void test() throws IOException {
        File openAPIFile=new File("data/openapi.json");
        File md=OpenAPIToMarkdown.openAPIToMarkdown(openAPIFile, null, Files.createTempDir());
        String markdown=FileUtils.readAllText(md, StandardCharsets.UTF_8);
        String css=FileUtils.readAllText(new File("test-data/main.css"), StandardCharsets.UTF_8);
        String html=PdfConverter.convertToHtml(markdown,css);
        File testHtml=new File("test-out/test.html");
        FileUtils.writeAllText(html,testHtml,StandardCharsets.UTF_8);
        //Desktop.getDesktop().open(testHtml);
        File pdfFile=new File("test-out/test.pdf");
        OutputStream out=new FileOutputStream(pdfFile);
        PdfConverter.convertToPdf(markdown, out);
        //Desktop.getDesktop().open(pdfFile);
    }
}
