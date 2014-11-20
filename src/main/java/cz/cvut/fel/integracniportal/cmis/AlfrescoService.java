package cz.cvut.fel.integracniportal.cmis;

import cz.cvut.fel.integracniportal.exceptions.ServiceAccessException;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.exceptions.CmisContentAlreadyExistsException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisPermissionDeniedException;

import java.io.InputStream;
import java.util.Map;

public interface AlfrescoService {

    public Session getSessionForUser(String username, String password);

    public Session getSessionForCurrentUser() throws ServiceAccessException;

    public Session getSessionForAdmin();

    public void createAlfrescoAccount(String username, String password) throws ServiceAccessException;

    public Folder getAlfrescoRootFolder(Session session) throws ServiceAccessException;

    public Folder getFolder(String id) throws CmisObjectNotFoundException, ServiceAccessException;

    public Document getFile(String id) throws CmisObjectNotFoundException, ServiceAccessException;

    public InputStream getFileContent(String id) throws CmisObjectNotFoundException, ServiceAccessException;

    public InputStream getFileContent(Document document) throws CmisObjectNotFoundException;

    public Document uploadFile(Folder parent, String filename, InputStream fileStream, long filesize, String contentType) throws ServiceAccessException, CmisContentAlreadyExistsException, CmisPermissionDeniedException;

    public void updateFileMetadata(String id, Map<String, String> metadata) throws CmisObjectNotFoundException, ServiceAccessException;

    public void updateFileContents(String id, InputStream fileStream, long filesize, String contentType) throws CmisObjectNotFoundException, ServiceAccessException;

    public void deleteFile(String id) throws CmisObjectNotFoundException, ServiceAccessException;

}
