<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="test.java.model.Employe" %>
<%@ page import="java.util.List" %>
<html>
<head>
    <title>Liste des Employes</title>
</head>
<body>
    <h1>Liste des Employes</h1>
    
    <% if (request.getAttribute("message") != null) { %>
        <div class="message">${message}</div>
    <% } %>
    
    <a href="${pageContext.request.contextPath}/employe/insert" class="btn">+ Ajouter un employe</a>
    
    <table>
        <thead>
            <tr>
                <th>ID Employe</th>
                <th>Nom</th>
                <th>ID Departement</th>
                <th>Departement</th>
            </tr>
        </thead>
        <tbody>
            <%
                List<Employe> employes = (List<Employe>) request.getAttribute("employes");
                if (employes != null && !employes.isEmpty()) {
                    for (Employe emp : employes) {
            %>
                <tr>
                    <td><%= emp.getId_employe() %></td>
                    <td><%= emp.getNom() %></td>
                    <td><%= emp.getDepartement() != null ? emp.getDepartement().getId_departement() : "N/A" %></td>
                    <td><%= emp.getDepartement() != null ? emp.getDepartement().getNom() : "N/A" %></td>
                </tr>
            <%
                    }
                } else {
            %>
                <tr>
                    <td colspan="4" style="text-align: center; color: #999;">Aucun employe enregistre</td>
                </tr>
            <%
                }
            %>
        </tbody>
    </table>
</body>
</html>