<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Uploader un fichier</title>
</head>
<body>
<h1>Uploader un fichier</h1>

<form id="uploadForm" action="${pageContext.request.contextPath}/file/save" method="POST" enctype="multipart/form-data">
    <input type="hidden" name="format" id="formatInput" value="">
    <div>
        <label for="file">Fichier :</label>
        <input type="file" id="file" name="file" required>
    </div>
    <div>
        <button type="submit">Uploader</button>
        <button type="button" onclick="submitAsJson()">Uploader (JSON)</button>
    </div>
</form>

<script>
    function submitAsJson() {
        document.getElementById('formatInput').value = 'json';
        document.getElementById('uploadForm').submit();
    }
</script>
</body>
</html>