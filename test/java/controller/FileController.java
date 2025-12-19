package test.java.controller;

import servlet.Controller;
import servlet.WebRoute;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Paths;

@Controller
public class FileController {

    @WebRoute(url = "/file/upload", method = "GET")
    public String showUploadForm(HttpServletRequest req, HttpServletResponse resp) {
        return "uploadForm";
    }

    @WebRoute(url = "/file/save", method = "POST")
    public String handleUpload(HttpServletRequest req, HttpServletResponse resp) {
        try {
            Part filePart = req.getPart("file");
            if (filePart == null || filePart.getSize() == 0) {
                req.setAttribute("message", "Aucun fichier fourni");
                return "fileResult";
            }

            String submittedFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            String contentType = filePart.getContentType();
            long size = filePart.getSize();

            String uploadsDir = req.getServletContext().getRealPath("/uploads");
            File uploads = new File(uploadsDir);
            if (!uploads.exists())
                uploads.mkdirs();

            File dest = new File(uploads, submittedFileName);
            try (InputStream is = filePart.getInputStream(); FileOutputStream fos = new FileOutputStream(dest)) {
                byte[] buffer = new byte[4096];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, read);
                }
            }

            req.setAttribute("fileName", submittedFileName);
            req.setAttribute("contentType", contentType);
            req.setAttribute("size", size);
            req.setAttribute("filePath", dest.getAbsolutePath());
            req.setAttribute("message", "Fichier ajoute avec succes !");
            return "fileResult";
        } catch (Exception e) {
            req.setAttribute("message", "Erreur upload : " + e.getMessage());
            return "fileResult";
        }
    }
}