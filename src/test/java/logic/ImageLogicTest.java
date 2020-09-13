/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import entity.Image;
import common.TomcatStartUp;
import common.ValidationException;
import dal.EMFactory;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import javax.persistence.EntityManager;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
/**
 *
 * @author Wenbo
 */
public class ImageLogicTest {
    private ImageLogic logic;
    private BoardLogic bLogic;
    private Image expectedImage;
    
    public static final SimpleDateFormat  FORMATTER = new  SimpleDateFormat("YYYY-MM-DD hh:mm:ss");
     
    Date date = new Date();  

    
    @BeforeAll
    final static void setUpBeforeClass() throws Exception {
        TomcatStartUp.createTomcat("/RedditReader", "common.ServletListener");
    }

    @AfterAll
    final static void tearDownAfterClass() throws Exception {
        TomcatStartUp.stopAndDestroyTomcat();
    }

    @BeforeEach
    final void setUp() throws Exception {
        
        logic = LogicFactory.getFor( "Image");
        bLogic = LogicFactory.getFor( "Board");
        
        Image image = new Image();
        image.setTitle("Junit 5 Test");
        image.setUrl("junit");
        image.setLocalPath("junit5");
        image.setDate(date);
        image.setBoard( bLogic.getWithId(2));

     
        EntityManager em = EMFactory.getEMF().createEntityManager();
  
        em.getTransaction().begin();
        expectedImage = em.merge(image);
        Hibernate.initialize(expectedImage.getBoard());
        em.getTransaction().commit();
        em.close();

    }

    @AfterEach
    final void tearDown() throws Exception {
        if (expectedImage != null) {
            logic.delete(expectedImage);
        }
    }

    @Test
    final void testGetAll() {
       
        List<Image> list = logic.getAll();
       
        int originalSize = list.size();

        
        assertNotNull(expectedImage);
        
        logic.delete(expectedImage);

       
        list = logic.getAll();
        
        assertEquals(originalSize - 1, list.size());
    }

    
    private void assertImageEquals(Image expected, Image actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getUrl(), actual.getUrl());
        assertEquals(expected.getLocalPath(), actual.getLocalPath());
        assertTrue(Math.abs(expected.getDate().getTime()-actual.getDate().getTime())<2000);
//        assertEquals(FORMATTER.format(expected.getDate()), FORMATTER.format(actual.getDate()));
        assertEquals(expected.getBoard(), actual.getBoard());
    }

    @Test
    final void testGetWithId() {
      
        Image returnedImage = logic.getWithId(expectedImage.getId());
        
       
        assertImageEquals(expectedImage, returnedImage);
    }

//    @Test
//    final void testGetImagesWithBoardId() {
//        Image returnedImage = (Image) logic.getImagesWithBoardId(expectedImage.getBoard().getId());
//
//       
//        assertImageEquals(expectedImage, returnedImage);
//    }

//    @Test
//    final void testGetImagesWithTitle() {
//        Image returnedImage =   logic.getImagesWithTitle(expectedImage.getTitle());
//
//       
//        assertImageEquals(expectedImage, returnedImage);
//    }
    
    @Test
    final void testGetWithUrl() {
        Image returnedImage = logic.getImageWithUrl(expectedImage.getUrl());

       
        assertImageEquals(expectedImage, returnedImage);
    }
    
    @Test
    final void testGetWithLocalPath() {
        Image returnedImage =  logic.getImageWithLocalPath(expectedImage.getLocalPath());

       
        assertImageEquals(expectedImage, returnedImage);
    }
    
//    @Test
//    final void testGetImagesWithDate() {
//        Image returnedImage = (Image) logic.getImagesWithDate(expectedImage.getDate());
//
//       
//        assertImageEquals(expectedImage, returnedImage);
//    }

    
    

//    @Test
//    final void testSearch() {
//        int foundFull = 0;
//        
//        String searchString = expectedImage.getTitle().substring(3);
//       
//        List<Image> returnedImage = logic.search(searchString);
//        for (Image image : returnedImage) {
//            
//            assertTrue(image.getTitle().contains(searchString));
//            
//            if (image.getId().equals(expectedImage.getId())) {
//                assertImageEquals(expectedImage, image);
//                foundFull++;
//            }
//        }
//        assertEquals(1, foundFull, "if zero means not found, if more than one means duplicate");
//    }

    @Test
    final void testCreateEntityAndAdd() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put(ImageLogic.URL, new String[]{"Test Create Entity"});
        sampleMap.put(ImageLogic.TITLE, new String[]{"testCreateAccount"});
        sampleMap.put(ImageLogic.DATE, new String[]{"2020/06/10"});
        sampleMap.put(ImageLogic.LOCAL_PATH, new String[]{"C:"});
        sampleMap.put(ImageLogic.BOARD_ID, new String[]{"1"});

        Image returnedImage = logic.createEntity(sampleMap);
        logic.add(returnedImage);

        returnedImage = (Image) logic.getImagesWithTitle(returnedImage.getTitle());

        assertEquals(sampleMap.get(ImageLogic.URL)[0], returnedImage.getUrl());
        assertEquals(sampleMap.get(ImageLogic.TITLE)[0], returnedImage.getTitle());
        assertEquals(sampleMap.get(ImageLogic.DATE)[0], returnedImage.getDate());
        assertEquals(sampleMap.get(ImageLogic.LOCAL_PATH)[0], returnedImage.getLocalPath());
        assertEquals(sampleMap.get(ImageLogic.BOARD_ID)[0], returnedImage.getBoard().toString());

        logic.delete(returnedImage);
    }

    @Test
    final void testCreateEntity() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put(ImageLogic.ID, new String[]{Integer.toString(expectedImage.getId())});
        sampleMap.put(ImageLogic.URL, new String[]{expectedImage.getUrl()});
        sampleMap.put(ImageLogic.TITLE, new String[]{expectedImage.getTitle()});
        sampleMap.put(ImageLogic.DATE, new String[]{FORMATTER.format(expectedImage.getDate())});
        sampleMap.put(ImageLogic.LOCAL_PATH, new String[]{expectedImage.getLocalPath()});

        Image returnedImage = logic.createEntity(sampleMap);

        assertImageEquals(expectedImage, returnedImage);
    }

    @Test
    final void testCreateEntityNullAndEmptyValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = (Map<String, String[]> map) -> {
            map.clear();
            map.put(ImageLogic.ID, new String[]{Integer.toString(expectedImage.getId())});
            map.put(ImageLogic.URL, new String[]{expectedImage.getUrl()});
            map.put(ImageLogic.TITLE, new String[]{expectedImage.getTitle()});
            map.put(ImageLogic.DATE, new String[]{expectedImage.getDate().toString()});
            map.put(ImageLogic.LOCAL_PATH, new String[]{expectedImage.getLocalPath()});
            map.put(ImageLogic.BOARD_ID, new String[]{expectedImage.getBoard().toString()});
        };

       
        fillMap.accept(sampleMap);
        sampleMap.replace(ImageLogic.ID, null);
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(ImageLogic.ID, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(ImageLogic.URL, null);
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(ImageLogic.URL, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(ImageLogic.TITLE, null);
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(ImageLogic.TITLE, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(ImageLogic.DATE, null);
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(ImageLogic.DATE, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap));
        
        fillMap.accept(sampleMap);
        sampleMap.replace(ImageLogic.LOCAL_PATH, null);
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(ImageLogic.LOCAL_PATH, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap));
        
        fillMap.accept(sampleMap);
        sampleMap.replace(ImageLogic.BOARD_ID, null);
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(ImageLogic.BOARD_ID, new String[]{});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
    }

    @Test
    final void testCreateEntityBadLengthValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = (Map<String, String[]> map) -> {
            map.clear();
            map.put(ImageLogic.ID, new String[]{Integer.toString(expectedImage.getId())});
            map.put(ImageLogic.URL, new String[]{expectedImage.getUrl()});
            map.put(ImageLogic.TITLE, new String[]{expectedImage.getTitle()});
            map.put(ImageLogic.DATE, new String[]{expectedImage.getDate().toString()});
            map.put(ImageLogic.LOCAL_PATH, new String[]{expectedImage.getLocalPath()});
        };

        IntFunction<String> generateString = (int length) -> {
            //https://www.baeldung.com/java-random-string#java8-alphabetic
            return new Random().ints('a', 'z' + 1).limit(length)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
        };

      
        fillMap.accept(sampleMap);
        sampleMap.replace(ImageLogic.ID, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(ImageLogic.ID, new String[]{"12b"});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(ImageLogic.URL, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(ImageLogic.URL, new String[]{generateString.apply(46)});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(ImageLogic.TITLE, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(ImageLogic.TITLE, new String[]{generateString.apply(46)});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(ImageLogic.DATE, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(ImageLogic.DATE, new String[]{generateString.apply(46)});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        
        fillMap.accept(sampleMap);
        sampleMap.replace(ImageLogic.LOCAL_PATH, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(ImageLogic.LOCAL_PATH, new String[]{generateString.apply(46)});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
    }

    @Test
    final void testCreateEntityEdgeValues() {
        IntFunction<String> generateString = (int length) -> {
            //https://www.baeldung.com/java-random-string#java8-alphabetic
            return new Random().ints('a', 'z' + 1).limit(length)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
        };

        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put(ImageLogic.ID, new String[]{Integer.toString(1)});
        sampleMap.put(ImageLogic.URL, new String[]{generateString.apply(1)});
        sampleMap.put(ImageLogic.TITLE, new String[]{generateString.apply(1)});
        sampleMap.put(ImageLogic.DATE, new String[]{generateString.apply(1)});
        sampleMap.put(ImageLogic.LOCAL_PATH, new String[]{generateString.apply(1)});

       
        Image returnedImage = logic.createEntity(sampleMap);
        assertEquals(Integer.parseInt(sampleMap.get(AccountLogic.ID)[0]), returnedImage.getId());
        assertEquals(sampleMap.get(ImageLogic.URL)[0], returnedImage.getUrl());
        assertEquals(sampleMap.get(ImageLogic.TITLE)[0], returnedImage.getTitle());
        assertEquals(sampleMap.get(ImageLogic.DATE)[0], returnedImage.getDate());
        assertEquals(sampleMap.get(ImageLogic.LOCAL_PATH)[0], returnedImage.getLocalPath());

        sampleMap = new HashMap<>();
        sampleMap.put(ImageLogic.ID, new String[]{Integer.toString(1)});
        sampleMap.put(ImageLogic.URL, new String[]{generateString.apply(45)});
        sampleMap.put(ImageLogic.TITLE, new String[]{generateString.apply(45)});
        sampleMap.put(ImageLogic.DATE, new String[]{generateString.apply(45)});
        sampleMap.put(ImageLogic.LOCAL_PATH, new String[]{generateString.apply(45)});

        //idealy every test should be in its own method
        returnedImage = logic.createEntity(sampleMap);
        assertEquals(Integer.parseInt(sampleMap.get(AccountLogic.ID)[0]), returnedImage.getId());
        assertEquals(sampleMap.get(ImageLogic.URL)[0], returnedImage.getUrl());
        assertEquals(sampleMap.get(ImageLogic.TITLE)[0], returnedImage.getTitle());
        assertEquals(sampleMap.get(ImageLogic.DATE)[0], returnedImage.getDate());
        assertEquals(sampleMap.get(ImageLogic.LOCAL_PATH)[0], returnedImage.getLocalPath());
    }

    @Test
    final void testGetColumnNames() {
        List<String> list = logic.getColumnNames();
        assertEquals(Arrays.asList("ID", "BoardId", "Title", "Url",  "LocalPath", "Date"), list);
    }

    @Test
    final void testGetColumnCodes() {
        List<String> list = logic.getColumnCodes();
        assertEquals(Arrays.asList(ImageLogic.ID, ImageLogic.BOARD_ID, ImageLogic.TITLE, ImageLogic.URL, ImageLogic.LOCAL_PATH, ImageLogic.DATE ), list);
    }

    @Test
    final void testExtractDataAsList() {
        List<?> list = logic.extractDataAsList(expectedImage);
        assertEquals(expectedImage.getId(), list.get(0));
        assertEquals(expectedImage.getBoard().getId(), list.get(1));
        assertEquals(expectedImage.getTitle(), list.get(2));
        assertEquals(expectedImage.getUrl(), list.get(3));
        assertEquals(expectedImage.getLocalPath(), list.get(4));
        assertEquals(expectedImage.getDate(), list.get(5));
        
        
        
    }
}
