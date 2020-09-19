package com.hngd.tool.cl;

import com.hngd.tool.OpenAPIGenerator;
import okhttp3.*;

import java.io.File;
import java.io.IOException;

public class TestUploadFile {

    public static void main(String[]args){

        File file=new File("./pom.xml");


        OpenAPIGenerator.pushToSwaggerUIServer(file,"192.168.0.140:8888");
    }
}
