/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.mymovies.dao;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sg.mymovies.models.Movie;
import com.sg.mymovies.ops.ArrayListUtility;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author apprentice
 */
public class MyMoviesFileIOImpl implements MyMoviesDAO {

    private HashMap<Integer, Movie> movieLibrary;
    private ArrayList<Movie> currentMovieList;
    private final String DELIMETER = "::";
    private final String FILE_PATH = System.getProperty("user.home") + File.separator
            + "Documents" + File.separator + "MovieLibrary";
    private final File FILE_DIR = new File(FILE_PATH);
    private final String FILE_NAME = "/movielibrary.txt";

    public MyMoviesFileIOImpl() {
        movieLibrary = new HashMap<>();
        if (!FILE_DIR.exists()) {
            FILE_DIR.mkdirs();
        }
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
        Gson gson = new Gson();
        Type type = new TypeToken<Map<Integer, Movie>>() {
        }.getType();
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(FILE_DIR + FILE_NAME));
            String jsonMovieLibrary = ((JSONObject) obj).toJSONString();
            movieLibrary = gson.fromJson(jsonMovieLibrary, type);
        } catch (IOException | ParseException ex) {
            Logger.getLogger(MyMoviesFileIOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void save() {
        Gson gson = new Gson();
        String jsonMovieLibrary = gson.toJson(movieLibrary);
        try (FileWriter file = new FileWriter(FILE_DIR + FILE_NAME)) {
            file.write(jsonMovieLibrary);
        } catch (IOException ex) {
            Logger.getLogger(MyMoviesFileIOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
