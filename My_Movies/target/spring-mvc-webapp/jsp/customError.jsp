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
        <title>Movie Library</title>
        <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/MovieLibraryCSS.css" rel="stylesheet">
    </head>
    <body>
        <div class='container-fluid'>
            <h1 class='text-center'>Movie Library</h1>
            <div>
                <h1>An error has occurred...</h1>
                <h3>${errorMessage}</h3>
            </div>
        </div>
        <script src="${pageContext.request.contextPath}/js/jquery-2.2.4.min.js"></script>
        <script src="${pageContext.request.contextPath}/js/bootstrap.min.js"></script>
        <script src="js/MovieLibraryJS.js"></script>
    </body>
</html>
