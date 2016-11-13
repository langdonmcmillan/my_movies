/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.mymovies.dao;

import com.sg.mymovies.models.Movie;
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
public class MyMoviesDbImpl implements MyMoviesDAO {

    private JdbcTemplate jdbcTemplate;
    private ArrayList<Movie> currentMovieList;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public MyMoviesDbImpl() {
        this.currentMovieList = new ArrayList<>();
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
    public Movie addMovie(Movie movie) {
        jdbcTemplate.update(ADD_MOVIE,
                movie.getTitle(),
                movie.getReleaseDate(),
                getMpaaRatingsID(movie),
                getDirectorID(movie),
                getStudioID(movie),
                movie.getUserRating(),
                movie.getTrailerURL(),
                movie.getCoverURL(),
                movie.getSynopsis(),
                movie.getNotes());
        movie.setId(jdbcTemplate.queryForObject("select LAST_INSERT_ID()", Integer.class));
        addWriters(movie);
        addActors(movie);
        insertMovieGenres(movie);
        insertMovieWriters(movie);
        insertMovieActors(movie);
        return movie;
    }

    @Override
    public void removeMovie(int id) {
        jdbcTemplate.update(DELETE_MOVIE_GENRES, id);
        jdbcTemplate.update(DELETE_MOVIE_WRITERS, id);
        jdbcTemplate.update(DELETE_MOVIE_ACTORS, id);
        jdbcTemplate.update(DELETE_MOVIE, id);
    }

    @Override
    public Movie viewMovie(int id) {
        try {
            Movie movie = jdbcTemplate.queryForObject(GET_MOVIE, new MovieMapper(), id);
            movie.setGenreList((ArrayList<String>) jdbcTemplate.queryForList(GET_GENRES, new Integer[]{id}, String.class));
            movie.setWriterList((ArrayList<String>) jdbcTemplate.queryForList(GET_WRITERS, new Integer[]{id}, String.class));
            movie.setActorList((ArrayList<String>) jdbcTemplate.queryForList(GET_ACTORS, new Integer[]{id}, String.class));
            return movie;
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public void updateMovie(Movie movie) {
        jdbcTemplate.update(UPDATE_MOVIE,
                movie.getTitle(),
                movie.getReleaseDate(),
                getMpaaRatingsID(movie),
                getDirectorID(movie),
                getStudioID(movie),
                movie.getUserRating(),
                movie.getTrailerURL(),
                movie.getCoverURL(),
                movie.getSynopsis(),
                movie.getNotes(),
                movie.getId());
        addWriters(movie);
        addActors(movie);
        jdbcTemplate.update(DELETE_MOVIE_GENRES, movie.getId());
        jdbcTemplate.update(DELETE_MOVIE_WRITERS, movie.getId());
        jdbcTemplate.update(DELETE_MOVIE_ACTORS, movie.getId());
        insertMovieGenres(movie);
        insertMovieWriters(movie);
        insertMovieActors(movie);
    }

    @Override
    public ArrayList<Movie> getAllMovies() {
        currentMovieList.clear();
        try {
            List<Movie> allMovies = jdbcTemplate.query(GET_ALL_MOVIES, new MovieMapper());
            for (Movie movie : allMovies) {
                movie.setGenreList((ArrayList<String>) jdbcTemplate.queryForList(GET_GENRES, new Integer[]{movie.getId()}, String.class));
                movie.setWriterList((ArrayList<String>) jdbcTemplate.queryForList(GET_WRITERS, new Integer[]{movie.getId()}, String.class));
                movie.setActorList((ArrayList<String>) jdbcTemplate.queryForList(GET_ACTORS, new Integer[]{movie.getId()}, String.class));
                currentMovieList.add(movie);
            }
            return currentMovieList;
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public ArrayList<Movie> sortByTitle(boolean alreadySorted) {
        String sortDirection = (alreadySorted) ? SORT_BY_TITLE_DESC : SORT_BY_TITLE_ASC;
        List<Integer> currentMovieIDs = currentMovieList.stream().map(Movie::getId).collect(Collectors.toList());
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("ids", currentMovieIDs);
        currentMovieList.clear();
        try {
            List<Movie> allMovies = this.namedParameterJdbcTemplate.query(sortDirection, params, new MovieMapper());
            for (Movie movie : allMovies) {
                movie.setGenreList((ArrayList<String>) jdbcTemplate.queryForList(GET_GENRES, new Integer[]{movie.getId()}, String.class));
                movie.setWriterList((ArrayList<String>) jdbcTemplate.queryForList(GET_WRITERS, new Integer[]{movie.getId()}, String.class));
                movie.setActorList((ArrayList<String>) jdbcTemplate.queryForList(GET_ACTORS, new Integer[]{movie.getId()}, String.class));
                currentMovieList.add(movie);
            }
            return currentMovieList;
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public ArrayList<Movie> sortByYear(boolean alreadySorted) {
        String sortDirection = (alreadySorted) ? SORT_BY_YEAR_ASC : SORT_BY_YEAR_DESC;
        List<Integer> currentMovieIDs = currentMovieList.stream().map(Movie::getId).collect(Collectors.toList());
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("ids", currentMovieIDs);
        currentMovieList.clear();
        try {
            List<Movie> allMovies = this.namedParameterJdbcTemplate.query(sortDirection, params, new MovieMapper());
            for (Movie movie : allMovies) {
                movie.setGenreList((ArrayList<String>) jdbcTemplate.queryForList(GET_GENRES, new Integer[]{movie.getId()}, String.class));
                movie.setWriterList((ArrayList<String>) jdbcTemplate.queryForList(GET_WRITERS, new Integer[]{movie.getId()}, String.class));
                movie.setActorList((ArrayList<String>) jdbcTemplate.queryForList(GET_ACTORS, new Integer[]{movie.getId()}, String.class));
                currentMovieList.add(movie);
            }
            return currentMovieList;
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public ArrayList<Movie> sortByMPAARating(boolean alreadySorted) {
        String sortDirection = (alreadySorted) ? SORT_BY_MPAA_DESC : SORT_BY_MPAA_ASC;
        List<Integer> currentMovieIDs = currentMovieList.stream().map(Movie::getId).collect(Collectors.toList());
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("ids", currentMovieIDs);
        currentMovieList.clear();
        try {
            List<Movie> allMovies = this.namedParameterJdbcTemplate.query(sortDirection, params, new MovieMapper());
            for (Movie movie : allMovies) {
                movie.setGenreList((ArrayList<String>) jdbcTemplate.queryForList(GET_GENRES, new Integer[]{movie.getId()}, String.class));
                movie.setWriterList((ArrayList<String>) jdbcTemplate.queryForList(GET_WRITERS, new Integer[]{movie.getId()}, String.class));
                movie.setActorList((ArrayList<String>) jdbcTemplate.queryForList(GET_ACTORS, new Integer[]{movie.getId()}, String.class));
                currentMovieList.add(movie);
            }
            return currentMovieList;
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public ArrayList<Movie> sortByUserRating(boolean alreadySorted) {
        String sortDirection = (alreadySorted) ? SORT_BY_USER_ASC : SORT_BY_USER_DESC;
        List<Integer> currentMovieIDs = currentMovieList.stream().map(Movie::getId).collect(Collectors.toList());
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("ids", currentMovieIDs);
        currentMovieList.clear();
        try {
            List<Movie> allMovies = this.namedParameterJdbcTemplate.query(sortDirection, params, new MovieMapper());
            for (Movie movie : allMovies) {
                movie.setGenreList((ArrayList<String>) jdbcTemplate.queryForList(GET_GENRES, new Integer[]{movie.getId()}, String.class));
                movie.setWriterList((ArrayList<String>) jdbcTemplate.queryForList(GET_WRITERS, new Integer[]{movie.getId()}, String.class));
                movie.setActorList((ArrayList<String>) jdbcTemplate.queryForList(GET_ACTORS, new Integer[]{movie.getId()}, String.class));
                currentMovieList.add(movie);
            }
            return currentMovieList;
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public ArrayList<Movie> getMoviesWithTitle(String searchTerm) {
        String title = "%" + searchTerm + "%";
        currentMovieList.clear();
        try {
            List<Movie> allMovies = jdbcTemplate.query(SEARCH_BY_TITLE, new MovieMapper(), title);
            for (Movie movie : allMovies) {
                movie.setGenreList((ArrayList<String>) jdbcTemplate.queryForList(GET_GENRES, new Integer[]{movie.getId()}, String.class));
                movie.setWriterList((ArrayList<String>) jdbcTemplate.queryForList(GET_WRITERS, new Integer[]{movie.getId()}, String.class));
                movie.setActorList((ArrayList<String>) jdbcTemplate.queryForList(GET_ACTORS, new Integer[]{movie.getId()}, String.class));
                currentMovieList.add(movie);
            }
            return currentMovieList;
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public ArrayList<Movie> getMoviesWithReleaseDate(int releaseDate) {
        currentMovieList.clear();
        try {
            List<Movie> allMovies = jdbcTemplate.query(SEARCH_BY_YEAR, new MovieMapper(), releaseDate);
            for (Movie movie : allMovies) {
                movie.setGenreList((ArrayList<String>) jdbcTemplate.queryForList(GET_GENRES, new Integer[]{movie.getId()}, String.class));
                movie.setWriterList((ArrayList<String>) jdbcTemplate.queryForList(GET_WRITERS, new Integer[]{movie.getId()}, String.class));
                movie.setActorList((ArrayList<String>) jdbcTemplate.queryForList(GET_ACTORS, new Integer[]{movie.getId()}, String.class));
                currentMovieList.add(movie);
            }
            return currentMovieList;
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public ArrayList<Movie> getMoviesWithGenre(String genre) {
        currentMovieList.clear();
        try {
            List<Movie> allMovies = jdbcTemplate.query(SEARCH_BY_GENRE, new MovieMapper(), genre);
            for (Movie movie : allMovies) {
                movie.setGenreList((ArrayList<String>) jdbcTemplate.queryForList(GET_GENRES, new Integer[]{movie.getId()}, String.class));
                movie.setWriterList((ArrayList<String>) jdbcTemplate.queryForList(GET_WRITERS, new Integer[]{movie.getId()}, String.class));
                movie.setActorList((ArrayList<String>) jdbcTemplate.queryForList(GET_ACTORS, new Integer[]{movie.getId()}, String.class));
                currentMovieList.add(movie);
            }
            return currentMovieList;
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public ArrayList<Movie> getMoviesWithDirector(String searchTerm) {
        String director = "%" + searchTerm + "%";
        currentMovieList.clear();
        try {
            List<Movie> allMovies = jdbcTemplate.query(SEARCH_BY_DIRECTOR, new MovieMapper(), director);
            for (Movie movie : allMovies) {
                movie.setGenreList((ArrayList<String>) jdbcTemplate.queryForList(GET_GENRES, new Integer[]{movie.getId()}, String.class));
                movie.setWriterList((ArrayList<String>) jdbcTemplate.queryForList(GET_WRITERS, new Integer[]{movie.getId()}, String.class));
                movie.setActorList((ArrayList<String>) jdbcTemplate.queryForList(GET_ACTORS, new Integer[]{movie.getId()}, String.class));
                currentMovieList.add(movie);
            }
            return currentMovieList;
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public ArrayList<Movie> getMoviesWithWriter(String searchTerm) {
        String writer = "%" + searchTerm + "%";
        currentMovieList.clear();
        try {
            List<Movie> allMovies = jdbcTemplate.query(SEARCH_BY_WRITER, new MovieMapper(), writer);
            for (Movie movie : allMovies) {
                movie.setGenreList((ArrayList<String>) jdbcTemplate.queryForList(GET_GENRES, new Integer[]{movie.getId()}, String.class));
                movie.setWriterList((ArrayList<String>) jdbcTemplate.queryForList(GET_WRITERS, new Integer[]{movie.getId()}, String.class));
                movie.setActorList((ArrayList<String>) jdbcTemplate.queryForList(GET_ACTORS, new Integer[]{movie.getId()}, String.class));
                currentMovieList.add(movie);
            }
            return currentMovieList;
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public ArrayList<Movie> getMoviesWithActor(String searchTerm) {
        String actor = "%" + searchTerm + "%";
        currentMovieList.clear();
        try {
            List<Movie> allMovies = jdbcTemplate.query(SEARCH_BY_ACTOR, new MovieMapper(), actor);
            for (Movie movie : allMovies) {
                movie.setGenreList((ArrayList<String>) jdbcTemplate.queryForList(GET_GENRES, new Integer[]{movie.getId()}, String.class));
                movie.setWriterList((ArrayList<String>) jdbcTemplate.queryForList(GET_WRITERS, new Integer[]{movie.getId()}, String.class));
                movie.setActorList((ArrayList<String>) jdbcTemplate.queryForList(GET_ACTORS, new Integer[]{movie.getId()}, String.class));
                currentMovieList.add(movie);
            }
            return currentMovieList;
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public ArrayList<Movie> getMoviesFromStudio(String searchTerm) {
        String studio = "%" + searchTerm + "%";
        currentMovieList.clear();
        try {
            List<Movie> allMovies = jdbcTemplate.query(SEARCH_BY_STUDIO, new MovieMapper(), studio);
            for (Movie movie : allMovies) {
                movie.setGenreList((ArrayList<String>) jdbcTemplate.queryForList(GET_GENRES, new Integer[]{movie.getId()}, String.class));
                movie.setWriterList((ArrayList<String>) jdbcTemplate.queryForList(GET_WRITERS, new Integer[]{movie.getId()}, String.class));
                movie.setActorList((ArrayList<String>) jdbcTemplate.queryForList(GET_ACTORS, new Integer[]{movie.getId()}, String.class));
                currentMovieList.add(movie);
            }
            return currentMovieList;
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public ArrayList<Movie> getMoviesWithMPAARating(String rating) {
        currentMovieList.clear();
        try {
            List<Movie> allMovies = jdbcTemplate.query(SEARCH_BY_MPAA, new MovieMapper(), rating);
            for (Movie movie : allMovies) {
                movie.setGenreList((ArrayList<String>) jdbcTemplate.queryForList(GET_GENRES, new Integer[]{movie.getId()}, String.class));
                movie.setWriterList((ArrayList<String>) jdbcTemplate.queryForList(GET_WRITERS, new Integer[]{movie.getId()}, String.class));
                movie.setActorList((ArrayList<String>) jdbcTemplate.queryForList(GET_ACTORS, new Integer[]{movie.getId()}, String.class));
                currentMovieList.add(movie);
            }
            return currentMovieList;
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public ArrayList<Movie> getMoviesByKeyword(String searchTerm) {
        String search = "%" + searchTerm + "%";
        currentMovieList.clear();
        try {
            List<Movie> allMovies = jdbcTemplate.query(SEARCH_BY_KEYWORD, new MovieMapper(), search, search, search, search);
            for (Movie movie : allMovies) {
                movie.setGenreList((ArrayList<String>) jdbcTemplate.queryForList(GET_GENRES, new Integer[]{movie.getId()}, String.class));
                movie.setWriterList((ArrayList<String>) jdbcTemplate.queryForList(GET_WRITERS, new Integer[]{movie.getId()}, String.class));
                movie.setActorList((ArrayList<String>) jdbcTemplate.queryForList(GET_ACTORS, new Integer[]{movie.getId()}, String.class));
                currentMovieList.add(movie);
            }
            return currentMovieList;
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    private void addActors(Movie movie) {
        final ArrayList<String> actorList = movie.getActorList();

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

    private void addWriters(Movie movie) {
        final ArrayList<String> writerList = movie.getWriterList();

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

    private void insertMovieGenres(Movie movie) {
        final int movieID = movie.getId();
        final List<Integer> genreIDs = getGenreIDs(movie);
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

    private void insertMovieWriters(Movie movie) {
        final int movieID = movie.getId();
        final List<Integer> writerIDs = getWriterIDs(movie);
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

    private void insertMovieActors(Movie movie) {
        final int movieID = movie.getId();
        final List<Integer> actorIDs = getActorIds(movie);
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

    private Integer getMpaaRatingsID(Movie movie) {
        String mpaaRating = movie.getMpaaRating();
        try {
            return jdbcTemplate.queryForObject(GET_MPAA_RATING_ID, Integer.class, mpaaRating);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    private Integer getDirectorID(Movie movie) {
        String director = movie.getDirector();
        jdbcTemplate.update(ADD_DIRECTOR,
                director);
        return jdbcTemplate.queryForObject(GET_DIRECTOR_ID, Integer.class, director);
    }

    private Integer getStudioID(Movie movie) {
        String studio = movie.getStudio();
        jdbcTemplate.update(ADD_STUDIO,
                studio);
        return jdbcTemplate.queryForObject(GET_STUDIO_ID, Integer.class, studio);

    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    private List<Integer> getGenreIDs(Movie movie) {

        List<String> genreList = movie.getGenres();
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
    private List<Integer> getWriterIDs(Movie movie) {

        List<String> writerList = movie.getWriterList();
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
    private List<Integer> getActorIds(Movie movie) {

        List<String> actorList = movie.getActorList();
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

    private static final class MovieMapper implements RowMapper<Movie> {

        @Override
        public Movie mapRow(ResultSet rs, int i) throws SQLException {
            Movie movie = new Movie();
            movie.setId(rs.getInt("movieID"));
            movie.setTitle(rs.getString("title"));
            movie.setReleaseDate(rs.getInt("releaseDate"));
            movie.setUserRating(rs.getInt("userRating"));
            movie.setTrailerURL(rs.getString("trailerURL"));
            movie.setCoverURL(rs.getString("coverURL"));
            movie.setSynopsis(rs.getString("synopsis"));
            movie.setMpaaRating(rs.getString("mpaaRatingsName"));
            movie.setDirector(rs.getString("directorName"));
            movie.setStudio(rs.getString("studioName"));
            movie.setNotes(rs.getString("notes"));
            return movie;
        }
    }
}
