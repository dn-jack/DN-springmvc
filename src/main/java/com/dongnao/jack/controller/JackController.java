package com.dongnao.jack.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.dongnao.jack.annotation.Controller;
import com.dongnao.jack.annotation.Qualifier;
import com.dongnao.jack.annotation.RequestMapping;
import com.dongnao.jack.annotation.RequestParam;
import com.dongnao.jack.service.JackService;

@Controller
@RequestMapping("/jack")
public class JackController {
    
    @Qualifier("JackServiceImpl")
    private JackService jackService;
    
    @RequestMapping("/query")
    public void query(HttpServletRequest request, HttpServletResponse response,
            HttpSession session, Map map,
            @RequestParam("name") String userName, List list) {
        
        try {
            PrintWriter pw = response.getWriter();
            String result = jackService.query(null);
            pw.write(result);
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @RequestMapping("/insert")
    public void insert(HttpServletRequest request,
            HttpServletResponse response, String param) {
        try {
            PrintWriter pw = response.getWriter();
            String result = jackService.insert(param);
            
            pw.write(result);
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @RequestMapping("/update")
    public void update(HttpServletRequest request,
            HttpServletResponse response, String param) {
        try {
            PrintWriter pw = response.getWriter();
            String result = jackService.update(param);
            
            pw.write(result);
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
