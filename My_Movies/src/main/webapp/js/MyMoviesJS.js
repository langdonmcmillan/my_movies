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
    loadDVDs();
    $("#trailerModal").on('hide.bs.modal', function () {
        $("#trailerVideo").empty();
    });
    $("#trailerModal").on('show.bs.modal', function () {
        $("#trailerVideo").append(trailerURL);
    });
});

function loadDVDs() {
    $.ajax({
        url: 'getDVDs'
    }).success(function (data, status) {
        fillDVDLibrary(data, status);
    });
}

function fillDVDLibrary(dvdList, status) {
    clearDVDLibrary();
    var dvdLibrary = $('#dvdListContent');
    $.each(dvdList, function (index, dvd) {
        dvdLibrary.append($('<tr>')
                .append($('<td>')
                        .append($('<button>')
                                .addClass("btn btn-primary")
                                .on("click", function (event) {
                                    id = dvd.id;
                                    showView();
                                })
                                .text("Select")
                                ))
                .append($('<td>').text(dvd.title))
                .append($('<td>').text(dvd.releaseDate))
                .append($('<td>').text(dvd.mpaaRating))
                .append($('<td>').append(function () {
                    for (i = 1; i <= 5; i++) {
                        if (i <= dvd.userRating) {
                            $(this).append("<img class='smallStar' src='" + fullStar + "'alt='Star'>");
                        } else {
                            $(this).append("<img class='smallStar' src='" + emptyStar + "'alt='Star'>");
                        }
                    }
                })));
    });
}

function clearDVDLibrary() {
    $("#dvdListContent").empty();
}

$(document).on("click", "#listAllButton", function () {
    loadDVDs();
});

$(".sortLink").on("click", function () {
    var linkID = $(this).attr("id");
    var alreadySorted = $(this).hasClass("alreadySorted");
    var sortType = $(this).attr("id").replace("SortLink", "");
    $.ajax({
        url: "sort/" + sortType + "/" + alreadySorted
    }).success(function (data, status) {
        fillDVDLibrary(data, status);
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
    var searchTerm = ($("#searchTerm").val() === "") ? "xalldvdsx" : $("#searchTerm").val();
    $.ajax({
        url: "search/" + searchType + "/" + searchTerm
    }).success(function (data, status) {
        fillDVDLibrary(data, status);
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
    var searchType = $(this).parent().attr("id").replace("dvd", "");
    var searchTerm = $(this).text().replace(",", "");
    searchTerm = searchTerm.trim();
    $.ajax({
        url: "search/" + searchType + "/" + searchTerm
    }).success(function (data, status) {
        fillDVDLibrary(data, status);
        $("#searchTerm").val('');
        $("#searchType").val('Keyword');
    });
});

function showView() {
    viewDVD();
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

function viewDVD() {
    $.ajax({
        type: "GET",
        url: "dvd/" + id
    }).success(function (dvd) {
        clearDVDInfo();
        var genres = "";
        var writers = "";
        var actors = "";
        var cover = dvd.coverURL;
        trailerURL = dvd.trailerURL;
        $("#dvdTitle").html(("<h3>" + dvd.title + "</h3>"));
        $("#dvdCoverURL").prepend("<a href='#trailerModal' data-toggle='modal' data-target='#trailerModal'>\n\
        <img id='dvdCover' src='" + cover + "'alt='Cover Art'></a>");
        $("#dvdSynopsis").html((dvd.synopsis));
        $("#dvdReleaseDate").append("<a href='#' class='searchLink'>" + dvd.releaseDate + "</a>");
        $.each(dvd.genreList, function (index, genre) {
            genres = genres + "<a href='#' class='searchLink'> " + genre + "</a>, ";
        });
        genres = genres.replace(/,(\s+)?$/, '');
        $('#dvdGenres').append(genres);
        $("#dvdDirector").append("<a href='#' class='searchLink'>" + dvd.director + "  </a>");
        $.each(dvd.writerList, function (index, writer) {
            writers = writers + "<a href='#' class='searchLink'> " + writer + "</a>, ";
        });
        writers = writers.replace(/,(\s+)?$/, '');
        $('#dvdWriters').append(writers);
        $.each(dvd.actorList, function (index, actor) {
            actors = actors + "<a href='#' class='searchLink'> " + actor + "</a>, ";
        });
        actors = actors.replace(/,(\s+)?$/, '');
        $('#dvdActors').append(actors);
        $("#dvdStudio").append("<a href='#' class='searchLink'>" + dvd.studio + "  </a>");
        $("#dvdMpaaRating").append("<a href='#' class='searchLink'>" + dvd.mpaaRating + "  </a>");
        for (i = 1; i <= 5; i++) {
            if (i <= dvd.userRating) {
                $("#dvdUserRating").append("<img id='" + i + "' class='view star' src='" + fullStar + "'alt='Star'>");
            } else {
                $("#dvdUserRating").append("<img id='" + i + "' class='view star' src='" + emptyStar + "'alt='Star'>");
            }
        }
        $("#dvdNotes").append(dvd.notes);
    });
}

function clearDVDInfo() {
    $("#dvdTitle").empty();
    $("#dvdTitle").html("<h3>Title</h3>");
    $("#dvdCoverURL").empty();
    $("#dvdSynopsis").empty();
    $("#dvdReleaseDate").empty();
    $("#dvdGenres").empty();
    $("#dvdDirector").empty();
    $("#dvdWriters").empty();
    $("#dvdActors").empty();
    $("#dvdStudio").empty();
    $("#dvdMpaaRating").empty();
    $("#dvdUserRating").empty();
    $("#dvdNotes").empty();
}

function populateEditFields() {
    clearDVDEdits();
    $.ajax({
        type: "GET",
        url: "dvd/" + id
    }).success(function (dvd) {
        var genres = "";
        var actors = "";
        var writers = "";
        $("#editDVDTitle").val((dvd.title));
        $("#editDVDCoverURL").val(dvd.coverURL);
        $("#editDVDTrailerURL").val(dvd.trailerURL);
        $("#editDVDSynopsis").val((dvd.synopsis));
        $("#editDVDReleaseDate").val(dvd.releaseDate);
        $.each(dvd.genreList, function (index, genre) {
            genres = genres + genre + ", ";
            $(".genreCheckbox").each(function () {
                var checkBoxValue = $(this).prop("value");
                if (checkBoxValue === genre) {
                    $(this).prop("checked", true);
                }
            });
        });
        genres = genres.replace(/,\s*$/, '') + " ";
        $("#editDVDGenres").append(genres + "<a href='#genresModal' data-toggle='modal' data-target='#editGenresModal'><strong>Edit</strong></a>");
        $("#editDVDDirector").val(dvd.director);
        $.each(dvd.writerList, function (index, writer) {
            writers = writers + writer + ", ";
            $('#editWritersModalBody').append("<div><input  id='writer::" + writer +
                    "' type='text' value='" + writer + "' class='writerInput form-control top-buffer'/><a href='#' class='deleteWriterActorLink'>Remove</a></div>");
        });
        writers = writers.replace(/,\s*$/, '') + " ";
        $('#editDVDWriters').append(writers);
        $("#editDVDWriters").append("<a href='#editWritersModal' data-toggle='modal' data-target='#editWritersModal'><strong>Edit</strong></a>");
        $.each(dvd.actorList, function (index, actor) {
            actors = actors + actor + ", ";
            $('#editActorsModalBody').append("<div><input type='text' value='" + actor + "' class='actorInput form-control top-buffer'/><a href='#' class='deleteWriterActorLink'>Remove</a></div>");
        });
        actors = actors.replace(/,\s*$/, '') + " ";
        $('#editDVDActors').append(actors);
        $("#editDVDActors").append("<a href='#editActorsModal' data-toggle='modal' data-target='#editActorsModal'><strong>Edit</strong></a>");
        $("#editDVDStudio").val(dvd.studio);
        $("#editDVDMpaaRating").val(dvd.mpaaRating);
        userRating = dvd.userRating;
        for (i = 1; i <= 5; i++) {
            if (i <= userRating) {
                $("#editDVDUserRating").append("<img id='" + i + "' class='star star-edit' src='" + fullStar + "'alt='Star'>");
            } else {
                $("#editDVDUserRating").append("<img id='" + i + "' class='star star-edit' src='" + emptyStar + "'alt='Star'>");
            }
        }
        $("#editDVDNotes").val(dvd.notes);
    });
}

function clearDVDEdits() {
    $("#editDVDTitle").val("");
    $("#editDVDCoverURL").val("");
    $("#editDVDSynopsis").val("");
    $("#editDVDReleaseDate").val("");
    $("#editDVDGenres").empty();
    $("#editDVDDirector").val("");
    $("#editDVDWriters").empty();
    $("#editDVDActors").empty();
    $("#editDVDStudio").val("");
    $("#editDVDMpaaRating").val("");
    $("#editDVDUserRating").empty();
    $("#editDVDNotes").empty();
    $("#editWritersModalBody").empty();
    $("#editActorsModalBody").empty();
    $(".genreCheckbox").each(function () {
        $(this).prop("checked", false);
    });
}

function displaySaveCancelButtons() {
    $("#edit-add-delete").empty();
    $("#edit-add-delete").append($("<a class='btn btn-primary dvdButton' id='saveButton'>Save</a>"));
    $("#edit-add-delete").append($("<a class='btn btn-primary dvdButton' id='cancelButton'>Cancel</a>"));
}

function displayEditDeleteButtons() {
    $("#edit-add-delete").empty();
    $("#edit-add-delete").append($("<a class='btn btn-primary dvdButton' id='editButton'>Edit</a>"));
    $("#edit-add-delete").append($("<a class='btn btn-primary dvdButton' id='deleteButton'>Delete</a>"));
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
        addDVD();
    } else {
        editDVD();
    }
});

function addDVD() {
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
        url: "dvd",
        data: JSON.stringify({
            title: $("#editDVDTitle").val(),
            coverURL: ($("#editDVDCoverURL").empty()) ? $("#editDVDCoverURL").val() : "",
            trailerURL: ($("#editDVDTrailerURL").empty()) ? $("#editDVDTrailerURL").val() : "",
            synopsis: ($("#editDVDSynopsis").empty()) ? $("#editDVDSynopsis").val() : "",
            releaseDate: ($("#editReleaseDate").empty()) ? $("#editDVDReleaseDate").val() : "",
            genreList: genres,
            director: ($("#editDVDDirector").empty()) ? $("#editDVDDirector").val() : "",
            writerList: writers,
            actorList: actors,
            studio: ($("#editDVDStudio").empty()) ? $("#editDVDStudio").val() : "",
            mpaaRating: ($("#editDVDMpaaRating").size()) ? $("#editDVDMpaaRating").val() : "",
            userRating: userRating,
            notes: ($("#editDVDNotes").empty()) ? $("#editDVDNotes").val() : ""
        }),
        contentType: "application/json; charset=utf-8",
        headers: {
            "Accept": "application/json",
            "Content-type": "application/json"
        },
        dataType: "json"
    }).success(function (dvd, status) {
        id = dvd.id;
        showView();
        loadDVDs();
    }).error(function (dvd, status) {
        $(".validationErrors").empty();
        $(".validationErrors").show();
        $(".edit").removeClass("has-error");
        $.each(dvd.responseJSON.fieldErrors, function (index, validationError) {
            var errorDiv = validationError.fieldName.replace(/\[(.*?)\]/, "");
            $("#" + errorDiv + "Validation").append(validationError.message);
            $("#" + errorDiv + "Validation").parent().addClass("has-error");
        });
    });
}

function editDVD() {
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
        url: "dvd/" + id,
        data: JSON.stringify({
            title: $("#editDVDTitle").val(),
            coverURL: ($("#editDVDCoverURL").empty()) ? $("#editDVDCoverURL").val() : "",
            trailerURL: ($("#editDVDTrailerURL").empty()) ? $("#editDVDTrailerURL").val() : "",
            synopsis: ($("#editDVDSynopsis").empty()) ? $("#editDVDSynopsis").val() : "",
            releaseDate: ($("#editReleaseDate").empty()) ? $("#editDVDReleaseDate").val() : "",
            genreList: genres,
            director: ($("#editDVDDirector").empty()) ? $("#editDVDDirector").val() : "",
            writerList: writers,
            actorList: actors,
            studio: ($("#editDVDStudio").empty()) ? $("#editDVDStudio").val() : "",
            mpaaRating: ($("#editDVDMpaaRating").size()) ? $("#editDVDMpaaRating").val() : "",
            userRating: userRating,
            notes: ($("#editDVDNotes").empty()) ? $("#editDVDNotes").val() : ""
        }),
        contentType: "application/json; charset=utf-8",
        headers: {
            "Accept": "application/json",
            "Content-type": "application/json"
        },
        dataType: "json"
    }).success(function (data, status) {
        showView();
        loadDVDs();
    }).error(function (dvd, status) {
        $(".validationErrors").empty();
        $(".validationErrors").show();
        $(".edit").removeClass("has-error");
        $.each(dvd.responseJSON.fieldErrors, function (index, validationError) {
            var errorDiv = validationError.fieldName.replace(/\[(.*?)\]/, "");
            $("#" + errorDiv + "Validation").append(validationError.message).append($("<br>"));
            $("#" + errorDiv + "Validation").parent().addClass("has-error");
        });
    });
}

$(document).on("click", "#deleteButton", function () {
    var answer = confirm("Are you sure you want to delete this DVD?");
    if (answer) {
        $.ajax({
            type: "DELETE",
            url: "dvd/" + id
        }).success(function () {
            loadDVDs();
            clearDVDInfo();
            clearDVDEdits();
            $("#dvdCoverURL").append("<img id='dvdCover' alt='Cover Art' height='300px' width='200px'>");
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
    $("#editDVDGenres").empty();
    $(".genreCheckbox").each(function () {
        if ($(this).is(":checked")) {
            genres = genres + ($(this).attr("value")) + ", ";
        }
    });
    genres = genres.replace(/,\s*$/, '') + " ";
    $("#editDVDGenres").append(genres + "<a href='#genresModal' data-toggle='modal' data-target='#editGenresModal'><strong>Edit</strong></a>");
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
    $("#editDVDWriters").empty();
    $(".writerInput").each(function () {
        if ($(this).val().length < 1) {
            $(this).parent().remove();
        } else {
            writers = writers + $(this).val() + ", ";
        }
    });
    writers = writers.replace(/,\s*$/, '') + " ";
    $('#editDVDWriters').append(writers);
    $("#editDVDWriters").append("<a href='#editWritersModal' data-toggle='modal' data-target='#editWritersModal'><strong>Edit</strong></a>");
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
    $("#editDVDActors").empty();
    $(".actorInput").each(function () {
        if ($(this).val().length < 1) {
            $(this).parent().remove();
        } else {
            actors = actors + $(this).val() + ", ";
        }
    });
    actors = actors.replace(/,\s*$/, '') + " ";
    $('#editDVDActors').append(actors);
    $("#editDVDActors").append("<a href='#editActorsModal' data-toggle='modal' data-target='#editActorsModal'><strong>Edit</strong></a>");
});