package com.hngd.base;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hngd.openapi.config.ServerConfig;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.servers.ServerVariable;
import io.swagger.v3.oas.models.servers.ServerVariables;


public class ApiInfoLoader {

    public static ServerConfig loadInfo(String confFilePath) {
        Gson gson = new Gson();
        File file = new File(confFilePath);
        Path path = file.toPath();
        byte[] data = null;
        try {
            data = Files.readAllBytes(path);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (data == null) {
            return null;
        }
        String src = new String(data, Charset.forName("UTF-8"));

        return gson.fromJson(src, ServerConfig.class);
    }
    public static void main(String[] args) {
        String confFilePath="./conf/swagger-config.json";
        ServerConfig info=loadInfo(confFilePath);
        Gson gson=new GsonBuilder().setPrettyPrinting().create();
        String str=gson.toJson(info);
        System.out.println(str);
        /**
        ServerConfig sc=new ServerConfig();
        sc.info=info;
        sc.servers=new ArrayList<>();
        Server s=new Server();
        s.setDescription("144网关");
        String url="https://192.168.0.144:8080";
        s.setUrl(url);
        ServerVariables variables=new ServerVariables();
        ServerVariable sv=new ServerVariable();
        sv.setDefault("默认");
        sv.setDescription("默认测试");
        variables.addServerVariable("测试", sv);
        s.setVariables(variables);
        sc.servers.add(s);
        
        Gson gson=new GsonBuilder().setPrettyPrinting().create();
        String str=gson.toJson(sc);
        System.out.println(str);
        */
    }
}
