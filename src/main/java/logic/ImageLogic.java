/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import common.ValidationException;
import dal.ImageDAL;
import entity.Image;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.ObjIntConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Shariar (Shawn) Emami, Chengzhi
 */
public class ImageLogic extends GenericLogic<Image, ImageDAL>{

    public static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final String ID = "id";
    public static final String URL = "url";
    public static final String TITLE = "title";
    public static final String DATE = "date";
    public static final String LOCAL_PATH = "local_path";
    public static final String BOARD_ID = "Board_id";

    
    ImageLogic(){
        super(new ImageDAL());
    }
    
    @Override
    public List<Image> getAll() {
        return get(()->dal().findAll());
    }

    @Override
    public Image getWithId(int id) {
        return get(()->dal().findById(id));
    }
    
    public List<Image> getImagesWithBoardId(int boardId){
        return get(()-> dal().findByBoardId(boardId));
    }
    
    public List<Image> getImagesWithTitle(String title){
        return get(()->dal().findByTitle(title));
    }
    
    public Image getImageWithUrl(String url){
        return get(()->dal().findByUrl(url));
    }
    
    public Image getImageWithLocalPath(String path){
        return get(()->dal().findByLocalPath(path));
    }
    
    public List<Image> getImagesWithDate(Date date){
        return get(()->dal().findByDate(date));
    }
    
    public String convertDate(Date date){
        return FORMATTER.format(date);
    }
    
    @Override
    public Image createEntity(Map<String, String[]> parameterMap) {
        Objects.requireNonNull(parameterMap, "parameterMap cannot be null");
        
        Image entity = new Image();
        if(parameterMap.containsKey(ID)){
            try{
                entity.setId(Integer.parseInt(parameterMap.get(ID)[0]));
            } catch (java.lang.NumberFormatException ex) {
                throw new ValidationException(ex);
            }
        }
        
        ObjIntConsumer< String> validator = (value, length) -> {
            if (value == null || value.trim().isEmpty() || value.length() > length) {
                throw new ValidationException("value cannot be null, empty or larger than " + length + " characters");
            }
        };
        
        String title = parameterMap.get(TITLE)[0];
        String url = parameterMap.get(URL)[0];
        String localPath = parameterMap.get(LOCAL_PATH)[0];
        String date = parameterMap.get(DATE)[0];
        
        validator.accept(title, 1000);
        validator.accept(url, 255);
        validator.accept(localPath, 255);
        
        entity.setTitle(title);
        entity.setUrl(url);
        entity.setLocalPath(localPath);
        try {
            entity.setDate(FORMATTER.parse(date));
        } catch (ParseException ex) {
            entity.setDate(new Date());
            throw new ValidationException(ex);
        }
        
        return entity;

}
    
    
    @Override
    public List<String> getColumnNames() {
        return Arrays.asList("ID", "BoardId", "Title", "Url", "LocalPath", "Date");
    }

    @Override
    public List<String> getColumnCodes() {
        return Arrays.asList(ID, BOARD_ID, TITLE, URL, LOCAL_PATH, DATE);
    }

    @Override
    public List<?> extractDataAsList(Image e) {
        return Arrays.asList(e.getId(), e.getBoard().getId(), e.getTitle(), e.getUrl(), e.getLocalPath(), e.getDate());
    }

  
    
    
}
