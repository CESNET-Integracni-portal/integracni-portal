package cz.cvut.fel.integracniportal.cmis;

import cz.cvut.fel.integracniportal.exceptions.ServiceAccessException;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.commons.exceptions.CmisContentAlreadyExistsException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;

import java.io.InputStream;
import java.util.Map;

public interface AlfrescoService {

    public Document getFile(String filename) throws CmisObjectNotFoundException;

    public InputStream getFileContent(String filename) throws CmisObjectNotFoundException;

    public InputStream getFileContent(Document document) throws CmisObjectNotFoundException;

    public Document uploadFile(String filename, InputStream fileStream, long filesize, String contentType) throws ServiceAccessException, CmisContentAlreadyExistsException;

    public void updateFileMetadata(String filename, Map<String, String> metadata) throws CmisObjectNotFoundException;

    public void updateFileContents(String filename, InputStream fileStream, long filesize, String contentType) throws CmisObjectNotFoundException;

    public void deleteFile(String filename) throws CmisObjectNotFoundException;

}
