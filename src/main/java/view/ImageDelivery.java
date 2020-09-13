/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Chengzhi
 */
@WebServlet(name = "ImageDelivery", urlPatterns = {"/image/*"})
public class ImageDelivery extends HttpServlet{
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        String imageDirectory = System.getProperty("user.home");
        String fileName = req.getPathInfo();
        File file = new File (imageDirectory + "/My Documents/Reddit Images", fileName);
        resp.setHeader("Content-Type", getServletContext().getMimeType(fileName));
        resp.setHeader("Content-Length", String.valueOf(file.length()));
        resp.setHeader("Content-Disposition", "inline;fileName\"" + fileName +"\"");
        Files.copy(file.toPath(), resp.getOutputStream());
    } 
}
