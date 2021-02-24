package com.hngd.codegen;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DetectPathVariable {

    public static void main(String[] args) {
        
        String s="/role/{parentMenuId}/list/{gg}/";
        Pattern pattern=Pattern.compile("\\{[^\\{\\}]+\\}");
        
        Matcher matcher=pattern.matcher(s);
        while(matcher.find()){
            int start=matcher.start();
            int end=matcher.end();
            System.out.println(s.substring(start, end));
        }
        
    }
    
    
}
