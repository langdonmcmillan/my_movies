package com.sg.dvdlibrary;

import com.sg.dvdlibrary.dao.DVDFileIOImpl;
import com.sg.dvdlibrary.dao.DVDLibraryDAO;
import com.sg.dvdlibrary.models.DVD;
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

@Controller
public class dvdLibraryController {

    private DVDLibraryDAO dao;
    
    @Autowired
    @Inject
    public dvdLibraryController(DVDLibraryDAO dao) {
        this.dao = dao;
    }

    @RequestMapping(value = {"/", "mymovies", "home"}, method = RequestMethod.GET)
    public String displayHome(Map<String, Object> model) {
        return "mymovies";
    }

    @RequestMapping(value = "/getDVDs", method = RequestMethod.GET)
    @ResponseBody
    public ArrayList<DVD> getAllContacts() {
        return dao.getAllDVDs();
    }

    @RequestMapping(value = "/dvd/{id}", method = RequestMethod.GET)
    @ResponseBody
    public DVD getDVD(@PathVariable("id") int id) {
        return dao.viewDVD(id);
    }

    @RequestMapping(value = "/dvd/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDVD(@PathVariable("id") int id) {
        dao.removeDVD(id);
    }

    @RequestMapping(value = "/dvd/{id}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void editDVD(@PathVariable("id") int id, @Valid @RequestBody DVD dvd) {
        dvd.setId(id);
        dao.updateDVD(dvd);
    }

    @RequestMapping(value = "/dvd", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public DVD addDVD(@Valid @RequestBody DVD dvd) {
        return dao.addDVD(dvd);
    }

    @RequestMapping(value = "/sort/{sortBy}/{descend}", method = RequestMethod.GET)
    @ResponseBody
    public ArrayList<DVD> sortDVDs(@PathVariable("sortBy") String sortBy, @PathVariable("descend") boolean descend) {
        ArrayList<DVD> sortedDVDs;
        switch (sortBy) {
            case "releaseDate":
                sortedDVDs = dao.sortByYear(descend);
                break;
            case "mpaaRating":
                sortedDVDs = dao.sortByMPAARating(descend);
                break;
            case "userRating":
                sortedDVDs = dao.sortByUserRating(descend);
                break;
            default:
                sortedDVDs = dao.sortByTitle(descend);
                break;
        }
        return sortedDVDs;
    }

    @RequestMapping(value = "/search/{searchType}/{searchTerm}", method = RequestMethod.GET)
    @ResponseBody
    public ArrayList<DVD> searchDVDs(@PathVariable("searchType") String searchType, @PathVariable("searchTerm") String searchTerm) {
        ArrayList<DVD> searchResults;
        switch (searchType) {
            case "Title":
                searchResults = dao.getDVDsWithTitle(searchTerm);
                break;
            case "Director":
                searchResults = dao.getDVDsWithDirector(searchTerm);
                break;
            case "Writers":
                searchResults = dao.getDVDsWithWriter(searchTerm);
                break;
            case "Actors":
                searchResults = dao.getDVDsWithActor(searchTerm);
                break;
            case "Studio":
                searchResults = dao.getDVDsFromStudio(searchTerm);
                break;
            case "ReleaseDate":
                searchResults = dao.getDVDsWithReleaseDate(Integer.parseInt(searchTerm));
                break;
            case "Genres":
                searchResults = dao.getDVDsWithGenre(searchTerm);
                break;
            case "MpaaRating":
                searchResults = dao.getDVDsWithMPAARating(searchTerm);
                break;
            default:
                searchResults = dao.getDVDsByKeyword(searchTerm);
                break;
        }
        if (searchTerm.equals("xalldvdsx")) {
            searchResults = dao.getAllDVDs();
        }
        return searchResults;
    }
}
