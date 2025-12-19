<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ page contentType="text/html;charset=UTF-8" language="java" %>

        </html>

        <body>
            </head>
            <title>Resultat Upload</title>

            <head>
                <html>
                <html>

                <head>
                    <title>Resultat Upload</title>
                </head>

            <body>
                <h1>Resultat de l'upload</h1>
                <p><strong>Message :</strong> ${message}</p>
                <c:if test="${not empty fileName}">
                    <p><strong>Nom :</strong> ${fileName}</p>
                    <p><strong>Type :</strong> ${contentType}</p>
                    <p><strong>Taille :</strong> ${size} octets</p>
                    <p><strong>Chemin :</strong> ${filePath}</p>
                </c:if>

                <p><a href="${pageContext.request.contextPath}/file/upload">Retour</a></p>
            </body>
        </body>

            </html>