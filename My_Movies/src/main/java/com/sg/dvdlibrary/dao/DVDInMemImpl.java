/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.dvdlibrary.dao;

import com.sg.dvdlibrary.models.DVD;
import com.sg.dvdlibrary.ops.ArrayListUtility;
import java.util.ArrayList;
import java.util.Collections;
import static java.util.Comparator.comparing;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 *
 * @author apprentice
 */
public class DVDInMemImpl implements DVDLibraryDAO {

    private HashMap<Integer, DVD> dvdLibrary;
    private ArrayList<DVD> currentDVDList;

    public DVDInMemImpl() {
        dvdLibrary = new HashMap<>();
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
        DVD testDVD = new DVD();
        ArrayList<String> actorList = new ArrayList<>();
        ArrayList<String> writerList = new ArrayList<>();
        ArrayList<String> genreList = new ArrayList<>();
        actorList.add("Clint Eastwood");
        actorList.add("Morgan Freeman");
        writerList.add("David Webb Peoples");
        genreList.add("Western");
        genreList.add("Drama");

        testDVD.setTitle("Unforgiven");
        testDVD.setDirector("Clint Eastwood");
        testDVD.setReleaseDate(1992);
        testDVD.setMpaaRating("R");
        testDVD.setStudio("Warner Bros");
        testDVD.setUserRating(5);
        testDVD.setActorList(actorList);
        testDVD.setNotes("Greatest western of all time. I'll fight anyone who says otherwise. Just kidding, I'm a lover not a fighter. ");
        testDVD.setWriterList(writerList);
        testDVD.setGenres(genreList);
        testDVD.setTrailerURL("https://www.youtube.com/embed/H9NQz2GXGTg");
        testDVD.setSynopsis("Western pitting an aging outlaw and friends against a brutal sheriff.");
        testDVD.setCoverURL("https://upload.wikimedia.org/wikipedia/en/4/4e/Unforgiven_2.jpg");
        addDVD(testDVD);
        
        testDVD = new DVD();
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

        testDVD.setTitle("Children of Men");
        testDVD.setDirector("Alfonso Cuarón");
        testDVD.setReleaseDate(2006);
        testDVD.setMpaaRating("R");
        testDVD.setStudio("Universal Pictures");
        testDVD.setUserRating(3);
        testDVD.setActorList(actorList);
        testDVD.setNotes("Dark dystopian sci fi.");
        testDVD.setWriterList(writerList);
        testDVD.setGenres(genreList);
        testDVD.setTrailerURL("https://www.youtube.com/embed/2VT2apoX90o");
        testDVD.setSynopsis("Dystopian sci fi movie about a world where humans can no longer reproduce.");
        testDVD.setCoverURL("https://www.uphe.com/sites/default/files/styles/scale__344w_/public/2016/04/childrenofmen_poster.jpg?itok=TBAn6_mt");
        addDVD(testDVD);
    }

    private void save() {

    }
}