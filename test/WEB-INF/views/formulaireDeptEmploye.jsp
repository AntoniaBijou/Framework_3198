<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Formulaire Departement avec Emp</title>
</head>
<body>
    <h1>Creer un Departement</h1>
    
    <form action="${pageContext.request.contextPath}/departement/save/map" method="POST">
        <div class="form-group">
            <label for="nom">Nom du departement :</label>
            <input type="text" id="nom" name="nom" required>
        </div>
        
        <div class="form-group">
            <label>Liste des employes :</label>
            <div class="checkbox-group">
                <input type="checkbox" id="emp1" name="employes" value="Jean Dupont">
                <label for="emp1">Jean Dupont</label><br>
                
                <input type="checkbox" id="emp2" name="employes" value="Marie Martin">
                <label for="emp2">Marie Martin</label><br>
                
                <input type="checkbox" id="emp3" name="employes" value="Pierre Durand">
                <label for="emp3">Pierre Durand</label><br>
                
                <input type="checkbox" id="emp4" name="employes" value="Sophie Bernard">
                <label for="emp4">Sophie Bernard</label><br>
            </div>
        </div>
        
        <button type="submit">Enregistrer</button>
    </form>
</body>
</html>