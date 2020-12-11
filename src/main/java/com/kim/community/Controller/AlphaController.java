package com.kim.community.Controller;

import com.kim.community.Entity.Page;
import com.kim.community.utils.CommunityUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.websocket.server.PathParam;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Controller
@RequestMapping("/alpha")
public class AlphaController {
    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello() {
        return "Hello Spring Boot.";
    }

    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response) {
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            String value = request.getHeader(name);
            System.out.println(name + ":" + value);
        }
        System.out.println(request.getParameter("code"));

        response.setContentType("text/html;charset=utf-8");

        try (
                PrintWriter writer = response.getWriter();
        ) {
            writer.write("<h1>test http</h1>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // url: students?current=1&limit=20
    // 获取url参数
    @RequestMapping(path = "/students", method = RequestMethod.GET)
    @ResponseBody
    public String getStudents(@RequestParam(name = "current", required = false, defaultValue = "1") int current,
                              @RequestParam(name = "limit", required = false, defaultValue = "20") int limit) {
        System.out.println(current);
        System.out.println(limit);
        return "some students";
    }

    // url: student/123
    // 获取 student_id
    @RequestMapping(path = "/student/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable("id") int id) {
        System.out.println(id);
        return "a student";
    }

    // 获取POST 方法传送的表单
    // 形参跟表单上的变量名对应 就能自动获取
    @RequestMapping(path = "/student", method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name, String age) {
        System.out.println(name);
        System.out.println(age);
        return "保存学生";
    }

    // 返回HTML数据
    @RequestMapping(path = "/teacher")
    public ModelAndView getTeacher() {
        ModelAndView mav = new ModelAndView();
        mav.addObject("name", "hhh");
        mav.addObject("age", 40);
        mav.setViewName("/demo/view");
        return mav;
    }

    //dispatcher自动创建Model装载到形参中
    @RequestMapping(path = "school")
    public String getSchool(Model model) {
        model.addAttribute("name", "school");
        model.addAttribute("age", 90);
        return "/demo/view";
    }

    // 响应JSON数据
    // @ResponseBody 自动把返回的Map对象 转换成 JSON格式
    @RequestMapping(path = "emp")
    @ResponseBody
    public Map<String, Object> getEmp() {
        Map<String, Object> emp = new HashMap<>();
        emp.put("name", "ZS");
        emp.put("age", 23);
        emp.put("salary", 20000.);
        return emp;
    }

    @RequestMapping(path = "emps")
    @ResponseBody
    public List<Map<String, Object>> getEmps() {
        List<Map<String, Object>> emps = new ArrayList<>();
        Map<String, Object> emp = new HashMap<>();
        emp.put("name", "ZS");
        emp.put("age", 23);
        emp.put("salary", 20000.);
        emps.add(emp);
        emp = new HashMap<>();
        emp.put("name", "WW");
        emp.put("age", 25);
        emp.put("salary", 30000.);
        emps.add(emp);
        emp = new HashMap<>();
        emp.put("name", "ZL");
        emp.put("age", 22);
        emp.put("salary", 50000.);
        emps.add(emp);
        return emps;
    }

    @RequestMapping("/test")
    @ResponseBody
    public String testPage(Model model, Page page) {
        System.out.println(model.getAttribute("page"));
        return "test";
    }

    @RequestMapping("/cookie/set")
    public @ResponseBody
    String setCookie(HttpServletResponse response) {
        // 設置cookie
        Cookie cookie = new Cookie("name", "value");
        // 設置 cookie 範圍
        // 使瀏覽器 只對某些地址發送cookie
        // 設置路徑後 在其路徑及其子路徑下生效
        cookie.setPath("/community/alpha");
        // 設置cookie 生命週期 單位: s
        // 默認是瀏覽器關閉後失效
        cookie.setMaxAge(60 * 10);
        response.addCookie(cookie);
        return "設置 cookie 成功";
    }

    @RequestMapping("/cookie/get")
    @ResponseBody
    public String getCookie(@CookieValue("name") String name) {
        System.out.println(name);
        return "get cookie success";
    }

    @RequestMapping(path = "/session/set", method = RequestMethod.GET)
    @ResponseBody
    public String setSession(HttpSession session) {
        // Session可以存任何數據
        session.setAttribute("id", 1);
        session.setAttribute("name", "test");
        return "set session success";
    }

    @RequestMapping(path = "/session/get", method = RequestMethod.GET)
    @ResponseBody
    public String getSession(HttpSession session) {
        // Session可以存任何數據
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));
        return "get session success";
    }

    @RequestMapping(path = "/ajax", method = RequestMethod.POST)
    @ResponseBody
    public String testAjax(String name, int age) {
        System.out.println(name);
        System.out.println(age);
        return CommunityUtil.getJsonString(200, "操作成功");
    }
}
