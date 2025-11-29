<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Formulaire Departement</title>
</head>
<body>
    <h1>Inserer un Departement</h1>
    <form action="${pageContext.request.contextPath}/departement/save" method="post">
        <label for="id_departement">ID Departement :</label>
        <input type="number" id="id_departement" name="id_departement" required><br><br>
        
        <label for="nom">Nom :</label>
        <input type="text" id="nom" name="nom" required><br><br>
        
        <button type="submit">Valider</button>
    </form>
</body>
</html>