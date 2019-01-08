/**
 * Copyright (c) 2017,湖南华南光电科技股份有限公司
 * All rights reserved.
 *
 * @文件名：MainServlet.java
 * @时间：2017年2月13日 下午4:20:24
 * @作者：
 * @备注：
 * @版本:
 */
package com.apidoc.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.hngd.webapi.doc.App;

/**
 * @author 
 */
@WebServlet("/main")
public class MainServlet extends HttpServlet
{
    
    /**
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse) 
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        ///api?api_key=test
        String key=req.getParameter("api_key");
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out=resp.getWriter();
        String ss=null;
        if(!StringUtils.isEmpty(key)){
            ss=App.toJson(key);
            
        }else{
            ss=App.toJson(); 
        }
        
       
        out.write(ss);
        out.flush();
        out.close();
    }
}
