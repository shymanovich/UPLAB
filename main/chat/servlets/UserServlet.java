package main.chat.servlets;

/**
 * Created by vlad on 02.06.2016.
 */
import main.chat.filters.AuthenticationChecker;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

@WebServlet(value = "/user")
public class UserServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        boolean isMultipart = ServletFileUpload.isMultipartContent(req);
        if (!isMultipart) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        DiskFileItemFactory factory = createFactory();

        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setSizeMax(1024 * 1024 * 50);

        try {
            List items = upload.parseRequest(req);
            for(Object obj : items) {
                FileItem item = (FileItem) obj;
                if (!item.isFormField()) {
                    processUploadAvatar(item, new AuthenticationChecker(req).getUsername());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        resp.sendRedirect("/pages/login.jsp");
    }

    private DiskFileItemFactory createFactory() {
        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(1024 * 1024);
        File tempDir = (File) getServletContext().getAttribute("javax.sevlet.context.tempdir");
        factory.setRepository(tempDir);
        return factory;
    }

    private void processUploadAvatar(FileItem item, String filename) throws Exception {
        File avatar;
        String path = getServletContext().getRealPath("avatars/" + filename + "." + "png");
        avatar = new File(path);
        avatar.createNewFile();
        item.write(avatar);
        BufferedImage image = ImageIO.read(avatar);
        image = resizeImage(image, 70, 70, false);
        avatar.createNewFile();
        ImageIO.write(image, "png", avatar);
    }

    BufferedImage resizeImage(Image image, int scaldeWidth, int scaledHeight, boolean preserveAlpha) {
        int imageType = preserveAlpha ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage scaledBI = new BufferedImage(scaldeWidth, scaledHeight, imageType);
        Graphics2D g = scaledBI.createGraphics();
        if(preserveAlpha) {
            g.setComposite(AlphaComposite.Src);
        }
        g.drawImage(image, 0, 0, scaldeWidth, scaledHeight, null);
        g.dispose();
        return scaledBI;
    }

    private String getExtension(FileItem item) {
        String splitName[] = item.getName().split("\\.");
        return splitName[splitName.length - 1];
    }
}