package com.hngd.code;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class TempFileTest {

    public static void main(String[] args) throws IOException {
        
        File file=Files.createTempDirectory("openapi-ui").toFile();
        System.out.println(file.getAbsolutePath());
        
    }
}
