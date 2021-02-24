package com.hngd.s2m.constant;

import java.util.ArrayList;
import java.util.List;

import io.github.swagger2markup.markup.builder.MarkupTableColumn;

public class Constants {

    public static List<MarkupTableColumn> columnSpecs=new ArrayList<MarkupTableColumn>();
    static {
        columnSpecs.add(new MarkupTableColumn("字段名称"));
        columnSpecs.add(new MarkupTableColumn("类型"));
        columnSpecs.add(new MarkupTableColumn("是否必填"));
        columnSpecs.add(new MarkupTableColumn("约束限制"));
        columnSpecs.add(new MarkupTableColumn("参数位置"));
        columnSpecs.add(new MarkupTableColumn("备注"));
    }
}
