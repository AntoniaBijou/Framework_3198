<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Ajouter un Employe</title>
</head>
<body>
    <h1>Ajouter un Employe</h1>
    
    <form id="employeForm" action="${pageContext.request.contextPath}/employe/save" method="POST">
        <input type="hidden" name="format" id="formatInput" value="">
        <div class="section">
            <h3>Informations de l'employe</h3>
            
            <div class="form-group">
                <label for="id_employe">ID Employe :</label>
                <input type="number" id="id_employe" name="id_employe" required>
            </div>
            
            <div class="form-group">
                <label for="nom">Nom de l'employe :</label>
                <input type="text" id="nom" name="nom" required>
            </div>
        </div>
        
        <div class="section">
            <h3>Departement</h3>
            
            <div class="form-group">
                <label for="dept_id">ID Departement :</label>
                <input type="number" id="dept_id" name="departement.id_departement" required>
            </div>
            
            <div class="form-group">
                <label for="dept_nom">Nom du Departement :</label>
                <input type="text" id="dept_nom" name="departement.nom" required>
            </div>
        </div>
        
        <button type="submit">Enregistrer</button>
        <button type="button" onclick="submitAsJson()">Enregistrer (JSON)</button>
    </form>
    <script>
        function submitAsJson() {
            document.getElementById('formatInput').value = 'json';
            document.getElementById('employeForm').submit();
        }
    </script>
</body>
</html>