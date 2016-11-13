/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.mymovies.dao;

import com.sg.mymovies.models.Movie;
import com.sg.mymovies.ops.ArrayListUtility;
import java.util.ArrayList;
import java.util.Collections;
import static java.util.Comparator.comparing;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import static java.util.Comparator.comparing;
import static java.util.Comparator.comparing;
import static java.util.Comparator.comparing;
import static java.util.Comparator.comparing;
import static java.util.Comparator.comparing;
import static java.util.Comparator.comparing;
import static java.util.Comparator.comparing;

/**
 *
 * @author apprentice
 */
public class MyMoviesInMemImpl implements MyMoviesDAO {

    private HashMap<Integer, Movie> movieLibrary;
    private ArrayList<Movie> currentMovieList;

    public MyMoviesInMemImpl() {
        movieLibrary = new HashMap<>();
        load();
    }

    @Override
    public Movie addMovie(Movie movie) {
        int id = (movieLibrary.isEmpty()) ? 1 : Collections.max(movieLibrary.keySet()) + 1;
        movie.setId(id);
        movieLibrary.put(id, movie);
        save();
        return movie;
    }

    @Override
    public void removeMovie(int id) {
        movieLibrary.remove(id);
        save();
    }

    @Override
    public Movie viewMovie(int id) {
        return movieLibrary.get(id);
    }

    @Override
    public void updateMovie(Movie movie) {
        movieLibrary.put(movie.getId(), movie);
        save();
    }

    @Override
    public ArrayList<Movie> getAllMovies() {
        currentMovieList = (ArrayList<Movie>) movieLibrary.values().stream()
                .sorted((movie1, movie2) -> movie1.getTitle().compareTo(movie2.getTitle()))
                .collect(Collectors.toList());
        return currentMovieList;
    }

    @Override
    public ArrayList<Movie> sortByTitle(boolean alreadySorted) {
        List<Movie> sortedMovies;
        if (alreadySorted) {
            sortedMovies = currentMovieList.stream().sorted((movie1, movie2) -> movie2.getTitle()
                    .compareToIgnoreCase(movie1.getTitle())).collect(Collectors.toList());
        } else {
            sortedMovies = currentMovieList.stream().sorted((movie1, movie2) -> movie1.getTitle()
                    .compareToIgnoreCase(movie2.getTitle())).collect(Collectors.toList());
        }
        return (ArrayList<Movie>) sortedMovies;
    }

    @Override
    public ArrayList<Movie> sortByYear(boolean alreadySorted) {
        List<Movie> sortedMovies;
        if (alreadySorted) {
            sortedMovies = currentMovieList.stream().sorted((movie1, movie2) -> movie1.getReleaseDate()
                    .compareTo(movie2.getReleaseDate())).collect(Collectors.toList());
        } else {
            sortedMovies = currentMovieList.stream().sorted((movie1, movie2) -> movie2.getReleaseDate()
                    .compareTo(movie1.getReleaseDate())).collect(Collectors.toList());
        }
        return (ArrayList<Movie>) sortedMovies;
    }

    @Override
    public ArrayList<Movie> sortByMPAARating(boolean alreadySorted) {
        List<Movie> sortedMovies;
        if (alreadySorted) {
            sortedMovies = currentMovieList.stream().sorted((movie1, movie2) -> movie2.getRatingsValue()
                    .compareTo(movie1.getRatingsValue())).collect(Collectors.toList());
        } else {
            sortedMovies = currentMovieList.stream().sorted((movie1, movie2) -> movie1.getRatingsValue()
                    .compareTo(movie2.getRatingsValue())).collect(Collectors.toList());
        }
        return (ArrayList<Movie>) sortedMovies;
    }

    @Override
    public ArrayList<Movie> sortByUserRating(boolean alreadySorted) {
        List<Movie> sortedMovies;
        if (alreadySorted) {
            sortedMovies = currentMovieList.stream().sorted((movie1, movie2) -> movie1.getUserRating()
                    .compareTo(movie2.getUserRating())).collect(Collectors.toList());
        } else {
            sortedMovies = currentMovieList.stream().sorted((movie1, movie2) -> movie2.getUserRating()
                    .compareTo(movie1.getUserRating())).collect(Collectors.toList());
        }
        return (ArrayList<Movie>) sortedMovies;
    }

    @Override
    public ArrayList<Movie> getMoviesWithTitle(String title) {
        currentMovieList = (ArrayList<Movie>) movieLibrary.values().stream()
                .filter(d -> d.getTitle().toLowerCase().contains(title.toLowerCase())).collect(Collectors.toList());
        return currentMovieList;
    }

    @Override
    public ArrayList<Movie> getMoviesWithReleaseDate(int releaseDate) {
        Integer releaseYear = releaseDate;
        currentMovieList = (ArrayList<Movie>) movieLibrary.values().stream()
                .filter(d -> d.getReleaseDate().equals(releaseYear)).collect(Collectors.toList());
        return currentMovieList;
    }

    @Override
    public ArrayList<Movie> getMoviesWithGenre(String genre) {
        currentMovieList = (ArrayList<Movie>) movieLibrary.values().stream()
                .filter(d -> d.getGenres().contains(genre)).collect(Collectors.toList());
        return currentMovieList;
    }

    @Override
    public ArrayList<Movie> getMoviesWithDirector(String director) {
        currentMovieList = (ArrayList<Movie>) movieLibrary.values().stream()
                .filter(d -> d.getDirector().toLowerCase().contains(director.toLowerCase())).collect(Collectors.toList());
        return currentMovieList;
    }

    @Override
    public ArrayList<Movie> getMoviesWithWriter(String writer) {
        currentMovieList = (ArrayList<Movie>) movieLibrary.values().stream()
                .filter(d -> ArrayListUtility.containsSequenceIgnoreCase(d.getWriterList(), writer)).collect(Collectors.toList());
        return currentMovieList;
    }

    @Override
    public ArrayList<Movie> getMoviesWithActor(String actor) {
        currentMovieList = (ArrayList<Movie>) movieLibrary.values().stream()
                .filter(d -> ArrayListUtility.containsSequenceIgnoreCase(d.getActorList(), actor)).collect(Collectors.toList());
        return currentMovieList;
    }

    @Override
    public ArrayList<Movie> getMoviesFromStudio(String studio) {
        currentMovieList = (ArrayList<Movie>) movieLibrary.values().stream()
                .filter(d -> d.getStudio().toLowerCase().contains(studio.toLowerCase())).collect(Collectors.toList());
        return currentMovieList;
    }

    @Override
    public ArrayList<Movie> getMoviesWithMPAARating(String rating) {
        currentMovieList = (ArrayList<Movie>) movieLibrary.values().stream()
                .filter(d -> d.getMpaaRating().toLowerCase().equals(rating.toLowerCase())).collect(Collectors.toList());
        return currentMovieList;
    }

    @Override
    public ArrayList<Movie> getMoviesByKeyword(String searchTerm) {
        Predicate<Movie> titleMatch = (d) -> d.getTitle().toLowerCase().contains(searchTerm.toLowerCase());
        Predicate<Movie> directorMatch = (d) -> d.getDirector().toLowerCase().contains(searchTerm.toLowerCase());
        Predicate<Movie> writerMatch = (d) -> ArrayListUtility.containsSequenceIgnoreCase(d.getWriterList(), searchTerm);
        Predicate<Movie> actorMatch = (d) -> ArrayListUtility.containsSequenceIgnoreCase(d.getWriterList(), searchTerm);

        currentMovieList = (ArrayList<Movie>) movieLibrary.values().stream().filter(titleMatch.or(directorMatch)
                .or(writerMatch))
                .collect(Collectors.toList());
        return currentMovieList;
    }

    private void load() {
        Movie testMovie = new Movie();
        ArrayList<String> actorList = new ArrayList<>();
        ArrayList<String> writerList = new ArrayList<>();
        ArrayList<String> genreList = new ArrayList<>();
        actorList.add("Clint Eastwood");
        actorList.add("Morgan Freeman");
        writerList.add("David Webb Peoples");
        genreList.add("Western");
        genreList.add("Drama");

        testMovie.setTitle("Unforgiven");
        testMovie.setDirector("Clint Eastwood");
        testMovie.setReleaseDate(1992);
        testMovie.setMpaaRating("R");
        testMovie.setStudio("Warner Bros");
        testMovie.setUserRating(5);
        testMovie.setActorList(actorList);
        testMovie.setNotes("Greatest western of all time. I'll fight anyone who says otherwise. Just kidding, I'm a lover not a fighter. ");
        testMovie.setWriterList(writerList);
        testMovie.setGenres(genreList);
        testMovie.setTrailerURL("https://www.youtube.com/embed/H9NQz2GXGTg");
        testMovie.setSynopsis("Western pitting an aging outlaw and friends against a brutal sheriff.");
        testMovie.setCoverURL("https://upload.wikimedia.org/wikipedia/en/4/4e/Unforgiven_2.jpg");
        addMovie(testMovie);
        
        testMovie = new Movie();
        actorList = new ArrayList<>();
        writerList = new ArrayList<>();
        genreList = new ArrayList<>();
        actorList.add("Clive Owen");
        actorList.add("Julianne Moore");
        actorList.add("Michael Caine");
        actorList.add("Chiwetel Ejiofor");
        writerList.add("Alfonso Cuarón");
        writerList.add("Timothy J. Sexton");
        writerList.add("David Arata");
        writerList.add("Mark Fergus");
        writerList.add("Hawk Ostby");
        genreList.add("Drama");
        genreList.add("Science Fiction");

        testMovie.setTitle("Children of Men");
        testMovie.setDirector("Alfonso Cuarón");
        testMovie.setReleaseDate(2006);
        testMovie.setMpaaRating("R");
        testMovie.setStudio("Universal Pictures");
        testMovie.setUserRating(3);
        testMovie.setActorList(actorList);
        testMovie.setNotes("Dark dystopian sci fi.");
        testMovie.setWriterList(writerList);
        testMovie.setGenres(genreList);
        testMovie.setTrailerURL("https://www.youtube.com/embed/2VT2apoX90o");
        testMovie.setSynopsis("Dystopian sci fi movie about a world where humans can no longer reproduce.");
        testMovie.setCoverURL("https://www.uphe.com/sites/default/files/styles/scale__344w_/public/2016/04/childrenofmen_poster.jpg?itok=TBAn6_mt");
        addMovie(testMovie);
    }

    private void save() {

    }
}