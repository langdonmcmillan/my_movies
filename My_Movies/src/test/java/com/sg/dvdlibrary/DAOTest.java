/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.dvdlibrary;

import com.sg.dvdlibrary.dao.DVDInMemImpl;
import com.sg.dvdlibrary.dao.DVDLibraryDAO;
import com.sg.dvdlibrary.models.DVD;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author apprentice
 */
public class DAOTest {

    private DVDLibraryDAO dvdDAO;
    private HashMap<Integer, DVD> tempDVDLibrary;

    public DAOTest() {
        ApplicationContext ctx = 
                new ClassPathXmlApplicationContext("test-applicationContext.xml");
        dvdDAO = ctx.getBean("dvdLibraryDAO", DVDLibraryDAO.class);
        tempDVDLibrary = new HashMap<>();
    }

    @Before
    public void setUp() {
        for (DVD dvd: dvdDAO.getAllDVDs()) {
            tempDVDLibrary.put(dvd.getId(), dvd);
        }
        for (DVD dvd: dvdDAO.getAllDVDs()) {
            dvdDAO.removeDVD(dvd.getId());
        }
        
        
        for (int i = 9; i > 0; i--) {
            DVD testDVD = new DVD();
            ArrayList<String> actorList = new ArrayList<>();
            ArrayList<String> writerList = new ArrayList<>();
            ArrayList<String> genreList = new ArrayList<>();
            actorList.add("Bovine Joni" + i);
            actorList.add("Ryan McPoyle" + i);
            actorList.add("Liam McPoyle" + i);
            writerList.add("Writer 1" + i);
            writerList.add("Writer 2" + i);
            genreList.add("Action");
            genreList.add("Comedy");
            
            testDVD.setTitle("Title" + i);
            testDVD.setDirector("Director" + i);
            testDVD.setReleaseDate(2000 + i);
            testDVD.setMpaaRating("PG");
            testDVD.setStudio("Studio" + i);
            testDVD.setUserRating(10 - i);
            testDVD.setActorList(actorList);
            testDVD.setNotes("TEST NOTES");
            testDVD.setWriterList(writerList);
            testDVD.setGenres(genreList);
            testDVD.setTrailerURL("trailerURL");
            dvdDAO.addDVD(testDVD);
        }
        
        DVD testDVD = new DVD();
        ArrayList<String> actorList = new ArrayList<>();
        ArrayList<String> noteList = new ArrayList<>();
        ArrayList<String> writerList = new ArrayList<>();
        ArrayList<String> genreList = new ArrayList<>();
        actorList.add("Drama Actor");
        actorList.add("Ryan Jacobs");
        writerList.add("Oscar Winner");
        genreList.add("Drama");
        genreList.add("Comedy");

        testDVD.setTitle("Serious Movie");
        testDVD.setDirector("Charlie Davies");
        testDVD.setReleaseDate(1950);
        testDVD.setMpaaRating("R");
        testDVD.setStudio("D Studio");
        testDVD.setUserRating(11);
        testDVD.setActorList(actorList);
        testDVD.setNotes("NOTEYNOTE");
        testDVD.setWriterList(writerList);
        testDVD.setGenres(genreList);
        testDVD.setTrailerURL("dramaTrailerURL");
        dvdDAO.addDVD(testDVD);
    }

    @After
    public void reset() {
        for (DVD dvd: dvdDAO.getAllDVDs()) {
            dvdDAO.removeDVD(dvd.getId());
        }
        for (int id: tempDVDLibrary.keySet()) {
            dvdDAO.addDVD(tempDVDLibrary.get(id));
        }
    }

    @Test
    public void testRemoveDVD() {
        dvdDAO.removeDVD(5);

        assertEquals(9, dvdDAO.getAllDVDs().size());
        assertEquals("Title9", dvdDAO.viewDVD(1).getTitle());
        assertFalse(dvdDAO.getAllDVDs().stream().filter(d -> d.getId() == 5).count() > 0);

        DVD testDVD = new DVD();
        ArrayList<String> actorList = new ArrayList<>();
        ArrayList<String> noteList = new ArrayList<>();
        actorList.add("After Delete");
        noteList.add("Test Note After Delete");
        testDVD.setTitle("TitleAD");
        testDVD.setDirector("DirectorAD");
        testDVD.setReleaseDate(2000);
        testDVD.setMpaaRating("R");
        testDVD.setStudio("StudioAD");
        testDVD.setUserRating(1);
        testDVD.setActorList(actorList);
        testDVD.setNotes("blahblah");

        dvdDAO.addDVD(testDVD);

        assertEquals(10, dvdDAO.getAllDVDs().size());
        assertEquals("Title9", dvdDAO.viewDVD(1).getTitle());
        assertEquals("TitleAD", dvdDAO.viewDVD(11).getTitle());
        assertFalse(dvdDAO.getAllDVDs().stream().filter(d -> d.getId() == 5).count() > 0);
    }

    @Test
    public void testUpdateDVD() {
        DVD testDVD = dvdDAO.viewDVD(5);
        ArrayList<String> actorList = new ArrayList<>();
        ArrayList<String> noteList = new ArrayList<>();
        actorList.add("Updated");
        noteList.add("Test Note Updated");
        testDVD.setTitle("TitleU");
        testDVD.setDirector("DirectorU");
        testDVD.setReleaseDate(2000);
        testDVD.setMpaaRating("R");
        testDVD.setStudio("StudioU");
        testDVD.setUserRating(1);
        testDVD.setActorList(actorList);
        testDVD.setNotes("yaba");

        dvdDAO.updateDVD(testDVD);

        assertEquals(10, dvdDAO.getAllDVDs().size());
        assertEquals("TitleU", dvdDAO.viewDVD(5).getTitle());
        assertTrue(dvdDAO.getAllDVDs().stream().filter(d -> d.getId() == 5).count() == 1);
    }

    @Test
    public void testGetAllDVDs() {
        assertTrue(dvdDAO.getAllDVDs().size() == 10);
    }

    @Test
    public void testTitleSort() {
        assertEquals(10, dvdDAO.getAllDVDs().size());
        ArrayList<DVD> sorted = dvdDAO.sortByTitle(true);
        assertEquals("Title9", sorted.get(0).getTitle());
        assertEquals("Title5", sorted.get(4).getTitle());
        assertEquals("Serious Movie", sorted.get(9).getTitle());
    }

    @Test
    public void testYearSort() {
        assertEquals(10, dvdDAO.getAllDVDs().size());
        ArrayList<DVD> sorted = dvdDAO.sortByYear(true);
        assertEquals("Title9", sorted.get(9).getTitle());
        assertEquals("Title5", sorted.get(5).getTitle());
        assertEquals("Serious Movie", sorted.get(0).getTitle());
    }

    @Test
    public void testMPAASort() {
        assertEquals(10, dvdDAO.getAllDVDs().size());
        ArrayList<DVD> sorted = dvdDAO.sortByMPAARating(true);
        assertEquals("Serious Movie", sorted.get(0).getTitle());
    }
    
    @Test
    public void testUserRatingSort() {
        assertEquals(10, dvdDAO.getAllDVDs().size());
        ArrayList<DVD> sorted = dvdDAO.sortByUserRating(true);
        assertEquals("Title9", sorted.get(0).getTitle());
        assertEquals("Title5", sorted.get(4).getTitle());
        assertEquals("Serious Movie", sorted.get(9).getTitle());
    }

    @Test
    public void testTitleSearch() {
        assertEquals(9, dvdDAO.getDVDsWithTitle("Title").size());
        assertEquals(1, dvdDAO.getDVDsWithTitle("Title1").size());
        assertEquals(0, dvdDAO.getDVDsWithTitle("Title11").size());
        assertEquals(1, dvdDAO.getDVDsWithTitle("Serious Movie").size());
        assertEquals(10, dvdDAO.getDVDsWithTitle("E").size());
    }
    
    @Test
    public void testYearSearch() {
        assertEquals(1, dvdDAO.getDVDsWithReleaseDate(2005).size());
    }
    
    @Test
    public void testGenreSearch() {
        assertEquals(9, dvdDAO.getDVDsWithGenre("Action").size());
        assertEquals(10, dvdDAO.getDVDsWithGenre("Comedy").size());
        assertEquals(1, dvdDAO.getDVDsWithGenre("Drama").size());
        assertEquals(0, dvdDAO.getDVDsWithGenre("D").size());
    }

    @Test
    public void testDirectorSearch() {
        assertEquals(9, dvdDAO.getDVDsWithDirector("Director").size());
        assertEquals(1, dvdDAO.getDVDsWithDirector("Director1").size());
        assertEquals(0, dvdDAO.getDVDsWithDirector("Director55").size());
        assertEquals(1, dvdDAO.getDVDsWithDirector("Charlie").size());
        assertEquals(10, dvdDAO.getDVDsWithDirector("D").size());
    }

    @Test
    public void testActorSearch() {
        assertEquals(9, dvdDAO.getDVDsWithActor("Bovine J").size());
        assertEquals(1, dvdDAO.getDVDsWithActor("Ryan J").size());
        assertEquals(9, dvdDAO.getDVDsWithActor("McPoyle").size());
        assertEquals(1, dvdDAO.getDVDsWithActor("Actor").size());
        assertEquals(10, dvdDAO.getDVDsWithActor("Ryan").size());
    }
    
    @Test
    public void testWriterSearch() {
        assertEquals(9, dvdDAO.getDVDsWithWriter("Writer").size());
        assertEquals(1, dvdDAO.getDVDsWithWriter("Writer 11").size());
        assertEquals(1, dvdDAO.getDVDsWithWriter("Oscar").size());
        assertEquals(10, dvdDAO.getDVDsWithWriter("W").size());
    }
    
    public void testStudioSearch() {
        assertEquals(10, dvdDAO.getDVDsWithWriter("Studio").size());
        assertEquals(1, dvdDAO.getDVDsWithWriter("Studio 1").size());
        assertEquals(10, dvdDAO.getDVDsWithWriter("D").size());
        assertEquals(1, dvdDAO.getDVDsWithWriter("D Studio").size());
    }
    public void testMPAASearch() {
        assertEquals(9, dvdDAO.getDVDsWithWriter("PG").size());
        assertEquals(0, dvdDAO.getDVDsWithWriter("G").size());
        assertEquals(0, dvdDAO.getDVDsWithWriter("PG-13").size());
        assertEquals(1, dvdDAO.getDVDsWithWriter("R").size());
    }
    public void testKeywordSearch() {
        assertEquals(10, dvdDAO.getDVDsWithWriter("Ryan").size());
        assertEquals(1, dvdDAO.getDVDsWithWriter("seriou").size());
        assertEquals(9, dvdDAO.getDVDsWithWriter("titl").size());
        assertEquals(0, dvdDAO.getDVDsWithWriter("studio").size());
        assertEquals(1, dvdDAO.getDVDsWithWriter("Oscar").size());
        assertEquals(10, dvdDAO.getDVDsWithWriter("D").size());
        assertEquals(1, dvdDAO.getDVDsWithWriter("Charlie").size());
        assertEquals(9, dvdDAO.getDVDsWithWriter("direct").size());
    }
}
