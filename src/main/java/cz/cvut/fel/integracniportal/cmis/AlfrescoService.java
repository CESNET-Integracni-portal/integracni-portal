package cz.cvut.fel.integracniportal.cmis;

import cz.cvut.fel.integracniportal.exceptions.ServiceAccessException;
import cz.cvut.fel.integracniportal.model.UserDetails;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.exceptions.CmisContentAlreadyExistsException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisPermissionDeniedException;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface AlfrescoService {

    public Session getSessionForUser(String username, String password);

    public Session getSessionForCurrentUser() throws ServiceAccessException;

    public Session getSessionForAdmin();

    public void createAlfrescoAccount(String username, String password) throws ServiceAccessException;

    public Folder getAlfrescoRootFolder(Session session) throws ServiceAccessException;

    public Folder getHomeFolderForCurrentUser() throws ServiceAccessException;

    public Folder getHomeFolderForUser(UserDetails userDetails, Session session) throws ServiceAccessException;

    public Folder getSharedFolderForCurrentUser() throws ServiceAccessException;

    public Folder getSharedFolderForUser(UserDetails userDetails, Session session) throws ServiceAccessException;

    public Folder getFolder(String id, Session session) throws CmisObjectNotFoundException, ServiceAccessException;

    public Folder getFolderByName(Folder parent, String folderName, Session session) throws CmisObjectNotFoundException, ServiceAccessException;

    public Document getFile(String id, Session session) throws CmisObjectNotFoundException, ServiceAccessException;

    public Document getFileByName(Folder parent, String fileName, Session session) throws CmisObjectNotFoundException, ServiceAccessException;

    public InputStream getFileContent(String id, Session session) throws CmisObjectNotFoundException, ServiceAccessException;

    public InputStream getFileContent(Document document) throws CmisObjectNotFoundException;

    public Folder createFolder(Folder parent, String folderName);

    public Document uploadFile(Folder parent, String filename, InputStream fileStream, long filesize, String contentType) throws ServiceAccessException, CmisContentAlreadyExistsException, CmisPermissionDeniedException;

    public void updateFileMetadata(String id, Map<String, String> metadata, Session session) throws CmisObjectNotFoundException, ServiceAccessException;

    public void updateFileContents(String id, InputStream fileStream, long filesize, String contentType, Session session) throws CmisObjectNotFoundException, ServiceAccessException;

    public void shareFileWithUsers(String fileId, List<UserDetails> targetUsers) throws ServiceAccessException;

    public List<String> getSharedWith(Document document);

    public void addPermission(CmisObject object, String principal, String... permissions);

}
