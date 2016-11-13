/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.mymovies.models;

import cz.jirutka.validator.collection.constraints.EachSize;
import java.time.LocalDate;
import java.util.ArrayList;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;

/**
 *
 * @author apprentice
 */
public class Movie {

    private final String[] RATINGS = {"G", "PG", "PG-13", "R", "NC-17", ""};
    @NotEmpty(message = "You must enter a title")
    @Size(max = 150, message = "The title may only be 150 characters long")
    private String title;
    private ArrayList<String> genreList;
    @Min(value = 1850, message = "They didn't have movies back then, did they?")
    @Max(value = 2250, message = "What, are you from the future or something?")
    private Integer releaseDate;
    private String mpaaRating;
    @Size(max = 50, message = "Director's name can only be 50 characters long")
    private String director;
    @EachSize(max = 50, message = "Each writer's name can only be 50 characters long")
    private ArrayList<String> writerList;
    @EachSize(max = 50, message = "Each actor's name can only be 50 characters long")
    private ArrayList<String> actorList;
    @Size(max = 50, message = "Studio name can only be 50 characters long")
    private String studio;
    private Integer userRating;
    @Size(max = 500, message = "A bit shorter, please (500 characters max)")
    private String notes;
    private Integer ratingsValue;
    private Integer id;
    @Size(max = 500, message = "I've never seen a URL that long, might want to find another")
    private String trailerURL;
    @Size(max = 500, message = "I've never seen a URL that long, might want to find another")
    private String coverURL;
    @Size(max = 500, message = "This is a synopsis of a movie, not your attempt at a novel")
    private String synopsis;

    public Movie() {
        
        this.title = "NA";
        this.releaseDate = 0;
        this.mpaaRating = "";
        this.director = "";
        this.studio = "";
        this.userRating = 0;
        this.id = 0;
        this.actorList = new ArrayList<>();
        this.genreList = new ArrayList<>();
        this.writerList = new ArrayList<>();
        this.notes = "";
        this.trailerURL = "";
        this.coverURL = "";
        this.synopsis = "";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Integer releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getMpaaRating() {
        return mpaaRating.toUpperCase();
    }

    public void setMpaaRating(String mpaaRating) {
        this.mpaaRating = mpaaRating.toUpperCase();
    }

    public ArrayList<String> getActorList() {
        return actorList;
    }

    public void setActorList(ArrayList<String> actorList) {
        this.actorList = actorList;
    }

    public Integer getUserRating() {
        return userRating;
    }

    public void setUserRating(Integer userRating) {
        this.userRating = userRating;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRatingsValue() {
        for (Integer i = 0; i < RATINGS.length; i++) {
            if (RATINGS[i].equalsIgnoreCase(this.mpaaRating)) {
                ratingsValue = i;
            }
        }
        return ratingsValue;
    }

    public ArrayList<String> getGenres() {
        return genreList;
    }

    public void setGenres(ArrayList<String> genres) {
        this.genreList = genres;
    }

    public ArrayList<String> getWriterList() {
        return writerList;
    }

    public void setWriterList(ArrayList<String> writerList) {
        this.writerList = writerList;
    }

    public String getTrailerURL() {
        return trailerURL;
    }

    public void setTrailerURL(String trailerURL) {
        this.trailerURL = trailerURL;
    }

    public ArrayList<String> getGenreList() {
        return genreList;
    }

    public void setGenreList(ArrayList<String> genreList) {
        this.genreList = genreList;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getStudio() {
        return studio;
    }

    public void setStudio(String studio) {
        this.studio = studio;
    }

    public String getCoverURL() {
        return coverURL;
    }

    public void setCoverURL(String coverURL) {
        this.coverURL = coverURL;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }
}
