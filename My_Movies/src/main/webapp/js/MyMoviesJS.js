/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
var trailerURL;
var id;
var emptyStar;
var fullStar;
var userRating;

$(document).ready(function () {
    $(".edit").hide();
    emptyStar = $("#emptyStar").attr("src");
    fullStar = $("#fullStar").attr("src");
    loadMovies();
    $("#trailerModal").on('hide.bs.modal', function () {
        $("#trailerVideo").empty();
    });
    $("#trailerModal").on('show.bs.modal', function () {
        $("#trailerVideo").append(trailerURL);
    });
});

function loadMovies() {
    $.ajax({
        url: 'getMovies'
    }).success(function (data, status) {
        fillMovieLibrary(data, status);
    });
}

function fillMovieLibrary(movieList, status) {
    clearMovieLibrary();
    var movieLibrary = $('#movieListContent');
    $.each(movieList, function (index, movie) {
        movieLibrary.append($('<tr>')
                .append($('<td>')
                        .append($('<button>')
                                .addClass("btn btn-primary")
                                .on("click", function (event) {
                                    id = movie.id;
                                    showView();
                                })
                                .text("Select")
                                ))
                .append($('<td>').text(movie.title))
                .append($('<td>').text(movie.releaseDate))
                .append($('<td>').text(movie.mpaaRating))
                .append($('<td>').append(function () {
                    for (i = 1; i <= 5; i++) {
                        if (i <= movie.userRating) {
                            $(this).append("<img class='smallStar' src='" + fullStar + "'alt='Star'>");
                        } else {
                            $(this).append("<img class='smallStar' src='" + emptyStar + "'alt='Star'>");
                        }
                    }
                })));
    });
}

function clearMovieLibrary() {
    $("#movieListContent").empty();
}

$(document).on("click", "#listAllButton", function () {
    loadMovies();
});

$(".sortLink").on("click", function () {
    var linkID = $(this).attr("id");
    var alreadySorted = $(this).hasClass("alreadySorted");
    var sortType = $(this).attr("id").replace("SortLink", "");
    $.ajax({
        url: "sort/" + sortType + "/" + alreadySorted
    }).success(function (data, status) {
        fillMovieLibrary(data, status);
        $(".sortLink").each(function () {
            var currentID = $(this).attr("id");
            if (currentID !== linkID) {
                $("#" + currentID).removeClass("alreadySorted");
            }
        });
        $("#" + linkID).toggleClass("alreadySorted");
    });
});

$(".searchButton").on("click", function () {
    var searchType = $("#searchType").val();
    var searchTerm = ($("#searchTerm").val() === "") ? "xallmoviesx" : $("#searchTerm").val();
    $.ajax({
        url: "search/" + searchType + "/" + searchTerm
    }).success(function (data, status) {
        fillMovieLibrary(data, status);
        $("#searchTerm").val('');
        $("#searchType").val('Keyword');
    });
});

$('.searchTerm').keypress(function (e) {
    if (e.which === 13) {
        $(".searchButton").click();
        return false;
    }
});

$(document).on("click", ".searchLink", function () {
    var searchType = $(this).parent().attr("id").replace("movie", "");
    var searchTerm = $(this).text().replace(",", "");
    searchTerm = searchTerm.trim();
    $.ajax({
        url: "search/" + searchType + "/" + searchTerm
    }).success(function (data, status) {
        fillMovieLibrary(data, status);
        $("#searchTerm").val('');
        $("#searchType").val('Keyword');
    });
});

function showView() {
    viewMovie();
    $(".view").show();
    $(".edit").hide();
    displayEditDeleteButtons();
}

function showEdit() {
    $(".validationErrors").empty();
    $(".edit").removeClass("has-error");
    populateEditFields();
    $(".edit").show();
    $(".view").hide();
    displaySaveCancelButtons();
}

function viewMovie() {
    $.ajax({
        type: "GET",
        url: "movie/" + id
    }).success(function (movie) {
        clearMovieInfo();
        var genres = "";
        var writers = "";
        var actors = "";
        var cover = movie.coverURL;
        trailerURL = movie.trailerURL;
        $("#movieTitle").html(("<h3>" + movie.title + "</h3>"));
        $("#movieCoverURL").prepend("<a href='#trailerModal' data-toggle='modal' data-target='#trailerModal'>\n\
        <img id='movieCover' src='" + cover + "'alt='Cover Art'></a>");
        $("#movieSynopsis").html((movie.synopsis));
        $("#movieReleaseDate").append("<a href='#' class='searchLink'>" + movie.releaseDate + "</a>");
        $.each(movie.genreList, function (index, genre) {
            genres = genres + "<a href='#' class='searchLink'> " + genre + "</a>, ";
        });
        genres = genres.replace(/,(\s+)?$/, '');
        $('#movieGenres').append(genres);
        $("#movieDirector").append("<a href='#' class='searchLink'>" + movie.director + "  </a>");
        $.each(movie.writerList, function (index, writer) {
            writers = writers + "<a href='#' class='searchLink'> " + writer + "</a>, ";
        });
        writers = writers.replace(/,(\s+)?$/, '');
        $('#movieWriters').append(writers);
        $.each(movie.actorList, function (index, actor) {
            actors = actors + "<a href='#' class='searchLink'> " + actor + "</a>, ";
        });
        actors = actors.replace(/,(\s+)?$/, '');
        $('#movieActors').append(actors);
        $("#movieStudio").append("<a href='#' class='searchLink'>" + movie.studio + "  </a>");
        $("#movieMpaaRating").append("<a href='#' class='searchLink'>" + movie.mpaaRating + "  </a>");
        for (i = 1; i <= 5; i++) {
            if (i <= movie.userRating) {
                $("#movieUserRating").append("<img id='" + i + "' class='view star' src='" + fullStar + "'alt='Star'>");
            } else {
                $("#movieUserRating").append("<img id='" + i + "' class='view star' src='" + emptyStar + "'alt='Star'>");
            }
        }
        $("#movieNotes").append(movie.notes);
    });
}

function clearMovieInfo() {
    $("#movieTitle").empty();
    $("#movieTitle").html("<h3>Title</h3>");
    $("#movieCoverURL").empty();
    $("#movieSynopsis").empty();
    $("#movieReleaseDate").empty();
    $("#movieGenres").empty();
    $("#movieDirector").empty();
    $("#movieWriters").empty();
    $("#movieActors").empty();
    $("#movieStudio").empty();
    $("#movieMpaaRating").empty();
    $("#movieUserRating").empty();
    $("#movieNotes").empty();
}

function populateEditFields() {
    clearMovieEdits();
    $.ajax({
        type: "GET",
        url: "movie/" + id
    }).success(function (movie) {
        var genres = "";
        var actors = "";
        var writers = "";
        $("#editMovieTitle").val((movie.title));
        $("#editMovieCoverURL").val(movie.coverURL);
        $("#editMovieTrailerURL").val(movie.trailerURL);
        $("#editMovieSynopsis").val((movie.synopsis));
        $("#editMovieReleaseDate").val(movie.releaseDate);
        $.each(movie.genreList, function (index, genre) {
            genres = genres + genre + ", ";
            $(".genreCheckbox").each(function () {
                var checkBoxValue = $(this).prop("value");
                if (checkBoxValue === genre) {
                    $(this).prop("checked", true);
                }
            });
        });
        genres = genres.replace(/,\s*$/, '') + " ";
        $("#editMovieGenres").append(genres + "<a href='#genresModal' data-toggle='modal' data-target='#editGenresModal'><strong>Edit</strong></a>");
        $("#editMovieDirector").val(movie.director);
        $.each(movie.writerList, function (index, writer) {
            writers = writers + writer + ", ";
            $('#editWritersModalBody').append("<div><input  id='writer::" + writer +
                    "' type='text' value='" + writer + "' class='writerInput form-control top-buffer'/><a href='#' class='deleteWriterActorLink'>Remove</a></div>");
        });
        writers = writers.replace(/,\s*$/, '') + " ";
        $('#editMovieWriters').append(writers);
        $("#editMovieWriters").append("<a href='#editWritersModal' data-toggle='modal' data-target='#editWritersModal'><strong>Edit</strong></a>");
        $.each(movie.actorList, function (index, actor) {
            actors = actors + actor + ", ";
            $('#editActorsModalBody').append("<div><input type='text' value='" + actor + "' class='actorInput form-control top-buffer'/><a href='#' class='deleteWriterActorLink'>Remove</a></div>");
        });
        actors = actors.replace(/,\s*$/, '') + " ";
        $('#editMovieActors').append(actors);
        $("#editMovieActors").append("<a href='#editActorsModal' data-toggle='modal' data-target='#editActorsModal'><strong>Edit</strong></a>");
        $("#editMovieStudio").val(movie.studio);
        $("#editMovieMpaaRating").val(movie.mpaaRating);
        userRating = movie.userRating;
        for (i = 1; i <= 5; i++) {
            if (i <= userRating) {
                $("#editMovieUserRating").append("<img id='" + i + "' class='star star-edit' src='" + fullStar + "'alt='Star'>");
            } else {
                $("#editMovieUserRating").append("<img id='" + i + "' class='star star-edit' src='" + emptyStar + "'alt='Star'>");
            }
        }
        $("#editMovieNotes").val(movie.notes);
    });
}

function clearMovieEdits() {
    $("#editMovieTitle").val("");
    $("#editMovieCoverURL").val("");
    $("#editMovieSynopsis").val("");
    $("#editMovieReleaseDate").val("");
    $("#editMovieGenres").empty();
    $("#editMovieDirector").val("");
    $("#editMovieWriters").empty();
    $("#editMovieActors").empty();
    $("#editMovieStudio").val("");
    $("#editMovieMpaaRating").val("");
    $("#editMovieUserRating").empty();
    $("#editMovieNotes").empty();
    $("#editWritersModalBody").empty();
    $("#editActorsModalBody").empty();
    $(".genreCheckbox").each(function () {
        $(this).prop("checked", false);
    });
}

function displaySaveCancelButtons() {
    $("#edit-add-delete").empty();
    $("#edit-add-delete").append($("<a class='btn btn-primary movieButton' id='saveButton'>Save</a>"));
    $("#edit-add-delete").append($("<a class='btn btn-primary movieButton' id='cancelButton'>Cancel</a>"));
}

function displayEditDeleteButtons() {
    $("#edit-add-delete").empty();
    $("#edit-add-delete").append($("<a class='btn btn-primary movieButton' id='editButton'>Edit</a>"));
    $("#edit-add-delete").append($("<a class='btn btn-primary movieButton' id='deleteButton'>Delete</a>"));
}

$(document).on("click", "#addButton", function () {
    id = 0;
    showEdit();
});

$(document).on("click", "#editButton", function () {
    showEdit();
});

$(document).on("click", "#cancelButton", function () {
    showView();
    if (id === 0) {
        $("#edit-add-delete").empty();
    }
});

$(document).on("click", "#saveButton", function () {
    if (id === 0) {
        addMovie();
    } else {
        editMovie();
    }
});

function addMovie() {
    var genres = [];
    var writers = [];
    var actors = [];
    $(".genreCheckbox").each(function () {
        if ($(this).is(":checked")) {
            genres.push($(this).val());
        }
    });
    $(".writerInput").each(function () {
        writers.push($(this).val());
    });
    $(".actorInput").each(function () {
        actors.push($(this).val());
    });
    $.ajax({
        type: "POST",
        url: "movie",
        data: JSON.stringify({
            title: $("#editMovieTitle").val(),
            coverURL: ($("#editMovieCoverURL").empty()) ? $("#editMovieCoverURL").val() : "",
            trailerURL: ($("#editMovieTrailerURL").empty()) ? $("#editMovieTrailerURL").val() : "",
            synopsis: ($("#editMovieSynopsis").empty()) ? $("#editMovieSynopsis").val() : "",
            releaseDate: ($("#editReleaseDate").empty()) ? $("#editMovieReleaseDate").val() : "",
            genreList: genres,
            director: ($("#editMovieDirector").empty()) ? $("#editMovieDirector").val() : "",
            writerList: writers,
            actorList: actors,
            studio: ($("#editMovieStudio").empty()) ? $("#editMovieStudio").val() : "",
            mpaaRating: ($("#editMovieMpaaRating").size()) ? $("#editMovieMpaaRating").val() : "",
            userRating: userRating,
            notes: ($("#editMovieNotes").empty()) ? $("#editMovieNotes").val() : ""
        }),
        contentType: "application/json; charset=utf-8",
        headers: {
            "Accept": "application/json",
            "Content-type": "application/json"
        },
        dataType: "json"
    }).success(function (movie, status) {
        id = movie.id;
        showView();
        loadMovies();
    }).error(function (movie, status) {
        $(".validationErrors").empty();
        $(".validationErrors").show();
        $(".edit").removeClass("has-error");
        $.each(movie.responseJSON.fieldErrors, function (index, validationError) {
            var errorDiv = validationError.fieldName.replace(/\[(.*?)\]/, "");
            $("#" + errorDiv + "Validation").append(validationError.message);
            $("#" + errorDiv + "Validation").parent().addClass("has-error");
        });
    });
}

function editMovie() {
    var genres = [];
    var writers = [];
    var actors = [];
    $(".genreCheckbox").each(function () {
        if ($(this).is(":checked")) {
            genres.push($(this).val());
        }
    });
    $(".writerInput").each(function () {
        writers.push($(this).val());
    });
    $(".actorInput").each(function () {
        actors.push($(this).val());
    });
    $.ajax({
        type: "PUT",
        url: "movie/" + id,
        data: JSON.stringify({
            title: $("#editMovieTitle").val(),
            coverURL: ($("#editMovieCoverURL").empty()) ? $("#editMovieCoverURL").val() : "",
            trailerURL: ($("#editMovieTrailerURL").empty()) ? $("#editMovieTrailerURL").val() : "",
            synopsis: ($("#editMovieSynopsis").empty()) ? $("#editMovieSynopsis").val() : "",
            releaseDate: ($("#editReleaseDate").empty()) ? $("#editMovieReleaseDate").val() : "",
            genreList: genres,
            director: ($("#editMovieDirector").empty()) ? $("#editMovieDirector").val() : "",
            writerList: writers,
            actorList: actors,
            studio: ($("#editMovieStudio").empty()) ? $("#editMovieStudio").val() : "",
            mpaaRating: ($("#editMovieMpaaRating").size()) ? $("#editMovieMpaaRating").val() : "",
            userRating: userRating,
            notes: ($("#editMovieNotes").empty()) ? $("#editMovieNotes").val() : ""
        }),
        contentType: "application/json; charset=utf-8",
        headers: {
            "Accept": "application/json",
            "Content-type": "application/json"
        },
        dataType: "json"
    }).success(function (data, status) {
        showView();
        loadMovies();
    }).error(function (movie, status) {
        $(".validationErrors").empty();
        $(".validationErrors").show();
        $(".edit").removeClass("has-error");
        $.each(movie.responseJSON.fieldErrors, function (index, validationError) {
            var errorDiv = validationError.fieldName.replace(/\[(.*?)\]/, "");
            $("#" + errorDiv + "Validation").append(validationError.message).append($("<br>"));
            $("#" + errorDiv + "Validation").parent().addClass("has-error");
        });
    });
}

$(document).on("click", "#deleteButton", function () {
    var answer = confirm("Are you sure you want to delete this Movie?");
    if (answer) {
        $.ajax({
            type: "DELETE",
            url: "movie/" + id
        }).success(function () {
            loadMovies();
            clearMovieInfo();
            clearMovieEdits();
            $("#movieCoverURL").append("<img id='movieCover' alt='Cover Art' height='300px' width='200px'>");
        });
    }
});

$(document).on("mouseover", ".star-edit", function () {
    var starNum = parseInt($(this).attr("id"));
    $(".star-edit").each(function () {
        if ((parseInt($(this).attr("id"))) <= starNum) {
            $(this).attr("src", fullStar);
        } else {
            $(this).attr("src", emptyStar);
        }
    });
});

$(document).on("mouseout", ".star-edit", function () {
    $(".star-edit").each(function () {
        if ((parseInt($(this).attr("id"))) <= userRating) {
            $(this).attr("src", fullStar);
        } else {
            $(this).attr("src", emptyStar);
        }
    });
});

$(document).on("click", ".star-edit", function () {
    userRating = parseInt($(this).attr("id"));
    $(".star-edit").each(function () {
        if ((parseInt($(this).attr("id"))) <= starNum) {
            $(this).attr("src", fullStar);
        } else {
            $(this).attr("src", emptyStar);
        }
    });
});

$(document).on("click", "#genresModalCloseButton", function () {
    var genres = "";
    $("#editMovieGenres").empty();
    $(".genreCheckbox").each(function () {
        if ($(this).is(":checked")) {
            genres = genres + ($(this).attr("value")) + ", ";
        }
    });
    genres = genres.replace(/,\s*$/, '') + " ";
    $("#editMovieGenres").append(genres + "<a href='#genresModal' data-toggle='modal' data-target='#editGenresModal'><strong>Edit</strong></a>");
});

$(document).on("click", "#writersModalAddButton", function () {
    $('#editWritersModalBody').append("<div><input type='text' class='writerInput form-control top-buffer' placeholder='Writer'/><a href='#' class='deleteWriterActorLink'>Remove</a></div>");
    $(".writerInput:last").focus();
});

$(document).on("keypress", ".writerInput", function (e) {
    if (e.which === 13) {
        $("#writersModalAddButton").click();
        return false;
    }
});

$(document).on("click", "#writersModalCloseButton", function () {
    var writers = "";
    $("#editMovieWriters").empty();
    $(".writerInput").each(function () {
        if ($(this).val().length < 1) {
            $(this).parent().remove();
        } else {
            writers = writers + $(this).val() + ", ";
        }
    });
    writers = writers.replace(/,\s*$/, '') + " ";
    $('#editMovieWriters').append(writers);
    $("#editMovieWriters").append("<a href='#editWritersModal' data-toggle='modal' data-target='#editWritersModal'><strong>Edit</strong></a>");
});

$(document).on("focusout", ".actorInput, .writerInput", function () {
    if ($(this).val().length < 1) {
        $(this).parent().remove();
    }
});

$(document).on("click", ".deleteWriterActorLink", function () {
    $(this).parent().remove();
});

$(document).on("click", "#actorsModalAddButton", function () {
    $('#editActorsModalBody').append("<div><input type='text' class='actorInput form-control top-buffer' placeholder='Actor'/><a href='#' class='deleteWriterActorLink'>Remove</a></div>");
    $(".actorInput:last").focus();
});

$(document).on("keypress", ".actorInput", function (e) {
    if (e.which === 13) {
        $("#actorsModalAddButton").click();
        return false;
    }
});

$(document).on("click", "#actorsModalCloseButton", function () {
    var actors = "";
    $("#editMovieActors").empty();
    $(".actorInput").each(function () {
        if ($(this).val().length < 1) {
            $(this).parent().remove();
        } else {
            actors = actors + $(this).val() + ", ";
        }
    });
    actors = actors.replace(/,\s*$/, '') + " ";
    $('#editMovieActors').append(actors);
    $("#editMovieActors").append("<a href='#editActorsModal' data-toggle='modal' data-target='#editActorsModal'><strong>Edit</strong></a>");
});