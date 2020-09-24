package com.hngd.doc;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.pdf.converter.PdfConverterExtension;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.DataSet;
import com.vladsch.flexmark.util.data.MutableDataSet;

@Deprecated
public class MarkdownToPdfTest {

	@Test
	public void test() {
	}
	final private static Map<String, DataHolder> optionsMap = new HashMap<>();
	static {
		optionsMap.put("ltr-text", new MutableDataSet().set(PdfConverterExtension.DEFAULT_TEXT_DIRECTION, PdfRendererBuilder.TextDirection.LTR));
		optionsMap.put("rtl-text", new MutableDataSet().set(PdfConverterExtension.DEFAULT_TEXT_DIRECTION, PdfRendererBuilder.TextDirection.RTL));
	}

}
