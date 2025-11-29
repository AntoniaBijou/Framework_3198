<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Departement</title>
</head>
<body>
    <h1>Departement Details</h1>
    <% if (request.getAttribute("message") != null) { %>
        <p style="color: green; font-weight: bold;">${message}</p>
    <% } %>
    <p>ID : ${dept.id_departement}</p>
    <p>Libelle : ${dept.nom}</p>
</body>
</html>