/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.mymovies;

import com.sg.mymovies.models.Movie;
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
import com.sg.mymovies.dao.MyMoviesDAO;

/**
 *
 * @author apprentice
 */
public class MyMoviesDAOTest {

    MyMoviesDAO dao;

    public MyMoviesDAOTest() {
    }

    @Before
    public void setUp() {
        ApplicationContext ctx
                = new ClassPathXmlApplicationContext("test-applicationContext.xml");
        dao = ctx.getBean("MyMovieDb", MyMoviesDAO.class);
        
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
            Movie testMovie = new Movie();
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

            testMovie.setTitle("Title" + i);
            testMovie.setDirector("Director" + i);
            testMovie.setReleaseDate(2000 + i);
            testMovie.setMpaaRating("PG");
            testMovie.setStudio("Studio" + i);
            testMovie.setUserRating(10 - i);
            testMovie.setActorList(actorList);
            testMovie.setNotes("TEST NOTES");
            testMovie.setWriterList(writerList);
            testMovie.setGenres(genreList);
            testMovie.setTrailerURL("trailerURL");
            dao.addMovie(testMovie);
        }

        Movie testMovie = new Movie();
        ArrayList<String> actorList = new ArrayList<>();
        ArrayList<String> writerList = new ArrayList<>();
        ArrayList<String> genreList = new ArrayList<>();
        actorList.add("Drama Actor");
        actorList.add("Ryan Jacobs");
        writerList.add("Oscar Winner");
        genreList.add("Drama");
        genreList.add("Comedy");

        testMovie.setTitle("Serious Movie");
        testMovie.setDirector("Charlie Davies");
        testMovie.setReleaseDate(1950);
        testMovie.setMpaaRating("R");
        testMovie.setStudio("D Studio");
        testMovie.setUserRating(11);
        testMovie.setActorList(actorList);
        testMovie.setNotes("NOTEYNOTE");
        testMovie.setWriterList(writerList);
        testMovie.setGenres(genreList);
        testMovie.setTrailerURL("dramaTrailerURL");
        dao.addMovie(testMovie);
    }

    @Test
    public void testRemoveMovie() {
        
        assertEquals(10, dao.getAllMovies().size());
        
        Movie testMovie = new Movie();
        ArrayList<String> actorList = new ArrayList<>();
        ArrayList<String> writerList = new ArrayList<>();
        ArrayList<String> genreList = new ArrayList<>();
        actorList.add("Deleted Actor");
        actorList.add("Deleted Actor 2");
        writerList.add("Deleted Writer");
        genreList.add("DeleteGenre1");
        genreList.add("DeleteGenre2");

        testMovie.setTitle("Deleted Movie");
        testMovie.setDirector("Deleted Director");
        testMovie.setReleaseDate(1950);
        testMovie.setMpaaRating("R");
        testMovie.setStudio("Del Studio");
        testMovie.setUserRating(11);
        testMovie.setActorList(actorList);
        testMovie.setNotes("Del note");
        testMovie.setWriterList(writerList);
        testMovie.setGenres(genreList);
        testMovie.setTrailerURL("delTrailerURL");
        
        dao.addMovie(testMovie);
        
        assertEquals(11, dao.getAllMovies().size());
        
        dao.removeMovie(testMovie.getId());
        
        assertEquals(10, dao.getAllMovies().size());
        assertFalse(dao.getAllMovies().stream().filter(d -> d.getTitle().equalsIgnoreCase("Deleted")).count() > 0);

        testMovie = new Movie();
        actorList = new ArrayList<>();
        writerList = new ArrayList<>();
        actorList.add("After Delete");
        testMovie.setTitle("TitleAD");
        testMovie.setDirector("DirectorAD");
        testMovie.setReleaseDate(2000);
        testMovie.setMpaaRating("R");
        testMovie.setStudio("StudioAD");
        testMovie.setUserRating(1);
        testMovie.setActorList(actorList);
        testMovie.setWriterList(writerList);
        testMovie.setNotes("blahblah");

        dao.addMovie(testMovie);

        assertEquals(11, dao.getAllMovies().size());
        assertEquals("TitleAD", dao.viewMovie(testMovie.getId()).getTitle());
        assertFalse(dao.getAllMovies().stream().filter(d -> d.getId() == 5).count() > 0);
    }

    @Test
    public void testUpdateMovie() {
        
        assertEquals(10, dao.getAllMovies().size());
        List<Movie> movieList = dao.getAllMovies();
        
        Movie testMovie = movieList.get(0);
        ArrayList<String> actorList = new ArrayList<>();
        actorList.add("Updated");
        testMovie.setTitle("TitleU");
        testMovie.setDirector("DirectorU");
        testMovie.setReleaseDate(2000);
        testMovie.setMpaaRating("R");
        testMovie.setStudio("StudioU");
        testMovie.setUserRating(1);
        testMovie.setActorList(actorList);
        testMovie.setNotes("yaba");

        dao.updateMovie(testMovie);

        assertEquals(10, dao.getAllMovies().size());
        assertEquals("TitleU", dao.viewMovie(testMovie.getId()).getTitle());
        assertTrue(dao.getAllMovies().stream().filter(d -> d.getTitle().equals("TitleU")).count() == 1);
    }

    @Test
    public void testTitleSort() {
        
        assertEquals(10, dao.getAllMovies().size());
        
        ArrayList<Movie> sorted = dao.sortByTitle(true);
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
        assertEquals(10, dao.getAllMovies().size());
        
        ArrayList<Movie> sorted = dao.sortByYear(true);
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
        assertEquals(10, dao.getAllMovies().size());
        
        ArrayList<Movie> sorted = dao.sortByMPAARating(!true);
        assertEquals("Serious Movie", sorted.get(9).getTitle());
        
        sorted = dao.sortByMPAARating(true);
        assertEquals("Serious Movie", sorted.get(0).getTitle());
    }
    
    @Test
    public void testUserRatingSort() {
        assertEquals(10, dao.getAllMovies().size());
        
        ArrayList<Movie> sorted = dao.sortByUserRating(!true);
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
        assertEquals(9, dao.getMoviesWithTitle("Title").size());
        assertEquals(1, dao.getMoviesWithTitle("Title1").size());
        assertEquals(0, dao.getMoviesWithTitle("Title11").size());
        assertEquals(1, dao.getMoviesWithTitle("Serious Movie").size());
        assertEquals(10, dao.getMoviesWithTitle("E").size());
    }
    
    @Test
    public void testYearSearch() {
        assertEquals(1, dao.getMoviesWithReleaseDate(2005).size());
    }
    
    @Test
    public void testGenreSearch() {
        assertEquals(9, dao.getMoviesWithGenre("Action").size());
        assertEquals(10, dao.getMoviesWithGenre("Comedy").size());
        assertEquals(1, dao.getMoviesWithGenre("Drama").size());
        assertEquals(0, dao.getMoviesWithGenre("D").size());
    }

    @Test
    public void testDirectorSearch() {
        assertEquals(9, dao.getMoviesWithDirector("Director").size());
        assertEquals(1, dao.getMoviesWithDirector("Director1").size());
        assertEquals(0, dao.getMoviesWithDirector("Director55").size());
        assertEquals(1, dao.getMoviesWithDirector("Charlie").size());
        assertEquals(10, dao.getMoviesWithDirector("D").size());
    }

    @Test
    public void testActorSearch() {
        assertEquals(9, dao.getMoviesWithActor("Bovine J").size());
        assertEquals(1, dao.getMoviesWithActor("Ryan J").size());
        assertEquals(9, dao.getMoviesWithActor("McPoyle").size());
        assertEquals(1, dao.getMoviesWithActor("Actor").size());
        assertEquals(10, dao.getMoviesWithActor("Ryan").size());
    }
    
    @Test
    public void testWriterSearch() {
        assertEquals(9, dao.getMoviesWithWriter("Writer").size());
        assertEquals(1, dao.getMoviesWithWriter("Writer 11").size());
        assertEquals(1, dao.getMoviesWithWriter("Oscar").size());
        assertEquals(10, dao.getMoviesWithWriter("W").size());
    }
    
    @Test
    public void testStudioSearch() {
        assertEquals(10, dao.getMoviesFromStudio("Studio").size());
        assertEquals(1, dao.getMoviesFromStudio("Studio1").size());
        assertEquals(10, dao.getMoviesFromStudio("D").size());
        assertEquals(1, dao.getMoviesFromStudio("D Studio").size());
    }
    
    @Test
    public void testMPAASearch() {
        assertEquals(9, dao.getMoviesWithMPAARating("PG").size());
        assertEquals(0, dao.getMoviesWithMPAARating("G").size());
        assertEquals(0, dao.getMoviesWithMPAARating("PG-13").size());
        assertEquals(1, dao.getMoviesWithMPAARating("R").size());
    }
    @Test
    public void testKeywordSearch() {
        assertEquals(10, dao.getMoviesByKeyword("Ryan").size());
        assertEquals(1, dao.getMoviesByKeyword("seriou").size());
        assertEquals(9, dao.getMoviesByKeyword("titl").size());
        assertEquals(0, dao.getMoviesByKeyword("studio").size());
        assertEquals(1, dao.getMoviesByKeyword("Oscar").size());
        assertEquals(10, dao.getMoviesByKeyword("D").size());
        assertEquals(1, dao.getMoviesByKeyword("Charlie").size());
        assertEquals(9, dao.getMoviesByKeyword("direct").size());
    }
}
