drop database if exists myMovies;
CREATE database if not exists myMovies;

use myMovies;

CREATE TABLE IF NOT EXISTS `Movies` (
 `movieID` int NOT NULL AUTO_INCREMENT,
 `title` varchar(50) NOT NULL,
 `releaseDate` int,
 `mpaaRatingsID` int,
 `directorID` int,
 `studioID` int,
 `userRating` int,
 `trailerURL` varchar(500),
 `coverURL` varchar(500),
 `synopsis` varchar(500),
 `notes` varchar(500),
 PRIMARY KEY (`movieID`)
);

CREATE TABLE IF NOT EXISTS `Directors` (
 `directorID` int NOT NULL AUTO_INCREMENT,
 `directorName` varchar(50) NOT NULL UNIQUE,
 PRIMARY KEY (`directorID`)
);

CREATE TABLE IF NOT EXISTS `Studios` (
 `studioID` int NOT NULL AUTO_INCREMENT,
 `studioName` varchar(50) NOT NULL UNIQUE,
 PRIMARY KEY (`studioID`)
);

CREATE TABLE IF NOT EXISTS `MpaaRatings` (
 `mpaaRatingsID` int NOT NULL AUTO_INCREMENT,
 `mpaaRatingsName` varchar(50) NOT NULL UNIQUE,
 PRIMARY KEY (`mpaaRatingsID`)
);

CREATE TABLE IF NOT EXISTS `Actors` (
 `actorID` int NOT NULL AUTO_INCREMENT,
 `actorName` varchar(50) NOT NULL UNIQUE,
 PRIMARY KEY (`actorID`)
);

CREATE TABLE IF NOT EXISTS `Writers` (
 `writerID` int NOT NULL AUTO_INCREMENT,
 `writerName` varchar(50) NOT NULL UNIQUE,
 PRIMARY KEY (`writerID`)
);

CREATE TABLE IF NOT EXISTS `Genres` (
 `genreID` int NOT NULL AUTO_INCREMENT,
 `genreName` varchar(50) NOT NULL UNIQUE,
 PRIMARY KEY (`genreID`)
);

CREATE TABLE IF NOT EXISTS `MoviesxGenres`(
 `movieID` int NOT NULL,
 `genreID` int NOT NULL,
 KEY (`movieID`),
 KEY (`genreID`)
);

CREATE TABLE IF NOT EXISTS `MoviesxWriters`(
 `movieID` int NOT NULL,
 `writerID` int NOT NULL,
 KEY (`movieID`),
 KEY (`writerID`)
);

CREATE TABLE IF NOT EXISTS `MoviesxActors`(
 `movieID` int NOT NULL,
 `actorID` int NOT NULL,
 KEY (`movieID`),
 KEY (`actorID`)
);

ALTER TABLE `Movies` 
 ADD CONSTRAINT FOREIGN KEY (`directorID`) REFERENCES `Directors` (`directorID`);

ALTER TABLE `Movies`
 ADD CONSTRAINT FOREIGN KEY (`mpaaRatingsID`) REFERENCES `MpaaRatings` (`mpaaRatingsID`);

ALTER TABLE `Movies` 
 ADD CONSTRAINT FOREIGN KEY (`studioID`) REFERENCES `Studios` (`studioID`);

ALTER TABLE `MoviesxGenres`
 ADD CONSTRAINT FOREIGN KEY (`movieID`) REFERENCES `Movies`
(`movieID`),
 ADD CONSTRAINT FOREIGN KEY (`genreID`) REFERENCES `Genres`
(`genreID`);

ALTER TABLE `MoviesxWriters`
 ADD CONSTRAINT FOREIGN KEY (`movieID`) REFERENCES `Movies`
(`movieID`),
 ADD CONSTRAINT FOREIGN KEY (`writerID`) REFERENCES `Writers`
(`writerID`);

ALTER TABLE `MoviesxActors`
 ADD CONSTRAINT FOREIGN KEY (`movieID`) REFERENCES `Movies`
(`movieID`),
 ADD CONSTRAINT FOREIGN KEY (`actorID`) REFERENCES `Actors`
(`actorID`);

insert into Genres (genreName) values ("Action"), ("Animation"), ("Comedy"), ("Crime"), ("Documentary"),
("Drama"), ("Family"), ("Fantasy"), ("Horror"), ("Musical"), ("Romance"), ("Science Fiction"), ("Sports"), ("Thriller"), ("War"), ("Western");

insert into MpaaRatings (mpaaRatingsName) values ("G"), ("PG"), ("PG-13"), ("R"), ("NC-17");

REVOKE ALL ON Genres FROM PUBLIC;
GRANT SELECT ON Genres TO PUBLIC;

REVOKE ALL ON MpaaRatings FROM PUBLIC;
GRANT SELECT ON MpaaRatings TO PUBLIC;