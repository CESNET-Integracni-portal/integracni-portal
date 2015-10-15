package cz.cvut.fel.integracniportal.utils;

import cz.cvut.fel.integracniportal.exceptions.InvalidStateException;
import cz.cvut.fel.integracniportal.service.FileUpload;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author Radek Jezdik
 */
public class UploadUtils {

    private UploadUtils() {
        // static class
    }

    public static FileUpload handleFileUpload(HttpServletRequest request) throws IOException, FileUploadException {
        if (!ServletFileUpload.isMultipartContent(request)) {
            throw new InvalidStateException("Invalid file upload request.");
        }

        ServletFileUpload fileUpload = new ServletFileUpload();

        FileItemIterator itemIterator = fileUpload.getItemIterator(request);
        while (itemIterator.hasNext()) {
            FileItemStream item = itemIterator.next();

            if (!item.isFormField()) {
                MediaType mediaType = MediaType.parseMediaType(item.getContentType());
                return new FileUpload(item.getName(), mediaType.getType() + "/" + mediaType.getSubtype(), item.openStream());
            }
        }

        throw new InvalidStateException("No file uploaded");
    }

}
