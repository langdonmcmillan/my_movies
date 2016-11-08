/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.dvdlibrary.dao;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sg.dvdlibrary.models.DVD;
import com.sg.dvdlibrary.ops.ArrayListUtility;
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
public class DVDFileIOImpl implements DVDLibraryDAO {

    private HashMap<Integer, DVD> dvdLibrary;
    private ArrayList<DVD> currentDVDList;
    private final String DELIMETER = "::";
    private final String FILE_PATH = System.getProperty("user.home") + File.separator
            + "Documents" + File.separator + "DVDLibrary";
    private final File FILE_DIR = new File(FILE_PATH);
    private final String FILE_NAME = "/dvdlibrary.txt";

    public DVDFileIOImpl() {
        dvdLibrary = new HashMap<>();
        if (!FILE_DIR.exists()) {
            FILE_DIR.mkdirs();
        }
        load();
    }

    @Override
    public DVD addDVD(DVD dvd) {
        int id = (dvdLibrary.isEmpty()) ? 1 : Collections.max(dvdLibrary.keySet()) + 1;
        dvd.setId(id);
        dvdLibrary.put(id, dvd);
        save();
        return dvd;
    }

    @Override
    public void removeDVD(int id) {
        dvdLibrary.remove(id);
        save();
    }

    @Override
    public DVD viewDVD(int id) {
        return dvdLibrary.get(id);
    }

    @Override
    public void updateDVD(DVD dvd) {
        dvdLibrary.put(dvd.getId(), dvd);
        save();
    }

    @Override
    public ArrayList<DVD> getAllDVDs() {
        currentDVDList = (ArrayList<DVD>) dvdLibrary.values().stream()
                .sorted((dvd1, dvd2) -> dvd1.getTitle().compareTo(dvd2.getTitle()))
                .collect(Collectors.toList());
        return currentDVDList;
    }

    @Override
    public ArrayList<DVD> sortByTitle(boolean defaultSort) {
        List<DVD> sortedDVDs;
        if (defaultSort) {
            sortedDVDs = currentDVDList.stream().sorted((dvd1, dvd2) -> dvd2.getTitle()
                    .compareToIgnoreCase(dvd1.getTitle())).collect(Collectors.toList());
        } else {
            sortedDVDs = currentDVDList.stream().sorted((dvd1, dvd2) -> dvd1.getTitle()
                    .compareToIgnoreCase(dvd2.getTitle())).collect(Collectors.toList());
        }
        return (ArrayList<DVD>) sortedDVDs;
    }

    @Override
    public ArrayList<DVD> sortByYear(boolean defaultSort) {
        List<DVD> sortedDVDs;
        if (defaultSort) {
            sortedDVDs = currentDVDList.stream().sorted((dvd1, dvd2) -> dvd1.getReleaseDate()
                    .compareTo(dvd2.getReleaseDate())).collect(Collectors.toList());
        } else {
            sortedDVDs = currentDVDList.stream().sorted((dvd1, dvd2) -> dvd2.getReleaseDate()
                    .compareTo(dvd1.getReleaseDate())).collect(Collectors.toList());
        }
        return (ArrayList<DVD>) sortedDVDs;
    }

    @Override
    public ArrayList<DVD> sortByMPAARating(boolean defaultSort) {
        List<DVD> sortedDVDs;
        if (defaultSort) {
            sortedDVDs = currentDVDList.stream().sorted((dvd1, dvd2) -> dvd2.getRatingsValue()
                    .compareTo(dvd1.getRatingsValue())).collect(Collectors.toList());
        } else {
            sortedDVDs = currentDVDList.stream().sorted((dvd1, dvd2) -> dvd1.getRatingsValue()
                    .compareTo(dvd2.getRatingsValue())).collect(Collectors.toList());
        }
        return (ArrayList<DVD>) sortedDVDs;
    }

    @Override
    public ArrayList<DVD> sortByUserRating(boolean defaultSort) {
        List<DVD> sortedDVDs;
        if (defaultSort) {
            sortedDVDs = currentDVDList.stream().sorted((dvd1, dvd2) -> dvd1.getUserRating()
                    .compareTo(dvd2.getUserRating())).collect(Collectors.toList());
        } else {
            sortedDVDs = currentDVDList.stream().sorted((dvd1, dvd2) -> dvd2.getUserRating()
                    .compareTo(dvd1.getUserRating())).collect(Collectors.toList());
        }
        return (ArrayList<DVD>) sortedDVDs;
    }

    @Override
    public ArrayList<DVD> getDVDsWithTitle(String title) {
        currentDVDList = (ArrayList<DVD>) dvdLibrary.values().stream()
                .filter(d -> d.getTitle().toLowerCase().contains(title.toLowerCase())).collect(Collectors.toList());
        return currentDVDList;
    }

    @Override
    public ArrayList<DVD> getDVDsWithReleaseDate(int releaseDate) {
        Integer releaseYear = releaseDate;
        currentDVDList = (ArrayList<DVD>) dvdLibrary.values().stream()
                .filter(d -> d.getReleaseDate().equals(releaseYear)).collect(Collectors.toList());
        return currentDVDList;
    }

    @Override
    public ArrayList<DVD> getDVDsWithGenre(String genre) {
        currentDVDList = (ArrayList<DVD>) dvdLibrary.values().stream()
                .filter(d -> d.getGenres().contains(genre)).collect(Collectors.toList());
        return currentDVDList;
    }

    @Override
    public ArrayList<DVD> getDVDsWithDirector(String director) {
        currentDVDList = (ArrayList<DVD>) dvdLibrary.values().stream()
                .filter(d -> d.getDirector().toLowerCase().contains(director.toLowerCase())).collect(Collectors.toList());
        return currentDVDList;
    }

    @Override
    public ArrayList<DVD> getDVDsWithWriter(String writer) {
        currentDVDList = (ArrayList<DVD>) dvdLibrary.values().stream()
                .filter(d -> ArrayListUtility.containsSequenceIgnoreCase(d.getWriterList(), writer)).collect(Collectors.toList());
        return currentDVDList;
    }

    @Override
    public ArrayList<DVD> getDVDsWithActor(String actor) {
        currentDVDList = (ArrayList<DVD>) dvdLibrary.values().stream()
                .filter(d -> ArrayListUtility.containsSequenceIgnoreCase(d.getActorList(), actor)).collect(Collectors.toList());
        return currentDVDList;
    }

    @Override
    public ArrayList<DVD> getDVDsFromStudio(String studio) {
        currentDVDList = (ArrayList<DVD>) dvdLibrary.values().stream()
                .filter(d -> d.getStudio().toLowerCase().contains(studio.toLowerCase())).collect(Collectors.toList());
        return currentDVDList;
    }

    @Override
    public ArrayList<DVD> getDVDsWithMPAARating(String rating) {
        currentDVDList = (ArrayList<DVD>) dvdLibrary.values().stream()
                .filter(d -> d.getMpaaRating().toLowerCase().equals(rating.toLowerCase())).collect(Collectors.toList());
        return currentDVDList;
    }

    @Override
    public ArrayList<DVD> getDVDsByKeyword(String searchTerm) {
        Predicate<DVD> titleMatch = (d) -> d.getTitle().toLowerCase().contains(searchTerm.toLowerCase());
        Predicate<DVD> directorMatch = (d) -> d.getDirector().toLowerCase().contains(searchTerm.toLowerCase());
        Predicate<DVD> writerMatch = (d) -> ArrayListUtility.containsSequenceIgnoreCase(d.getWriterList(), searchTerm);
        Predicate<DVD> actorMatch = (d) -> ArrayListUtility.containsSequenceIgnoreCase(d.getWriterList(), searchTerm);

        currentDVDList = (ArrayList<DVD>) dvdLibrary.values().stream().filter(titleMatch.or(directorMatch)
                .or(writerMatch))
                .collect(Collectors.toList());
        return currentDVDList;
    }

    private void load() {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<Integer, DVD>>() {
        }.getType();
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(FILE_DIR + FILE_NAME));
            String jsonDVDLibrary = ((JSONObject) obj).toJSONString();
            dvdLibrary = gson.fromJson(jsonDVDLibrary, type);
        } catch (IOException | ParseException ex) {
            Logger.getLogger(DVDFileIOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void save() {
        Gson gson = new Gson();
        String jsonDVDLibrary = gson.toJson(dvdLibrary);
        try (FileWriter file = new FileWriter(FILE_DIR + FILE_NAME)) {
            file.write(jsonDVDLibrary);
        } catch (IOException ex) {
            Logger.getLogger(DVDFileIOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
