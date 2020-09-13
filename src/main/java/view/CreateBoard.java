/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import entity.Board;
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
import logic.BoardLogic;
import logic.HostLogic;
import logic.LogicFactory;

/**
 *
 * @author Chengzhi
 */
@WebServlet(name = "CreateBoard", urlPatterns = {"/CreateBoard"})
public class CreateBoard extends HttpServlet {
    private String errorMessage = null;
    private HostLogic hlogic = LogicFactory.getFor("Host");
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {

            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Create Board</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div style=\"text-align: center;\">");
            out.println("<div style=\"display: inline-block; text-align: left;\">");
            out.println("<form method=\"post\">");
            out.println("Host Id:<br>");

            out.printf("<select name=\"%s\">", BoardLogic.HOST_ID);
            hlogic.getAll().forEach (host->
                    out.printf("<option value=\"%d\">%s</option>", host.getId(),host.getName())
            );
            out.printf("</select>");
            out.println("<br>");
            out.println("Url:<br>");
            out.printf("<input type=\"text\" name=\"%s\" value=\"\"><br>",BoardLogic.URL);
            out.println("<br>");
            out.println("Name:<br>");
            out.printf("<input type=\"text\" name=\"%s\" value=\"\"><br>",BoardLogic.NAME);
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
        
        BoardLogic bLogic = LogicFactory.getFor("Board");
        String url = request.getParameter(BoardLogic.URL);
        if(bLogic.getBoardWithUrl(url)==null){
            HostLogic hLogic = LogicFactory.getFor("Host");
            Board board = bLogic.createEntity( request.getParameterMap());
            Host host = hLogic.getWithId(Integer.parseInt(request.getParameter(BoardLogic.HOST_ID)));
            board.setHostid(host);
            bLogic.add(board);
        }else{
           
            errorMessage = "Host Url: \"" + url + "\" already exists";
        }
        if( request.getParameter("add")!=null){
 
            processRequest(request, response);
        }else if (request.getParameter("view")!=null) {
            
            response.sendRedirect("BoardTable");
        }
    }
    
    @Override
    public String getServletInfo() {
        return "Create a Board Entity";
    }

    private static final boolean DEBUG = true;

    public void log( String msg) {
        if(DEBUG){
            String message = String.format( "[%s] %s", getClass().getSimpleName(), msg);
            getServletContext().log( message);
        }
    }

    public void log( String msg, Throwable t) {
        String message = String.format( "[%s] %s", getClass().getSimpleName(), msg);
        getServletContext().log( message, t);
    }
}
