/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.dvdlibrary;

import com.sg.dvdlibrary.dao.DVDLibraryDAO;
import com.sg.dvdlibrary.models.DVD;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/**
 *
 * @author apprentice
 */
public class DAODbTest {

    DVDLibraryDAO dao;

    public DAODbTest() {
    }

    @Before
    public void setUp() {
        ApplicationContext ctx
                = new ClassPathXmlApplicationContext("test-applicationContext.xml");
        dao = ctx.getBean("dvdMyMovieDb", DVDLibraryDAO.class);
        
        JdbcTemplate template = (JdbcTemplate) ctx.getBean("jdbcTemplate");
        
        template.execute("delete from MoviesxGenres");
        template.execute("delete from MoviesxActors");
        template.execute("delete from MoviesxWriters");
        template.execute("delete from Movies");
        template.execute("delete from Actors");
        template.execute("delete from Writers");
        template.execute("delete from Directors");
        template.execute("delete from Studios"); 

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
            dao.addDVD(testDVD);
        }

        DVD testDVD = new DVD();
        ArrayList<String> actorList = new ArrayList<>();
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
        dao.addDVD(testDVD);
    }

    @Test
    public void testRemoveDVD() {
        
        assertEquals(10, dao.getAllDVDs().size());
        
        DVD testDVD = new DVD();
        ArrayList<String> actorList = new ArrayList<>();
        ArrayList<String> writerList = new ArrayList<>();
        ArrayList<String> genreList = new ArrayList<>();
        actorList.add("Deleted Actor");
        actorList.add("Deleted Actor 2");
        writerList.add("Deleted Writer");
        genreList.add("DeleteGenre1");
        genreList.add("DeleteGenre2");

        testDVD.setTitle("Deleted Movie");
        testDVD.setDirector("Deleted Director");
        testDVD.setReleaseDate(1950);
        testDVD.setMpaaRating("R");
        testDVD.setStudio("Del Studio");
        testDVD.setUserRating(11);
        testDVD.setActorList(actorList);
        testDVD.setNotes("Del note");
        testDVD.setWriterList(writerList);
        testDVD.setGenres(genreList);
        testDVD.setTrailerURL("delTrailerURL");
        
        dao.addDVD(testDVD);
        
        assertEquals(11, dao.getAllDVDs().size());
        
        dao.removeDVD(testDVD.getId());
        
        assertEquals(10, dao.getAllDVDs().size());
        assertFalse(dao.getAllDVDs().stream().filter(d -> d.getTitle().equalsIgnoreCase("Deleted")).count() > 0);

        testDVD = new DVD();
        actorList = new ArrayList<>();
        writerList = new ArrayList<>();
        actorList.add("After Delete");
        testDVD.setTitle("TitleAD");
        testDVD.setDirector("DirectorAD");
        testDVD.setReleaseDate(2000);
        testDVD.setMpaaRating("R");
        testDVD.setStudio("StudioAD");
        testDVD.setUserRating(1);
        testDVD.setActorList(actorList);
        testDVD.setWriterList(writerList);
        testDVD.setNotes("blahblah");

        dao.addDVD(testDVD);

        assertEquals(11, dao.getAllDVDs().size());
        assertEquals("TitleAD", dao.viewDVD(testDVD.getId()).getTitle());
        assertFalse(dao.getAllDVDs().stream().filter(d -> d.getId() == 5).count() > 0);
    }

    @Test
    public void testUpdateDVD() {
        
        assertEquals(10, dao.getAllDVDs().size());
        List<DVD> dvdList = dao.getAllDVDs();
        
        DVD testDVD = dvdList.get(0);
        ArrayList<String> actorList = new ArrayList<>();
        actorList.add("Updated");
        testDVD.setTitle("TitleU");
        testDVD.setDirector("DirectorU");
        testDVD.setReleaseDate(2000);
        testDVD.setMpaaRating("R");
        testDVD.setStudio("StudioU");
        testDVD.setUserRating(1);
        testDVD.setActorList(actorList);
        testDVD.setNotes("yaba");

        dao.updateDVD(testDVD);

        assertEquals(10, dao.getAllDVDs().size());
        assertEquals("TitleU", dao.viewDVD(testDVD.getId()).getTitle());
        assertTrue(dao.getAllDVDs().stream().filter(d -> d.getTitle().equals("TitleU")).count() == 1);
    }

    @Test
    public void testTitleSort() {
        
        assertEquals(10, dao.getAllDVDs().size());
        
        ArrayList<DVD> sorted = dao.sortByTitle(true);
        assertEquals("Title9", sorted.get(0).getTitle());
        assertEquals("Title5", sorted.get(4).getTitle());
        assertEquals("Serious Movie", sorted.get(9).getTitle()); 
        
        sorted = new ArrayList<>();
        sorted = dao.sortByTitle(!true);
        assertEquals("Title9", sorted.get(9).getTitle());
        assertEquals("Title5", sorted.get(5).getTitle());
        assertEquals("Serious Movie", sorted.get(0).getTitle()); 
    }
    
    @Test
    public void testYearSort() {
        assertEquals(10, dao.getAllDVDs().size());
        
        ArrayList<DVD> sorted = dao.sortByYear(true);
        assertEquals("Title9", sorted.get(9).getTitle());
        assertEquals("Title5", sorted.get(5).getTitle());
        assertEquals("Serious Movie", sorted.get(0).getTitle());
        
        sorted = new ArrayList<>();
        sorted = dao.sortByYear(!true);
        assertEquals("Title9", sorted.get(0).getTitle());
        assertEquals("Title5", sorted.get(4).getTitle());
        assertEquals("Serious Movie", sorted.get(9).getTitle());
    }
    
    @Test
    public void testMPAASort() {
        assertEquals(10, dao.getAllDVDs().size());
        
        ArrayList<DVD> sorted = dao.sortByMPAARating(!true);
        assertEquals("Serious Movie", sorted.get(9).getTitle());
        
        sorted = dao.sortByMPAARating(true);
        assertEquals("Serious Movie", sorted.get(0).getTitle());
    }
    
    @Test
    public void testUserRatingSort() {
        assertEquals(10, dao.getAllDVDs().size());
        
        ArrayList<DVD> sorted = dao.sortByUserRating(!true);
        assertEquals("Title9", sorted.get(9).getTitle());
        assertEquals("Title5", sorted.get(5).getTitle());
        assertEquals("Serious Movie", sorted.get(0).getTitle());
        
        sorted = dao.sortByUserRating(true);
        assertEquals("Title9", sorted.get(0).getTitle());
        assertEquals("Title5", sorted.get(4).getTitle());
        assertEquals("Serious Movie", sorted.get(9).getTitle());
    }
    
    @Test
    public void testTitleSearch() {
        assertEquals(9, dao.getDVDsWithTitle("Title").size());
        assertEquals(1, dao.getDVDsWithTitle("Title1").size());
        assertEquals(0, dao.getDVDsWithTitle("Title11").size());
        assertEquals(1, dao.getDVDsWithTitle("Serious Movie").size());
        assertEquals(10, dao.getDVDsWithTitle("E").size());
    }
    
    @Test
    public void testYearSearch() {
        assertEquals(1, dao.getDVDsWithReleaseDate(2005).size());
    }
    
    @Test
    public void testGenreSearch() {
        assertEquals(9, dao.getDVDsWithGenre("Action").size());
        assertEquals(10, dao.getDVDsWithGenre("Comedy").size());
        assertEquals(1, dao.getDVDsWithGenre("Drama").size());
        assertEquals(0, dao.getDVDsWithGenre("D").size());
    }

    @Test
    public void testDirectorSearch() {
        assertEquals(9, dao.getDVDsWithDirector("Director").size());
        assertEquals(1, dao.getDVDsWithDirector("Director1").size());
        assertEquals(0, dao.getDVDsWithDirector("Director55").size());
        assertEquals(1, dao.getDVDsWithDirector("Charlie").size());
        assertEquals(10, dao.getDVDsWithDirector("D").size());
    }

    @Test
    public void testActorSearch() {
        assertEquals(9, dao.getDVDsWithActor("Bovine J").size());
        assertEquals(1, dao.getDVDsWithActor("Ryan J").size());
        assertEquals(9, dao.getDVDsWithActor("McPoyle").size());
        assertEquals(1, dao.getDVDsWithActor("Actor").size());
        assertEquals(10, dao.getDVDsWithActor("Ryan").size());
    }
    
    @Test
    public void testWriterSearch() {
        assertEquals(9, dao.getDVDsWithWriter("Writer").size());
        assertEquals(1, dao.getDVDsWithWriter("Writer 11").size());
        assertEquals(1, dao.getDVDsWithWriter("Oscar").size());
        assertEquals(10, dao.getDVDsWithWriter("W").size());
    }
    
    @Test
    public void testStudioSearch() {
        assertEquals(10, dao.getDVDsFromStudio("Studio").size());
        assertEquals(1, dao.getDVDsFromStudio("Studio1").size());
        assertEquals(10, dao.getDVDsFromStudio("D").size());
        assertEquals(1, dao.getDVDsFromStudio("D Studio").size());
    }
    
    @Test
    public void testMPAASearch() {
        assertEquals(9, dao.getDVDsWithMPAARating("PG").size());
        assertEquals(0, dao.getDVDsWithMPAARating("G").size());
        assertEquals(0, dao.getDVDsWithMPAARating("PG-13").size());
        assertEquals(1, dao.getDVDsWithMPAARating("R").size());
    }
    @Test
    public void testKeywordSearch() {
        assertEquals(10, dao.getDVDsByKeyword("Ryan").size());
        assertEquals(1, dao.getDVDsByKeyword("seriou").size());
        assertEquals(9, dao.getDVDsByKeyword("titl").size());
        assertEquals(0, dao.getDVDsByKeyword("studio").size());
        assertEquals(1, dao.getDVDsByKeyword("Oscar").size());
        assertEquals(10, dao.getDVDsByKeyword("D").size());
        assertEquals(1, dao.getDVDsByKeyword("Charlie").size());
        assertEquals(9, dao.getDVDsByKeyword("direct").size());
    }
}
