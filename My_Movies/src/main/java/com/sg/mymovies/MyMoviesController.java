package com.sg.mymovies;

import com.sg.mymovies.dao.MyMoviesFileIOImpl;
import com.sg.mymovies.models.Movie;
import java.util.ArrayList;
import java.util.Map;
import javax.inject.Inject;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.sg.mymovies.dao.MyMoviesDAO;

@Controller
public class MyMoviesController {

    private MyMoviesDAO dao;
    
    @Autowired
    @Inject
    public MyMoviesController(MyMoviesDAO dao) {
        this.dao = dao;
    }

    @RequestMapping(value = {"/", "mymovies", "home"}, method = RequestMethod.GET)
    public String displayHome(Map<String, Object> model) {
        return "mymovies";
    }

    @RequestMapping(value = "/getMovies", method = RequestMethod.GET)
    @ResponseBody
    public ArrayList<Movie> getAllContacts() {
        return dao.getAllMovies();
    }

    @RequestMapping(value = "/movie/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Movie getMovie(@PathVariable("id") int id) {
        return dao.viewMovie(id);
    }

    @RequestMapping(value = "/movie/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMovie(@PathVariable("id") int id) {
        dao.removeMovie(id);
    }

    @RequestMapping(value = "/movie/{id}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void editMovie(@PathVariable("id") int id, @Valid @RequestBody Movie movie) {
        movie.setId(id);
        dao.updateMovie(movie);
    }

    @RequestMapping(value = "/movie", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public Movie addMovie(@Valid @RequestBody Movie movie) {
        return dao.addMovie(movie);
    }

    @RequestMapping(value = "/sort/{sortBy}/{descend}", method = RequestMethod.GET)
    @ResponseBody
    public ArrayList<Movie> sortMovies(@PathVariable("sortBy") String sortBy, @PathVariable("descend") boolean alreadySorted) {
        ArrayList<Movie> sortedMovies;
        switch (sortBy) {
            case "releaseDate":
                sortedMovies = dao.sortByYear(alreadySorted);
                break;
            case "mpaaRating":
                sortedMovies = dao.sortByMPAARating(alreadySorted);
                break;
            case "userRating":
                sortedMovies = dao.sortByUserRating(alreadySorted);
                break;
            default:
                sortedMovies = dao.sortByTitle(alreadySorted);
                break;
        }
        return sortedMovies;
    }

    @RequestMapping(value = "/search/{searchType}/{searchTerm}", method = RequestMethod.GET)
    @ResponseBody
    public ArrayList<Movie> searchMovies(@PathVariable("searchType") String searchType, @PathVariable("searchTerm") String searchTerm) {
        ArrayList<Movie> searchResults;
        switch (searchType) {
            case "Title":
                searchResults = dao.getMoviesWithTitle(searchTerm);
                break;
            case "Director":
                searchResults = dao.getMoviesWithDirector(searchTerm);
                break;
            case "Writers":
                searchResults = dao.getMoviesWithWriter(searchTerm);
                break;
            case "Actors":
                searchResults = dao.getMoviesWithActor(searchTerm);
                break;
            case "Studio":
                searchResults = dao.getMoviesFromStudio(searchTerm);
                break;
            case "ReleaseDate":
                searchResults = dao.getMoviesWithReleaseDate(Integer.parseInt(searchTerm));
                break;
            case "Genres":
                searchResults = dao.getMoviesWithGenre(searchTerm);
                break;
            case "MpaaRating":
                searchResults = dao.getMoviesWithMPAARating(searchTerm);
                break;
            default:
                searchResults = dao.getMoviesByKeyword(searchTerm);
                break;
        }
        if (searchTerm.equals("xallmoviesx")) {
            searchResults = dao.getAllMovies();
        }
        return searchResults;
    }
}
