/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import entity.Host;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logic.HostLogic;
import logic.LogicFactory;

/**
 *
 * @author  Chengzhi
 */
@WebServlet(name = "CreateHost", urlPatterns = {"/CreateHost"})
public class CreateHost extends HttpServlet {
    private String errorMessage = null;
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Create Host</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div style=\"text-align: center;\">");
            out.println("<div style=\"display: inline-block; text-align: left;\">");
            out.println("<form method=\"post\">");
            out.println("Name:<br>");
            
            out.printf("<input type=\"text\" name=\"%s\" value=\"\"><br>",HostLogic.NAME);
            out.println("<br>");
            out.println("Url:<br>");
            out.printf("<input type=\"text\" name=\"%s\" value=\"\"><br>",HostLogic.URL);
            out.println("<br>");
            out.println("Extraction Type:<br>");
            out.printf("<input type=\"text\" name=\"%s\" value=\"\"><br>",HostLogic.EXTRACTION_TYPE);
            out.println("<br>");
            out.println("<input type=\"submit\" name=\"view\" value=\"Add and View\">");
            out.println("<input type=\"submit\" name=\"add\" value=\"Add\">");
            out.println("</form>");
            if(errorMessage!=null&&!errorMessage.isEmpty()){
                out.println("<p color=red>");
                out.println("<font color=red size=4px>");
                out.println(errorMessage);
                out.println("</font>");
                out.println("</p>");
            }
            out.println("<pre>");
            out.println("Submitted keys and values:");
            out.println(toStringMap(request.getParameterMap()));
            out.println("</pre>");
            out.println("</div>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
        }
    }
    
    private String toStringMap(Map<String, String[]> values) {
        StringBuilder builder = new StringBuilder();
        values.forEach((k, v) -> builder.append("Key=").append(k)
                .append(", ")
                .append("Value/s=").append(Arrays.toString(v))
                .append(System.lineSeparator()));
        return builder.toString();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log("GET");
        processRequest(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log("POST");
        
        HostLogic hLogic = LogicFactory.getFor("Host");
        String name = request.getParameter(HostLogic.NAME);
        if(hLogic.getHostWithName(name)==null){
            Host host = hLogic.createEntity( request.getParameterMap());

            hLogic.add(host);
        }else{

            errorMessage = "Name: \"" + name + "\" already exists";
        }
        if( request.getParameter("add")!=null){
 
            processRequest(request, response);
        }else if (request.getParameter("view")!=null) {
            
            response.sendRedirect("HostTable");
        }
    }
}
