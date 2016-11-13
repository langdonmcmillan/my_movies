/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.mymovies.dao;

import com.sg.mymovies.models.Movie;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author apprentice
 */
public interface MyMoviesDAO {
    public Movie addMovie(Movie movie);
    public void removeMovie(int id);
    public Movie viewMovie(int id);
    public void updateMovie(Movie movie);
    public ArrayList<Movie> getAllMovies();
    public ArrayList<Movie> sortByTitle(boolean alreadySorted);
    public ArrayList<Movie> sortByYear(boolean alreadySorted);
    public ArrayList<Movie> sortByMPAARating(boolean alreadySorted);
    public ArrayList<Movie> sortByUserRating(boolean alreadySorted);
    public ArrayList<Movie> getMoviesWithTitle(String title);
    public ArrayList<Movie> getMoviesWithReleaseDate(int releaseDate);
    public ArrayList<Movie> getMoviesWithGenre(String genre);
    public ArrayList<Movie> getMoviesWithDirector(String director);
    public ArrayList<Movie> getMoviesWithWriter(String writer);
    public ArrayList<Movie> getMoviesWithActor(String actor);
    public ArrayList<Movie> getMoviesFromStudio(String studio);
    public ArrayList<Movie> getMoviesWithMPAARating(String rating);
    public ArrayList<Movie> getMoviesByKeyword(String searchTerm);
}
