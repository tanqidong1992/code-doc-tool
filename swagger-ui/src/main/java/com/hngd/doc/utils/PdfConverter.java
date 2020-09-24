package com.hngd.doc.utils;

import java.io.OutputStream;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.pdf.converter.PdfConverterExtension;
import com.vladsch.flexmark.profile.pegdown.Extensions;
import com.vladsch.flexmark.profile.pegdown.PegdownOptionsAdapter;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;

public class PdfConverter {

	final private static DataHolder OPTIONS = PegdownOptionsAdapter.flexmarkOptions(
            Extensions.ALL
    );
 
	public static String convertToHtml(String markdown,String css){
        MutableDataSet options = new MutableDataSet(OPTIONS);
        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();
        Node document = parser.parse(markdown);
        String html = renderer.render(document);  // "<p>This is <em>Sparta</em></p>\n"
        System.out.println(html);
        html="<html><head>" +
                "<meta http-equiv=\"content-type\" content=\"text/html;charset=utf-8\">"
                +"<style type=\"text/css\">"
                +css
                +"</style>"
                +"</head><body>\n"+
                html + "</body></html>\n";
        return html;
    }
	public static void convertToPdf(String markdown,OutputStream out) {
        String html=convertToHtml(markdown,"");
        PdfConverterExtension.exportToPdf(out, html,"", OPTIONS);
	}
}
