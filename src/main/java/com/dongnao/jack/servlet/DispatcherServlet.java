package com.dongnao.jack.servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dongnao.jack.annotation.Controller;
import com.dongnao.jack.annotation.Qualifier;
import com.dongnao.jack.annotation.RequestMapping;
import com.dongnao.jack.annotation.Service;
import com.dongnao.jack.handlerAdapter.HandlerAdapter;

/**
 * Servlet implementation class DispatcherServlet
 */
public class DispatcherServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    List<String> classNames = new ArrayList<String>();
    
    Map<String, Object> beans = new HashMap<String, Object>();
    
    Map<String, Object> handlerMap = new HashMap<String, Object>();
    
    Properties prop = null;
    
    private static String HANDLERADAPTER = "com.dongnao.jack.handlerAdapter";
    
    /**
     * Default constructor. 
     */
    public DispatcherServlet() {
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @see Servlet#init(ServletConfig)
     */
    public void init(ServletConfig config) throws ServletException {
        //1、我们要根据一个基本包进行扫描，扫描里面的子包以及子包下的类
        scanPackage("com.dongnao");
        
        for (String classname : classNames) {
            System.out.println(classname);
        }
        
        //2、我们肯定是要把扫描出来的类进行实例化
        instance();
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
        
        //3、依赖注入，把service层的实例注入到controller
        ioc();
        
        //4、建立一个path与method的映射关系
        HandlerMapping();
        for (Map.Entry<String, Object> entry : handlerMap.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
        
        InputStream is = this.getClass()
                .getResourceAsStream("/config/properties/spring.properties");
        prop = new Properties();
        try {
            prop.load(is);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }
    
    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        String uri = request.getRequestURI();
        
        String context = request.getContextPath();
        
        String path = uri.replace(context, "");
        
        Method method = (Method)handlerMap.get(path);
        
        Object instance = beans.get("/" + path.split("/")[1]);
        
        HandlerAdapter ha = (HandlerAdapter)beans.get(prop.getProperty(HANDLERADAPTER));
        
        Object[] args = ha.hand(request, response, method, beans);
        
        try {
            method.invoke(instance, args);
        }
        catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
    private void HandlerMapping() {
        if (beans.entrySet().size() <= 0) {
            System.out.println("没有类的实例化！");
            return;
        }
        
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            Object instance = entry.getValue();
            
            Class clazz = instance.getClass();
            
            if (clazz.isAnnotationPresent(Controller.class)) {
                RequestMapping requestMapping = (RequestMapping)clazz.getAnnotation(RequestMapping.class);
                String classPath = requestMapping.value();
                
                Method[] methods = clazz.getMethods();
                
                for (Method method : methods) {
                    if (method.isAnnotationPresent(RequestMapping.class)) {
                        RequestMapping methodrm = (RequestMapping)method.getAnnotation(RequestMapping.class);
                        String methodPath = methodrm.value();
                        
                        handlerMap.put(classPath + methodPath, method);
                    }
                    else {
                        continue;
                    }
                }
            }
        }
    }
    
    private void ioc() {
        
        if (beans.entrySet().size() <= 0) {
            System.out.println("没有类的实例化！");
            return;
        }
        
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            Object instance = entry.getValue();
            
            Class clazz = instance.getClass();
            
            if (clazz.isAnnotationPresent(Controller.class)) {
                Field[] fields = clazz.getDeclaredFields();
                
                for (Field field : fields) {
                    if (field.isAnnotationPresent(Qualifier.class)) {
                        Qualifier qualifier = (Qualifier)field.getAnnotation(Qualifier.class);
                        
                        String value = qualifier.value();
                        
                        field.setAccessible(true);
                        try {
                            field.set(instance, beans.get(value));
                        }
                        catch (IllegalArgumentException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        catch (IllegalAccessException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    else {
                        continue;
                    }
                }
            }
            else {
                continue;
            }
        }
    }
    
    private void instance() {
        if (classNames.size() <= 0) {
            System.out.println("包扫描失败！");
            return;
        }
        
        for (String className : classNames) {
            //com.dongnao.jack.service.impl.JackServiceImpl.class
            String cn = className.replace(".class", "");
            
            try {
                Class clazz = Class.forName(cn);
                
                if (clazz.isAnnotationPresent(Controller.class)) {
                    Controller controller = (Controller)clazz.getAnnotation(Controller.class);
                    Object instance = clazz.newInstance();
                    
                    RequestMapping requestMapping = (RequestMapping)clazz.getAnnotation(RequestMapping.class);
                    String rmvalue = requestMapping.value();
                    
                    beans.put(rmvalue, instance);
                }
                else if (clazz.isAnnotationPresent(Service.class)) {
                    Service service = (Service)clazz.getAnnotation(Service.class);
                    Object instance = clazz.newInstance();
                    
                    beans.put(service.value(), instance);
                }
                else {
                    continue;
                }
            }
            catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (InstantiationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    
    private void scanPackage(String basePackage) {
        
        URL url = this.getClass()
                .getClassLoader()
                .getResource("/" + replaceTo(basePackage));
        
        String fileStr = url.getFile();
        
        File file = new File(fileStr);
        
        String[] filesStr = file.list();
        
        for (String path : filesStr) {
            File filePath = new File(fileStr + path);
            
            if (filePath.isDirectory()) {
                //com.dongnao.jack
                scanPackage(basePackage + "." + path);
            }
            else {
                classNames.add(basePackage + "." + filePath.getName());
            }
        }
    }
    
    private String replaceTo(String basePackage) {
        return basePackage.replaceAll("\\.", "/");
    }
    
}
