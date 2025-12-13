<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>
<body>
    
    <!-- ===== SPRINT 8 : Affichage des parametres reçus via Map ===== -->
    <% if (request.getAttribute("params") != null) { %>
        <hr>
        <div style="background-color: #f0f0f0; padding: 15px; border-radius: 5px; margin-top: 20px;">
            <h2>Résultat attendu :</h2>
            <p><strong>Parametres reçus :</strong></p>
            <ul>
                <%
                    java.util.Map<String, String> params = (java.util.Map<String, String>) request.getAttribute("params");
                    for (java.util.Map.Entry<String, String> entry : params.entrySet()) {
                %>
                    <li><strong><%= entry.getKey() %></strong> : [<%= entry.getValue() %>]</li>
                <%
                    }
                %>
            </ul>
        </div>
    <% } %>
</body>
</html>