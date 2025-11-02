package servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FrontServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        chercherRessource(req, resp);
    }

    private void chercherRessource(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getRequestURI().substring(req.getContextPath().length());
        
        if ("/".equals(path)) {
            resp.getWriter().println("/");
            return;
        }

        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        String webInfPath = "/WEB-INF/views/";
        String viewPath;
        if (path.startsWith("WEB-INF/views/")) {
            viewPath = "/" + path;
        } else {
            viewPath = webInfPath + path;
        }

        File file = new File(getServletContext().getRealPath(viewPath));

        if (file.exists() && file.isFile()) {
            resp.setContentType("text/html; charset=UTF-8");
            try (FileInputStream fis = new FileInputStream(file);
                    OutputStream os = resp.getOutputStream()) {

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            }
        } else {
            resp.getWriter().println(path);
        }
    }

}