<%-- 
    Document   : movielibrary
    Created on : Nov 2, 2016, 3:46:18 PM
    Author     : apprentice
--%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>My Movies</title>
        <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/MyMoviesCSS.css" rel="stylesheet">
    </head>
    <body>
        <div class='container-fluid'>
            <h1 class='text-center'>My Movies</h1>
            <div id="movieSide" class="col-sm-6 text-center">
                <div id="movieTitle" class="view"><h3>Title</h3></div>
                <div id="editMovieTitleDiv" class="edit">
                    <div id="titleValidation" class="validationErrors errorHeaders"></div>
                    <h3>Title</h3>
                    <input id="editMovieTitle" type="text" class="form-control" placeholder="Title" style="max-width: 300px; margin: 0 auto;"/>
                </div>
                <div id="movieCoverURL" class="top-buffer view">
                    <img id='movieCover' alt='Cover Art' height='300px' width='200px'>
                </div>
                <div id="editMovieCoverURLDiv" class="edit">
                    <div id="titleValidation" class="validationErrors errorHeaders"></div>
                    <h3>Cover URL</h3>
                    <input id="editMovieCoverURL" type="url" class="form-control" placeholder="Cover URL" style="max-width: 300px; margin: 0 auto;"/>
                </div>
                <div id="editMovieTrailerURLDiv" class="edit">
                    <div id="titleValidation" class="validationErrors errorHeaders"></div>
                    <h3>Trailer URL - Enter Embed Code</h3>
                    <input id="editMovieTrailerURL" type="url" class="form-control" placeholder="Trailer URL" style="max-width: 300px; margin: 0 auto;"/>
                </div>
                <div id="movieTableDiv">
                    <table id="movieTable" class="table table-bordered text-left top-buffer">
                        <!-- Input by default, hide and make link when movie loaded -->
                        <tr rowspan="2">
                            <th class="col-sm-1">Synopsis:</th>
                            <td id="movieSynopsis" class="view"></td>
                            <td class="edit">
                                <div id="synopsisValidation" class="validationErrors"></div>
                                <input id="editMovieSynopsis" type="text" class="form-control" placeholder="Synopsis"/>
                            </td>
                        </tr>
                        <tr>
                            <td>Release:</td>

                            <td id="movieReleaseDate" class="view"></td>
                            <td class="edit">
                                <div id="releaseDateValidation" class="validationErrors"></div>
                                <input id="editMovieReleaseDate" type="number" class="form-control" placeholder="Release Date"/>
                            </td>
                        </tr>
                        <tr>
                            <td>Genres:</td>
                            <td id="movieGenres" class="view"></td>
                            <td id="editMovieGenres" class="edit"></td>
                        </tr>
                        <tr>
                            <td>Director:</td>

                            <td id="movieDirector" class="view"></div></td>
                            <td class="edit">
                                <div id="directorValidation" class="validationErrors"></div>
                                <input id="editMovieDirector" type="text" class="form-control" placeholder="Director"/>
                            </td>
                        </tr>
                        <tr>
                            <td>Writers:</td>
                            <td id="movieWriters" class="view"></td>
                            <td class="edit">
                                <div id="writerListValidation" class="validationErrors"></div>
                                <div id="editMovieWriters"></div>
                            </td>
                        </tr>
                        <tr>
                            <td>Actors:</td>
                            <td id="movieActors" class="view"></td>
                            <td id="editMovieActors" class="edit"></td>
                        </tr>
                        <tr>
                            <td>Studio:</td>
                            <td id="movieStudio" class="view"></td>
                            <td class="edit">
                                <div id="studioValidation" class="validationErrors"></div>
                                <input id="editMovieStudio" type="text" class="form-control" placeholder="Studio"/>
                            </td>
                        </tr>
                        <tr>
                            <td>MPAA:</td>
                            <td id="movieMpaaRating" class="view"></td>
                            <td class="edit">
                                <select id="editMovieMpaaRating" type="text" class="form-control">
                                    <option id="na" value="" selected>Select a Rating</option>
                                    <option id="g" value="G">G</option>
                                    <option id="pg" value="PG">PG</option>
                                    <option id="pg-13" value="PG-13">PG-13</option>
                                    <option id="r" value="R">R</option>
                                    <option id="nc-17" value="NC-17">NC-17</option>
                                </select>
                            </td>
                        </tr>
                        <tr>
                            <td>User Rating:</td>
                            <td id="movieUserRating" class="view">
                            </td>
                            <td id="editMovieUserRating" class="edit">
                            </td>
                        <input class="hidden form-control" type="number" id="userRatingNumber" value="0" readOnly/>
                        <img id="fullStar" class="star hidden" src="${pageContext.request.contextPath}/img/fullStar.png"/>
                        <img id="emptyStar" class="star hidden" src="${pageContext.request.contextPath}/img/emptyStar.png"/>
                        </tr>
                        <tr>
                            <td>Notes:</td>
                            <td id="movieNotes" class="view"></td>
                            <td class="edit">
                                <div id="notesValidation" class="validationErrors"></div>
                                <input id="editMovieNotes" type="text" class="form-control" placeholder="Notes"/>
                            </td>
                        </tr>
                    </table>
                    <div id="edit-add-delete" class="text-center">
                    </div>
                </div>
                <div id="viewMovieButtons" class="text-center form-group">
                </div>
            </div>
            <div id="librarySide" class="col-sm-6 side">
                <div>
                    <form class="form-inline" role="form">
                        <a id="addButton" class='btn btn-primary top-buffer'>+ Add Movie</a>
                        <button id="listAllButton" class='btn btn-primary top-buffer'>List All Movies</button>
                        <select id='searchType' class='form-control top-buffer'>
                            <option value="Keyword" selected>Keyword Search</option>
                            <option value="Title">Title</option>
                            <option value="Director">Director</option>
                            <option value="Actors">Actor</option>
                            <option value="Writers">Writer</option>
                            <option value="Studio">Studio</option>
                        </select>
                        <input type='text' class='form-control top-buffer searchTerm' id='searchTerm' id='searchBar' placeholder='Search...'/>
                        <a type="submit" class='btn btn-primary top-buffer searchButton'>Search</a> 
                    </form>
                </div>
                <div id="libraryTableDiv" class="top-buffer">
                    <table id="movieListTable" class="table table-bordered table-striped">
                        <thead>
                            <tr>
                                <th class="col-sm-1"><button style="visibility: hidden" class="btn btn-primary">Submit</button></th>
                                <th class="col-sm-6"><a href="#" id="titleSortLink" class="sortLink">Title</a></th>
                                <th class="col-sm-1"><a href="#" id="releaseDateSortLink" class="sortLink">Release Year</a></th>
                                <th class="col-sm-1"><a href="#" id="mpaaRatingSortLink" class="sortLink">MPAA</a></th>
                                <th class="col-sm-3"><a href="#" id="userRatingSortLink" class="sortLink">User Rating</a></th>
                            </tr>
                        </thead>
                        <tbody id="movieListContent">
                        </tbody>
                    </table>
                </div>
            </div>
            <div class="modal fade" id="trailerModal" tabindex="-1" role="dialog" aria-labelledby="trailerModal" aria-hidden="true">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                        </div>
                        <div id="trailerVideo" class="modal-body">

                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                        </div>
                    </div>
                </div>
            </div>
            <div class="modal fade" id="editGenresModal" tabindex="-1" role="dialog" aria-labelledby="editGenresModal">
                <div class="modal-dialog" role="document">
                    <div class="modal-content">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                            <h4 id="editGenresModalTitle" class="modal-title">Edit Genres</h4>
                        </div>
                        <div class="modal-body" id="editGenresModalBody">
                            <table class="table">
                                <tr>
                                    <td><label><input class="genreCheckbox" type="Checkbox" value="Action">Action</label></td>
                                    <td><label><input class="genreCheckbox" type="Checkbox" value="Animation">Animation</label></td>
                                    <td><label><input class="genreCheckbox" type="Checkbox" value="Comedy">Comedy</label></td>
                                    <td><label><input class="genreCheckbox" type="Checkbox" value="Crime">Crime</label></td>
                                </tr>
                                <tr>
                                    <td><label><input class="genreCheckbox" type="Checkbox" value="Documentary">Documentary</label></td>
                                    <td><label><input class="genreCheckbox" type="Checkbox" value="Drama">Drama</label></td>
                                    <td><label><input class="genreCheckbox" type="Checkbox" value="Family">Family</label></td>
                                    <td><label><input class="genreCheckbox" type="Checkbox" value="Fantasy">Fantasy</label></td>
                                </tr>
                                <tr>
                                    <td><label><input class="genreCheckbox" type="Checkbox" value="Horror">Horror</label></td>
                                    <td><label><input class="genreCheckbox" type="Checkbox" value="Musical">Musical</label></td>
                                    <td><label><input class="genreCheckbox" type="Checkbox" value="Romance">Romance</label></td>
                                    <td><label><input class="genreCheckbox" type="Checkbox" value="Science Fiction">Science Fiction</label></td>
                                </tr>
                                <tr>
                                    <td><label><input class="genreCheckbox" type="Checkbox" value="Sports">Sports</label></td>
                                    <td><label><input class="genreCheckbox" type="Checkbox" value="Thriller">Thriller</label></td>
                                    <td><label><input class="genreCheckbox" type="Checkbox" value="War">War</label></td>
                                    <td><label><input class="genreCheckbox" type="Checkbox" value="Western">Western</label></td>
                                </tr>
                            </table>
                        </div>
                        <div class="modal-footer">
                            <button id="genresModalCloseButton" type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                        </div>
                    </div>
                </div>
            </div>
            <div class="modal fade" id="editWritersModal" tabindex="-1" role="dialog" aria-labelledby="editWritersModal">
                <div class="modal-dialog" role="document">
                    <div class="modal-content">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                            <h4 id="editWritersModalTitle" class="modal-title">Edit Writers</h4>
                        </div>
                        <div class="modal-body" id="editWritersModalBody">

                        </div>
                        <div class="modal-footer">
                            <button id="writersModalAddButton" type="button" class="btn btn-primary">Add Writer</button>
                            <button id="writersModalCloseButton" type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                        </div>
                    </div>
                </div>
            </div>
            <div class="modal fade" id="editActorsModal" tabindex="-1" role="dialog" aria-labelledby="editActorsModal">
                <div class="modal-dialog" role="document">
                    <div class="modal-content">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                            <h4 id="editActorsModalTitle" class="modal-title">Edit Actors</h4>
                        </div>
                        <div class="modal-body" id="editActorsModalBody">

                        </div>
                        <div class="modal-footer">
                            <button id="actorsModalAddButton" type="button" class="btn btn-primary">Add Actor</button>
                            <button id="actorsModalCloseButton" type="button" class="btn btn-primary" data-dismiss="modal">Close</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <script src="${pageContext.request.contextPath}/js/jquery-2.2.4.min.js"></script>
        <script src="${pageContext.request.contextPath}/js/bootstrap.min.js"></script>
        <script src="js/MyMoviesJS.js"></script>
    </body>
</html>
