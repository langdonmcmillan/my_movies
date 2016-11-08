/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.dvdlibrary.dao;

import com.sg.dvdlibrary.models.DVD;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author apprentice
 */
public interface DVDLibraryDAO {
    public DVD addDVD(DVD dvd);
    public void removeDVD(int id);
    public DVD viewDVD(int id);
    public void updateDVD(DVD dvd);
    public ArrayList<DVD> getAllDVDs();
    public ArrayList<DVD> sortByTitle(boolean defaultSort);
    public ArrayList<DVD> sortByYear(boolean defaultSort);
    public ArrayList<DVD> sortByMPAARating(boolean defaultSort);
    public ArrayList<DVD> sortByUserRating(boolean defaultSort);
    public ArrayList<DVD> getDVDsWithTitle(String title);
    public ArrayList<DVD> getDVDsWithReleaseDate(int releaseDate);
    public ArrayList<DVD> getDVDsWithGenre(String genre);
    public ArrayList<DVD> getDVDsWithDirector(String director);
    public ArrayList<DVD> getDVDsWithWriter(String writer);
    public ArrayList<DVD> getDVDsWithActor(String actor);
    public ArrayList<DVD> getDVDsFromStudio(String studio);
    public ArrayList<DVD> getDVDsWithMPAARating(String rating);
    public ArrayList<DVD> getDVDsByKeyword(String searchTerm);
}
