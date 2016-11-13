/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.dvdlibrary.dao;

import com.sg.dvdlibrary.models.DVD;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author apprentice
 */
public class DVDDbImpl implements DVDLibraryDAO {

    private JdbcTemplate jdbcTemplate;
    private ArrayList<DVD> currentDVDList;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public DVDDbImpl() {
        this.currentDVDList = new ArrayList<>();
    }

    
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(this.jdbcTemplate);
    }

    private static final String ADD_MOVIE = "insert into Movies (title, releaseDate, mpaaRatingsID, "
            + "directorID, studioID, userRating, trailerURL, coverURL, synopsis, notes) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String ADD_DIRECTOR = "insert ignore into Directors (directorName) values (?)";
    private static final String ADD_STUDIO = "insert ignore into Studios (studioName) values (?)";
    private static final String ADD_WRITER = "insert ignore into Writers (writerName) values (?)";
    private static final String ADD_ACTOR = "insert ignore into Actors (actorName) values (?)";

    private static final String UPDATE_MOVIE = "update Movies set title = ?, releaseDate = ?, mpaaRatingsID = ?, "
            + "directorID = ?, studioID = ?, userRating = ?, trailerURL = ?, coverURL = ?, synopsis = ?, notes = ? where movieID = ?";

    private static final String INSERT_MOVIE_GENRES = "insert into MoviesxGenres (movieID, genreID) values (?, ?)";
    private static final String INSERT_MOVIE_WRITERS = "insert into MoviesxWriters (movieID, writerID) values (?, ?)";
    private static final String INSERT_MOVIE_ACTORS = "insert into MoviesxActors (movieID, actorID) values (?, ?)";

    private static final String DELETE_MOVIE = "delete from Movies where movieID = ?";
    private static final String DELETE_MOVIE_GENRES = "delete from MoviesxGenres where movieID = ?";
    private static final String DELETE_MOVIE_WRITERS = "delete from MoviesxWriters where movieID = ?";
    private static final String DELETE_MOVIE_ACTORS = "delete from MoviesxActors where movieID = ?";

    private static final String GET_MPAA_RATING_ID = "select mpaaRatingsID from MpaaRatings where mpaaRatingsName = ?";
    private static final String GET_DIRECTOR_ID = "select directorID from Directors where directorName = ?";
    private static final String GET_STUDIO_ID = "select studioID from Studios where studioName = ?";
    private static final String GET_ACTOR_ID = "select actorID from Actors where actorName = ?";
    private static final String GET_GENRE_ID = "select genreID from Genres where genreName = ?";
    private static final String GET_WRITER_ID = "select writerID from Writers where writerName = ?";

    private static final String GET_MOVIE = "select m.movieID, m.title, m.releaseDate, mpaa.mpaaRatingsName, d.directorName, s.studioName, m.userRating, m.trailerURL, m.coverURL, m.synopsis, m.notes from Movies m\n"
            + "join Directors d on d.directorID = m.directorID\n"
            + "join Studios s on s.studioID = m.studioID\n"
            + "join MpaaRatings mpaa on mpaa.mpaaRatingsID = m.mpaaRatingsID\n"
            + "where m.movieID = ?";
    private static final String GET_ALL_MOVIES = "select m.movieID, m.title, m.releaseDate, mpaa.mpaaRatingsName, d.directorName, s.studioName, m.userRating, m.trailerURL, m.coverURL, m.synopsis, m.notes from Movies m\n"
            + "join Directors d on d.directorID = m.directorID\n"
            + "join Studios s on s.studioID = m.studioID\n"
            + "join MpaaRatings mpaa on mpaa.mpaaRatingsID = m.mpaaRatingsID order by title asc";
    private static final String GET_MPAA_RATING = "select mpaaRatingsName from MpaaRatings mpaa join Movies m on m.mpaaRatingsID = mpaa.mpaaRatingsID where movieID = ?";
    private static final String GET_DIRECTOR = "select directorName from Directors d join Movies m on m.directorID = d.directorID where movieID = ?";
    private static final String GET_STUDIO = "select studioName from Studios s join Movies m on m.studioID = s.studioID where movieID = ?";
    private static final String GET_GENRES = "select genreName from Genres g join MoviesxGenres mg on g.genreID = mg.genreID where mg.movieID = ?";
    private static final String GET_WRITERS = "select writerName from Writers w join MoviesxWriters mw on w.writerID = mw.writerID where mw.movieID = ?";
    private static final String GET_ACTORS = "select actorName from Actors g join MoviesxActors ma on g.actorID = ma.actorID where ma.movieID = ?";

    private static final String SORT_BY_TITLE_ASC = "select m.movieID, m.title, m.releaseDate, mpaa.mpaaRatingsName, d.directorName, s.studioName, m.userRating, m.trailerURL, m.coverURL, m.synopsis, m.notes from Movies m \n"
            + "join Directors d on d.directorID = m.directorID\n"
            + "join Studios s on s.studioID = m.studioID\n"
            + "join MpaaRatings mpaa on mpaa.mpaaRatingsID = m.mpaaRatingsID where m.movieID in (:ids) order by m.title asc";
    private static final String SORT_BY_TITLE_DESC = "select m.movieID, m.title, m.releaseDate, mpaa.mpaaRatingsName, d.directorName, s.studioName, m.userRating, m.trailerURL, m.coverURL, m.synopsis, m.notes from Movies m \n"
            + "join Directors d on d.directorID = m.directorID\n"
            + "join Studios s on s.studioID = m.studioID\n"
            + "join MpaaRatings mpaa on mpaa.mpaaRatingsID = m.mpaaRatingsID where m.movieID in (:ids) order by m.title desc";
    private static final String SORT_BY_YEAR_ASC = "select m.movieID, m.title, m.releaseDate, mpaa.mpaaRatingsName, d.directorName, s.studioName, m.userRating, m.trailerURL, m.coverURL, m.synopsis, m.notes from Movies m \n"
            + "join Directors d on d.directorID = m.directorID\n"
            + "join Studios s on s.studioID = m.studioID\n"
            + "join MpaaRatings mpaa on mpaa.mpaaRatingsID = m.mpaaRatingsID where m.movieID in (:ids) order by m.releaseDate asc, m.title asc";
    private static final String SORT_BY_YEAR_DESC = "select m.movieID, m.title, m.releaseDate, mpaa.mpaaRatingsName, d.directorName, s.studioName, m.userRating, m.trailerURL, m.coverURL, m.synopsis, m.notes from Movies m \n"
            + "join Directors d on d.directorID = m.directorID\n"
            + "join Studios s on s.studioID = m.studioID\n"
            + "join MpaaRatings mpaa on mpaa.mpaaRatingsID = m.mpaaRatingsID where m.movieID in (:ids) order by m.releaseDate desc, m.title asc";
    private static final String SORT_BY_MPAA_ASC = "select m.movieID, m.title, m.releaseDate, mpaa.mpaaRatingsName, d.directorName, s.studioName, m.userRating, m.trailerURL, m.coverURL, m.synopsis, m.notes from Movies m \n"
            + "join Directors d on d.directorID = m.directorID\n"
            + "join Studios s on s.studioID = m.studioID\n"
            + "join MpaaRatings mpaa on mpaa.mpaaRatingsID = m.mpaaRatingsID where m.movieID in (:ids) order by mpaa.mpaaRatingsID asc, m.title asc";
    private static final String SORT_BY_MPAA_DESC = "select m.movieID, m.title, m.releaseDate, mpaa.mpaaRatingsName, d.directorName, s.studioName, m.userRating, m.trailerURL, m.coverURL, m.synopsis, m.notes from Movies m \n"
            + "join Directors d on d.directorID = m.directorID\n"
            + "join Studios s on s.studioID = m.studioID\n"
            + "join MpaaRatings mpaa on mpaa.mpaaRatingsID = m.mpaaRatingsID where m.movieID in (:ids) order by mpaa.mpaaRatingsID desc, m.title asc";
    private static final String SORT_BY_USER_ASC = "select m.movieID, m.title, m.releaseDate, mpaa.mpaaRatingsName, d.directorName, s.studioName, m.userRating, m.trailerURL, m.coverURL, m.synopsis, m.notes from Movies m \n"
            + "join Directors d on d.directorID = m.directorID\n"
            + "join Studios s on s.studioID = m.studioID\n"
            + "join MpaaRatings mpaa on mpaa.mpaaRatingsID = m.mpaaRatingsID where m.movieID in (:ids) order by m.userRating asc, m.title asc";
    private static final String SORT_BY_USER_DESC = "select m.movieID, m.title, m.releaseDate, mpaa.mpaaRatingsName, d.directorName, s.studioName, m.userRating, m.trailerURL, m.coverURL, m.synopsis, m.notes from Movies m \n"
            + "join Directors d on d.directorID = m.directorID\n"
            + "join Studios s on s.studioID = m.studioID\n"
            + "join MpaaRatings mpaa on mpaa.mpaaRatingsID = m.mpaaRatingsID where m.movieID in (:ids) order by m.userRating desc, m.title asc";

    private static final String SEARCH_BY_TITLE = "select distinct m.movieID, m.title, m.releaseDate, mpaa.mpaaRatingsName, d.directorName, s.studioName, m.userRating, m.trailerURL, m.coverURL, m.synopsis, m.notes from Movies m \n"
            + "join Directors d on d.directorID = m.directorID\n"
            + "join Studios s on s.studioID = m.studioID\n"
            + "join MpaaRatings mpaa on mpaa.mpaaRatingsID = m.mpaaRatingsID where m.title like ? order by title asc";
    private static final String SEARCH_BY_YEAR = "select distinct m.movieID, m.title, m.releaseDate, mpaa.mpaaRatingsName, d.directorName, s.studioName, m.userRating, m.trailerURL, m.coverURL, m.synopsis, m.notes from Movies m \n"
            + "join Directors d on d.directorID = m.directorID\n"
            + "join Studios s on s.studioID = m.studioID\n"
            + "join MpaaRatings mpaa on mpaa.mpaaRatingsID = m.mpaaRatingsID where m.releaseDate = ? order by title asc";
    private static final String SEARCH_BY_DIRECTOR = "select distinct m.movieID, m.title, m.releaseDate, mpaa.mpaaRatingsName, d.directorName, s.studioName, m.userRating, m.trailerURL, m.coverURL, m.synopsis, m.notes from Movies m \n"
            + "join Directors d on d.directorID = m.directorID "
            + "join Studios s on s.studioID = m.studioID "
            + "join MpaaRatings mpaa on mpaa.mpaaRatingsID = m.mpaaRatingsID "
            + "where d.directorName like ? order by title asc";
    private static final String SEARCH_BY_STUDIO = "select distinct m.movieID, m.title, m.releaseDate, mpaa.mpaaRatingsName, d.directorName, s.studioName, m.userRating, m.trailerURL, m.coverURL, m.synopsis, m.notes from Movies m \n"
            + "join Directors d on d.directorID = m.directorID "
            + "join Studios s on s.studioID = m.studioID "
            + "join MpaaRatings mpaa on mpaa.mpaaRatingsID = m.mpaaRatingsID "
            + "where s.studioName like ? order by title asc";
    private static final String SEARCH_BY_MPAA = "select distinct m.movieID, m.title, m.releaseDate, mpaa.mpaaRatingsName, d.directorName, s.studioName, m.userRating, m.trailerURL, m.coverURL, m.synopsis, m.notes from Movies m \n"
            + "join Directors d on d.directorID = m.directorID \n"
            + "join Studios s on s.studioID = m.studioID \n"
            + "join MpaaRatings mpaa on mpaa.mpaaRatingsID = m.mpaaRatingsID "
            + "where mpaa.mpaaRatingsName = ? order by title asc";
    private static final String SEARCH_BY_GENRE = "select distinct m.movieID, m.title, m.releaseDate, mpaa.mpaaRatingsName, d.directorName, s.studioName, m.userRating, m.trailerURL, m.coverURL, m.synopsis, m.notes from Movies m \n"
            + "join Directors d on d.directorID = m.directorID \n"
            + "join Studios s on s.studioID = m.studioID \n"
            + "join MpaaRatings mpaa on mpaa.mpaaRatingsID = m.mpaaRatingsID "
            + "join MoviesxGenres mg on mg.movieID = m.movieID "
            + "join Genres g on mg.genreID = g.genreID where g.genreName = ? order by title asc";
    private static final String SEARCH_BY_WRITER = "select distinct m.movieID, m.title, m.releaseDate, mpaa.mpaaRatingsName, d.directorName, s.studioName, m.userRating, m.trailerURL, m.coverURL, m.synopsis, m.notes from Movies m \n"
            + "join Directors d on d.directorID = m.directorID \n"
            + "join Studios s on s.studioID = m.studioID \n"
            + "join MpaaRatings mpaa on mpaa.mpaaRatingsID = m.mpaaRatingsID "
            + "join MoviesxWriters mw on mw.movieID = m.movieID "
            + "join Writers w on mw.writerID = w.writerID where w.writerName like ? order by title asc";
    private static final String SEARCH_BY_ACTOR = "select distinct m.movieID, m.title, m.releaseDate, mpaa.mpaaRatingsName, d.directorName, s.studioName, m.userRating, m.trailerURL, m.coverURL, m.synopsis, m.notes from Movies m \n"
            + "join Directors d on d.directorID = m.directorID\n"
            + "join Studios s on s.studioID = m.studioID\n"
            + "join MpaaRatings mpaa on mpaa.mpaaRatingsID = m.mpaaRatingsID "
            + "join MoviesxActors mg on mg.movieID = m.movieID "
            + "join Actors g on mg.actorID = g.actorID where g.actorName like ? order by title asc";
    private static final String SEARCH_BY_KEYWORD = "select distinct m.movieID, m.title, m.releaseDate, mpaa.mpaaRatingsName, d.directorName, s.studioName, m.userRating, m.trailerURL, m.coverURL, m.synopsis, m.notes from Movies m \n"
            + "join Directors d on d.directorID = m.directorID\n"
            + "join Studios s on s.studioID = m.studioID\n"
            + "join MpaaRatings mpaa on mpaa.mpaaRatingsID = m.mpaaRatingsID "
            + "join MoviesxWriters mw on mw.movieID = m.movieID "
            + "join MoviesxActors ma on ma.movieID = m.movieID "
            + "join Writers w on mw.writerID = w.writerID "
            + "join Actors a on ma.actorID = a.actorID "
            + "where m.title like ? or d.directorName like ? or w.writerName like ? or a.actorName like ? "
            + "order by title asc";
    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public DVD addDVD(DVD dvd) {
        jdbcTemplate.update(ADD_MOVIE,
                dvd.getTitle(),
                dvd.getReleaseDate(),
                getMpaaRatingsID(dvd),
                getDirectorID(dvd),
                getStudioID(dvd),
                dvd.getUserRating(),
                dvd.getTrailerURL(),
                dvd.getCoverURL(),
                dvd.getSynopsis(),
                dvd.getNotes());
        dvd.setId(jdbcTemplate.queryForObject("select LAST_INSERT_ID()", Integer.class));
        addWriters(dvd);
        addActors(dvd);
        insertMovieGenres(dvd);
        insertMovieWriters(dvd);
        insertMovieActors(dvd);
        return dvd;
    }

    @Override
    public void removeDVD(int id) {
        jdbcTemplate.update(DELETE_MOVIE_GENRES, id);
        jdbcTemplate.update(DELETE_MOVIE_WRITERS, id);
        jdbcTemplate.update(DELETE_MOVIE_ACTORS, id);
        jdbcTemplate.update(DELETE_MOVIE, id);
    }

    @Override
    public DVD viewDVD(int id) {
        try {
            DVD dvd = jdbcTemplate.queryForObject(GET_MOVIE, new MovieMapper(), id);
            dvd.setGenreList((ArrayList<String>) jdbcTemplate.queryForList(GET_GENRES, new Integer[]{id}, String.class));
            dvd.setWriterList((ArrayList<String>) jdbcTemplate.queryForList(GET_WRITERS, new Integer[]{id}, String.class));
            dvd.setActorList((ArrayList<String>) jdbcTemplate.queryForList(GET_ACTORS, new Integer[]{id}, String.class));
            return dvd;
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public void updateDVD(DVD dvd) {
        jdbcTemplate.update(UPDATE_MOVIE,
                dvd.getTitle(),
                dvd.getReleaseDate(),
                getMpaaRatingsID(dvd),
                getDirectorID(dvd),
                getStudioID(dvd),
                dvd.getUserRating(),
                dvd.getTrailerURL(),
                dvd.getCoverURL(),
                dvd.getSynopsis(),
                dvd.getNotes(),
                dvd.getId());
        addWriters(dvd);
        addActors(dvd);
        jdbcTemplate.update(DELETE_MOVIE_GENRES, dvd.getId());
        jdbcTemplate.update(DELETE_MOVIE_WRITERS, dvd.getId());
        jdbcTemplate.update(DELETE_MOVIE_ACTORS, dvd.getId());
        insertMovieGenres(dvd);
        insertMovieWriters(dvd);
        insertMovieActors(dvd);
    }

    @Override
    public ArrayList<DVD> getAllDVDs() {
        currentDVDList.clear();
        try {
            List<DVD> allDVDs = jdbcTemplate.query(GET_ALL_MOVIES, new MovieMapper());
            for (DVD dvd : allDVDs) {
                dvd.setGenreList((ArrayList<String>) jdbcTemplate.queryForList(GET_GENRES, new Integer[]{dvd.getId()}, String.class));
                dvd.setWriterList((ArrayList<String>) jdbcTemplate.queryForList(GET_WRITERS, new Integer[]{dvd.getId()}, String.class));
                dvd.setActorList((ArrayList<String>) jdbcTemplate.queryForList(GET_ACTORS, new Integer[]{dvd.getId()}, String.class));
                currentDVDList.add(dvd);
            }
            return currentDVDList;
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public ArrayList<DVD> sortByTitle(boolean defaultSort) {
        String sortDirection = (defaultSort) ? SORT_BY_TITLE_DESC : SORT_BY_TITLE_ASC;
        List<Integer> currentDVDIDs = currentDVDList.stream().map(DVD::getId).collect(Collectors.toList());
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("ids", currentDVDIDs);
        String ascdesc = (defaultSort) ? "asc" : "desc";
        currentDVDList.clear();
        try {
            List<DVD> allDVDs = this.namedParameterJdbcTemplate.query(sortDirection, params, new MovieMapper());
            for (DVD dvd : allDVDs) {
                dvd.setGenreList((ArrayList<String>) jdbcTemplate.queryForList(GET_GENRES, new Integer[]{dvd.getId()}, String.class));
                dvd.setWriterList((ArrayList<String>) jdbcTemplate.queryForList(GET_WRITERS, new Integer[]{dvd.getId()}, String.class));
                dvd.setActorList((ArrayList<String>) jdbcTemplate.queryForList(GET_ACTORS, new Integer[]{dvd.getId()}, String.class));
                currentDVDList.add(dvd);
            }
            return currentDVDList;
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public ArrayList<DVD> sortByYear(boolean defaultSort) {
        String sortDirection = (defaultSort) ? SORT_BY_YEAR_ASC : SORT_BY_YEAR_DESC;
        List<Integer> currentDVDIDs = currentDVDList.stream().map(DVD::getId).collect(Collectors.toList());
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("ids", currentDVDIDs);
        currentDVDList.clear();
        try {
            List<DVD> allDVDs = this.namedParameterJdbcTemplate.query(sortDirection, params, new MovieMapper());
            for (DVD dvd : allDVDs) {
                dvd.setGenreList((ArrayList<String>) jdbcTemplate.queryForList(GET_GENRES, new Integer[]{dvd.getId()}, String.class));
                dvd.setWriterList((ArrayList<String>) jdbcTemplate.queryForList(GET_WRITERS, new Integer[]{dvd.getId()}, String.class));
                dvd.setActorList((ArrayList<String>) jdbcTemplate.queryForList(GET_ACTORS, new Integer[]{dvd.getId()}, String.class));
                currentDVDList.add(dvd);
            }
            return currentDVDList;
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public ArrayList<DVD> sortByMPAARating(boolean defaultSort) {
        String sortDirection = (defaultSort) ? SORT_BY_MPAA_DESC : SORT_BY_MPAA_ASC;
        List<Integer> currentDVDIDs = currentDVDList.stream().map(DVD::getId).collect(Collectors.toList());
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("ids", currentDVDIDs);
        currentDVDList.clear();
        try {
            List<DVD> allDVDs = this.namedParameterJdbcTemplate.query(sortDirection, params, new MovieMapper());
            for (DVD dvd : allDVDs) {
                dvd.setGenreList((ArrayList<String>) jdbcTemplate.queryForList(GET_GENRES, new Integer[]{dvd.getId()}, String.class));
                dvd.setWriterList((ArrayList<String>) jdbcTemplate.queryForList(GET_WRITERS, new Integer[]{dvd.getId()}, String.class));
                dvd.setActorList((ArrayList<String>) jdbcTemplate.queryForList(GET_ACTORS, new Integer[]{dvd.getId()}, String.class));
                currentDVDList.add(dvd);
            }
            return currentDVDList;
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public ArrayList<DVD> sortByUserRating(boolean defaultSort) {
        String sortDirection = (!defaultSort) ? SORT_BY_USER_DESC : SORT_BY_USER_ASC;
        List<Integer> currentDVDIDs = currentDVDList.stream().map(DVD::getId).collect(Collectors.toList());
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("ids", currentDVDIDs);
        currentDVDList.clear();
        try {
            List<DVD> allDVDs = this.namedParameterJdbcTemplate.query(sortDirection, params, new MovieMapper());
            for (DVD dvd : allDVDs) {
                dvd.setGenreList((ArrayList<String>) jdbcTemplate.queryForList(GET_GENRES, new Integer[]{dvd.getId()}, String.class));
                dvd.setWriterList((ArrayList<String>) jdbcTemplate.queryForList(GET_WRITERS, new Integer[]{dvd.getId()}, String.class));
                dvd.setActorList((ArrayList<String>) jdbcTemplate.queryForList(GET_ACTORS, new Integer[]{dvd.getId()}, String.class));
                currentDVDList.add(dvd);
            }
            return currentDVDList;
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public ArrayList<DVD> getDVDsWithTitle(String searchTerm) {
        String title = "%" + searchTerm + "%";
        currentDVDList.clear();
        try {
            List<DVD> allDVDs = jdbcTemplate.query(SEARCH_BY_TITLE, new MovieMapper(), title);
            for (DVD dvd : allDVDs) {
                dvd.setGenreList((ArrayList<String>) jdbcTemplate.queryForList(GET_GENRES, new Integer[]{dvd.getId()}, String.class));
                dvd.setWriterList((ArrayList<String>) jdbcTemplate.queryForList(GET_WRITERS, new Integer[]{dvd.getId()}, String.class));
                dvd.setActorList((ArrayList<String>) jdbcTemplate.queryForList(GET_ACTORS, new Integer[]{dvd.getId()}, String.class));
                currentDVDList.add(dvd);
            }
            return currentDVDList;
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public ArrayList<DVD> getDVDsWithReleaseDate(int releaseDate) {
        currentDVDList.clear();
        try {
            List<DVD> allDVDs = jdbcTemplate.query(SEARCH_BY_YEAR, new MovieMapper(), releaseDate);
            for (DVD dvd : allDVDs) {
                dvd.setGenreList((ArrayList<String>) jdbcTemplate.queryForList(GET_GENRES, new Integer[]{dvd.getId()}, String.class));
                dvd.setWriterList((ArrayList<String>) jdbcTemplate.queryForList(GET_WRITERS, new Integer[]{dvd.getId()}, String.class));
                dvd.setActorList((ArrayList<String>) jdbcTemplate.queryForList(GET_ACTORS, new Integer[]{dvd.getId()}, String.class));
                currentDVDList.add(dvd);
            }
            return currentDVDList;
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public ArrayList<DVD> getDVDsWithGenre(String genre) {
        currentDVDList.clear();
        try {
            List<DVD> allDVDs = jdbcTemplate.query(SEARCH_BY_GENRE, new MovieMapper(), genre);
            for (DVD dvd : allDVDs) {
                dvd.setGenreList((ArrayList<String>) jdbcTemplate.queryForList(GET_GENRES, new Integer[]{dvd.getId()}, String.class));
                dvd.setWriterList((ArrayList<String>) jdbcTemplate.queryForList(GET_WRITERS, new Integer[]{dvd.getId()}, String.class));
                dvd.setActorList((ArrayList<String>) jdbcTemplate.queryForList(GET_ACTORS, new Integer[]{dvd.getId()}, String.class));
                currentDVDList.add(dvd);
            }
            return currentDVDList;
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public ArrayList<DVD> getDVDsWithDirector(String searchTerm) {
        String director = "%" + searchTerm + "%";
        currentDVDList.clear();
        try {
            List<DVD> allDVDs = jdbcTemplate.query(SEARCH_BY_DIRECTOR, new MovieMapper(), director);
            for (DVD dvd : allDVDs) {
                dvd.setGenreList((ArrayList<String>) jdbcTemplate.queryForList(GET_GENRES, new Integer[]{dvd.getId()}, String.class));
                dvd.setWriterList((ArrayList<String>) jdbcTemplate.queryForList(GET_WRITERS, new Integer[]{dvd.getId()}, String.class));
                dvd.setActorList((ArrayList<String>) jdbcTemplate.queryForList(GET_ACTORS, new Integer[]{dvd.getId()}, String.class));
                currentDVDList.add(dvd);
            }
            return currentDVDList;
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public ArrayList<DVD> getDVDsWithWriter(String searchTerm) {
        String writer = "%" + searchTerm + "%";
        currentDVDList.clear();
        try {
            List<DVD> allDVDs = jdbcTemplate.query(SEARCH_BY_WRITER, new MovieMapper(), writer);
            for (DVD dvd : allDVDs) {
                dvd.setGenreList((ArrayList<String>) jdbcTemplate.queryForList(GET_GENRES, new Integer[]{dvd.getId()}, String.class));
                dvd.setWriterList((ArrayList<String>) jdbcTemplate.queryForList(GET_WRITERS, new Integer[]{dvd.getId()}, String.class));
                dvd.setActorList((ArrayList<String>) jdbcTemplate.queryForList(GET_ACTORS, new Integer[]{dvd.getId()}, String.class));
                currentDVDList.add(dvd);
            }
            return currentDVDList;
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public ArrayList<DVD> getDVDsWithActor(String searchTerm) {
        String actor = "%" + searchTerm + "%";
        currentDVDList.clear();
        try {
            List<DVD> allDVDs = jdbcTemplate.query(SEARCH_BY_ACTOR, new MovieMapper(), actor);
            for (DVD dvd : allDVDs) {
                dvd.setGenreList((ArrayList<String>) jdbcTemplate.queryForList(GET_GENRES, new Integer[]{dvd.getId()}, String.class));
                dvd.setWriterList((ArrayList<String>) jdbcTemplate.queryForList(GET_WRITERS, new Integer[]{dvd.getId()}, String.class));
                dvd.setActorList((ArrayList<String>) jdbcTemplate.queryForList(GET_ACTORS, new Integer[]{dvd.getId()}, String.class));
                currentDVDList.add(dvd);
            }
            return currentDVDList;
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public ArrayList<DVD> getDVDsFromStudio(String searchTerm) {
        String studio = "%" + searchTerm + "%";
        currentDVDList.clear();
        try {
            List<DVD> allDVDs = jdbcTemplate.query(SEARCH_BY_STUDIO, new MovieMapper(), studio);
            for (DVD dvd : allDVDs) {
                dvd.setGenreList((ArrayList<String>) jdbcTemplate.queryForList(GET_GENRES, new Integer[]{dvd.getId()}, String.class));
                dvd.setWriterList((ArrayList<String>) jdbcTemplate.queryForList(GET_WRITERS, new Integer[]{dvd.getId()}, String.class));
                dvd.setActorList((ArrayList<String>) jdbcTemplate.queryForList(GET_ACTORS, new Integer[]{dvd.getId()}, String.class));
                currentDVDList.add(dvd);
            }
            return currentDVDList;
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public ArrayList<DVD> getDVDsWithMPAARating(String rating) {
        currentDVDList.clear();
        try {
            List<DVD> allDVDs = jdbcTemplate.query(SEARCH_BY_MPAA, new MovieMapper(), rating);
            for (DVD dvd : allDVDs) {
                dvd.setGenreList((ArrayList<String>) jdbcTemplate.queryForList(GET_GENRES, new Integer[]{dvd.getId()}, String.class));
                dvd.setWriterList((ArrayList<String>) jdbcTemplate.queryForList(GET_WRITERS, new Integer[]{dvd.getId()}, String.class));
                dvd.setActorList((ArrayList<String>) jdbcTemplate.queryForList(GET_ACTORS, new Integer[]{dvd.getId()}, String.class));
                currentDVDList.add(dvd);
            }
            return currentDVDList;
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public ArrayList<DVD> getDVDsByKeyword(String searchTerm) {
        String search = "%" + searchTerm + "%";
        currentDVDList.clear();
        try {
            List<DVD> allDVDs = jdbcTemplate.query(SEARCH_BY_KEYWORD, new MovieMapper(), search, search, search, search);
            for (DVD dvd : allDVDs) {
                dvd.setGenreList((ArrayList<String>) jdbcTemplate.queryForList(GET_GENRES, new Integer[]{dvd.getId()}, String.class));
                dvd.setWriterList((ArrayList<String>) jdbcTemplate.queryForList(GET_WRITERS, new Integer[]{dvd.getId()}, String.class));
                dvd.setActorList((ArrayList<String>) jdbcTemplate.queryForList(GET_ACTORS, new Integer[]{dvd.getId()}, String.class));
                currentDVDList.add(dvd);
            }
            return currentDVDList;
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    private void addActors(DVD dvd) {
        final ArrayList<String> actorList = dvd.getActorList();

        jdbcTemplate.batchUpdate(ADD_ACTOR, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, actorList.get(i));
            }

            @Override
            public int getBatchSize() {
                return actorList.size();
            }
        });
    }

    private void addWriters(DVD dvd) {
        final ArrayList<String> writerList = dvd.getWriterList();

        jdbcTemplate.batchUpdate(ADD_WRITER, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {

                ps.setString(1, writerList.get(i));
            }

            @Override
            public int getBatchSize() {
                return writerList.size();
            }
        });
    }

    private void insertMovieGenres(DVD dvd) {
        final int movieID = dvd.getId();
        final List<Integer> genreIDs = getGenreIDs(dvd);
        jdbcTemplate.batchUpdate(INSERT_MOVIE_GENRES, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, movieID);
                ps.setInt(2, genreIDs.get(i));
            }

            @Override
            public int getBatchSize() {
                return genreIDs.size();
            }
        });
    }

    private void insertMovieWriters(DVD dvd) {
        final int movieID = dvd.getId();
        final List<Integer> writerIDs = getWriterIDs(dvd);
        jdbcTemplate.batchUpdate(INSERT_MOVIE_WRITERS, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, movieID);
                ps.setInt(2, writerIDs.get(i));
            }

            @Override
            public int getBatchSize() {
                return writerIDs.size();
            }
        });
    }

    private void insertMovieActors(DVD dvd) {
        final int movieID = dvd.getId();
        final List<Integer> actorIDs = getActorIds(dvd);
        jdbcTemplate.batchUpdate(INSERT_MOVIE_ACTORS, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, movieID);
                ps.setInt(2, actorIDs.get(i));
            }

            @Override
            public int getBatchSize() {
                return actorIDs.size();
            }
        });
    }

    private Integer getMpaaRatingsID(DVD dvd) {
        String mpaaRating = dvd.getMpaaRating();
        try {
            return jdbcTemplate.queryForObject(GET_MPAA_RATING_ID, Integer.class, mpaaRating);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    private Integer getDirectorID(DVD dvd) {
        String director = dvd.getDirector();
        jdbcTemplate.update(ADD_DIRECTOR,
                director);
        return jdbcTemplate.queryForObject(GET_DIRECTOR_ID, Integer.class, director);
    }

    private Integer getStudioID(DVD dvd) {
        String studio = dvd.getStudio();
        jdbcTemplate.update(ADD_STUDIO,
                studio);
        return jdbcTemplate.queryForObject(GET_STUDIO_ID, Integer.class, studio);

    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    private List<Integer> getGenreIDs(DVD dvd) {

        List<String> genreList = dvd.getGenres();
        List<Integer> genreIDs = new ArrayList<>();
        for (String genre : genreList) {
            try {
                genreIDs.add(jdbcTemplate.queryForObject(GET_GENRE_ID, Integer.class, genre));
            } catch (EmptyResultDataAccessException ex) {
            }
        }
        return genreIDs;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    private List<Integer> getWriterIDs(DVD dvd) {

        List<String> writerList = dvd.getWriterList();
        List<Integer> writerIDs = new ArrayList<>();
        for (String writerName : writerList) {
            try {
                writerIDs.add(jdbcTemplate.queryForObject(GET_WRITER_ID, Integer.class, writerName));
            } catch (EmptyResultDataAccessException ex) {
                jdbcTemplate.update(ADD_WRITER, writerName);
                writerIDs.add(jdbcTemplate.queryForObject("select LAST_INSERT_ID()", Integer.class));
            }
        }
        return writerIDs;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    private List<Integer> getActorIds(DVD dvd) {

        List<String> actorList = dvd.getActorList();
        List<Integer> actorIDs = new ArrayList<>();
        for (String actorName : actorList) {
            try {
                actorIDs.add(jdbcTemplate.queryForObject(GET_ACTOR_ID, Integer.class, actorName));
            } catch (EmptyResultDataAccessException ex) {
                jdbcTemplate.update(ADD_ACTOR, actorName);
                actorIDs.add(jdbcTemplate.queryForObject("select LAST_INSERT_ID()", Integer.class));
            }
        }
        return actorIDs;
    }

    private static final class MovieMapper implements RowMapper<DVD> {

        @Override
        public DVD mapRow(ResultSet rs, int i) throws SQLException {
            DVD dvd = new DVD();
            dvd.setId(rs.getInt("movieID"));
            dvd.setTitle(rs.getString("title"));
            dvd.setReleaseDate(rs.getInt("releaseDate"));
            dvd.setUserRating(rs.getInt("userRating"));
            dvd.setTrailerURL(rs.getString("trailerURL"));
            dvd.setCoverURL(rs.getString("coverURL"));
            dvd.setSynopsis(rs.getString("synopsis"));
            dvd.setMpaaRating(rs.getString("mpaaRatingsName"));
            dvd.setDirector(rs.getString("directorName"));
            dvd.setStudio(rs.getString("studioName"));
            dvd.setNotes(rs.getString("notes"));
            return dvd;
        }
    }
}
