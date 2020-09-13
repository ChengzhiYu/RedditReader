/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import common.FileUtility;
import entity.Board;
import entity.Image;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logic.BoardLogic;
import logic.ImageLogic;
import logic.LogicFactory;
import reddit.Post;
import reddit.Reddit;
import reddit.Sort;

/**
 *
 * @author Chengzhi
 */
@WebServlet(name = "ImageView", urlPatterns = {"/ImageView"})
public class ImageView extends HttpServlet{
    ImageLogic logic = LogicFactory.getFor("Image");
    
    private String errorMessage = null;
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>ImageViewNormal</title>");
            out.println("</head>");
            out.println("<body>");

            out.println("<div align=\"center\">");
            out.println("<div align=\"center\" class=\" imageContainer\">");
            
            List<Image> entities = logic.getAll();
            
            entities.forEach(e->{
                String url = FileUtility.getFileName(e.getUrl());
                out.println("<div align = \"center\"><div align=\"center\" class=\"imageContainer\">");
                out.printf("<img class=\"imageThumb\" src=\"image/%s\"/>",url );
                out.println("</div></div><br>");
            });
            
            out.println("</div>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
        }
    }
    
     @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        FileUtility.createDirectory(System.getProperty("user.home") + "/My Documents/Reddit Images/" );
        BoardLogic bLogic = LogicFactory.getFor("Board");
        Board board = bLogic.getWithId(3);
        
         Consumer<Post> saveImage = (Post post) -> {
            if (post.isImage() && !post.isOver18()) {
                String path = post.getUrl();
                if (logic.getImageWithUrl(path)==null){                  
                FileUtility.downloadAndSaveFile(path, System.getProperty("user.home") + "/My Documents/Reddit Images/");
                               
                Map<String, String[]> sampleMap = new HashMap<>();
                sampleMap.put(logic.TITLE, new String[]{post.getTitle()});
                sampleMap.put(logic.URL, new String[]{post.getUrl()});
                sampleMap.put(logic.LOCAL_PATH, new String[]{FileUtility.getFileName(post.getUrl())});
                sampleMap.put(logic.DATE, new String[]{logic.convertDate(post.getDate())});
                

                Image image = logic.createEntity(sampleMap);
                image.setBoard(board);

                logic.add(image);
                }else{
                //if duplicate print the error message
                errorMessage = "Url: \"" + path + "\" already exists";
                }
                
            }
        };

        Reddit reddit = new Reddit();
                reddit.authenticate().buildRedditPagesConfig(board.getName(), 5, Sort.BEST);
                reddit.requestNextPage().proccessNextPage(saveImage);
                
         processRequest(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
       processRequest(request, response);
    }
    
    
}
