package cz.cvut.fel.integracniportal.cmis;

import cz.cvut.fel.integracniportal.exceptions.ServiceAccessException;
import cz.cvut.fel.integracniportal.model.UserDetails;
import cz.cvut.fel.integracniportal.service.UserDetailsService;
import cz.cvut.fel.integracniportal.utils.JsonRestTemplate;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisContentAlreadyExistsException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisPermissionDeniedException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AlfrescoServiceImpl implements AlfrescoService {

    public enum Permissions {
        READ("cmis:read"),
        WRITE("cmis:write"),
        ALL("cmis:all");

        private final String value;

        Permissions(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }

    private static final Logger logger = Logger.getLogger(AlfrescoServiceImpl.class);

    @Value("${alfresco.addPersonUrl}")
    private String addPersonUrl;

    @Value("${alfresco.path}")
    private String rootFolder;

    @Autowired
    private CmisSessionFactory alfrescoSessionFactory;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public Session getSessionForUser(String username, String password) {
        return alfrescoSessionFactory.createSessionForUser(username, password);
    }

    @Override
    @Transactional(rollbackFor = ServiceAccessException.class)
    public Session getSessionForCurrentUser() throws ServiceAccessException {
        UserDetails currentUser = userDetailsService.getCurrentUser();
        StringBuilder sb = new StringBuilder("int-portal-");
        sb.append(currentUser.getUsername());
        String alfrescoUsername = sb.toString();
        String alfrescoPassword = currentUser.getAlfrescoPassword();

        // Create alfresco account for the user if he doesn't have one.
        if (alfrescoPassword == null) {
            alfrescoPassword = UUID.randomUUID().toString();
            createAlfrescoAccount(alfrescoUsername, alfrescoPassword);
            currentUser.setAlfrescoPassword(alfrescoPassword);
            userDetailsService.saveUser(currentUser);
        }

        return alfrescoSessionFactory.createSessionForUser(alfrescoUsername, alfrescoPassword);
    }

    @Override
    public Session getSessionForAdmin() {
        return alfrescoSessionFactory.createAdminSession();
    }

    @Override
    public void createAlfrescoAccount(String username, String password) throws ServiceAccessException {
        JsonRestTemplate jsonRestTemplate = new JsonRestTemplate();
        jsonRestTemplate.addBasicAuthHeader(alfrescoSessionFactory.getAdminUsername(), alfrescoSessionFactory.getAdminPassword());
        jsonRestTemplate.addParameter("userName", username);
        jsonRestTemplate.addParameter("firstName", username);
        jsonRestTemplate.addParameter("lastName", username);
        jsonRestTemplate.addParameter("email", username+"@int-portal.cvut.cz");
        jsonRestTemplate.addParameter("password", password);
        ResponseEntity<String> response = jsonRestTemplate.post(addPersonUrl, String.class);
        if (!response.getStatusCode().series().equals(HttpStatus.Series.SUCCESSFUL)) {
            throw new ServiceAccessException("alfresco.unableToCreateUser");
        }
    }

    @Override
    public Folder getAlfrescoRootFolder(Session session) throws ServiceAccessException {
        try {
            CmisObject folderObject = session.getObjectByPath("/Integracni-portal");
            if (folderObject != null && folderObject.getBaseTypeId().equals(BaseTypeId.CMIS_FOLDER)) {
                return (Folder) folderObject;
            } else {
                throw new ServiceAccessException("alfresco.rootFolderInaccesible");
            }
        } catch (CmisObjectNotFoundException e) {
            throw new ServiceAccessException("alfresco.rootFolderInaccesible");
        }
    }

    @Override
    public Folder getFolder(String id) throws CmisObjectNotFoundException, ServiceAccessException {
        CmisObject foldertObject = getObjectById(id);
        if (foldertObject.getBaseTypeId().equals(BaseTypeId.CMIS_FOLDER)) {
            return (Folder) foldertObject;
        } else {
            throw new CmisObjectNotFoundException();
        }
    }

    @Override
    public Document getFile(String id) throws CmisObjectNotFoundException, ServiceAccessException {
        CmisObject documentObject = getObjectById(id);
        if (documentObject.getBaseTypeId().equals(BaseTypeId.CMIS_DOCUMENT)) {
            return (Document) documentObject;
        } else {
            throw new CmisObjectNotFoundException();
        }
    }

    @Override
    public InputStream getFileContent(String id) throws CmisObjectNotFoundException, ServiceAccessException {
        Document document = getFile(id);
        return getFileContent(document);
    }

    @Override
    public InputStream getFileContent(Document document) throws CmisObjectNotFoundException {
        ContentStream contentStream = document.getContentStream();
        if (contentStream == null) {
            byte[] emptyBuf = new byte[0];
            return new ByteArrayInputStream(emptyBuf);
        } else {
            return contentStream.getStream();
        }
    }

    @Override
    public Document uploadFile(Folder parent, String filename, InputStream fileStream, long filesize, String contentType) throws ServiceAccessException, CmisContentAlreadyExistsException, CmisPermissionDeniedException {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
        properties.put(PropertyIds.NAME, filename);
        ContentStream contentStream = getSessionForCurrentUser().getObjectFactory().createContentStream(filename, filesize, contentType, fileStream);

        Document document = parent.createDocument(properties, contentStream, VersioningState.MAJOR);
        return document;
    }

    @Override
    public void updateFileMetadata(String id, Map<String, String> metadata) throws CmisObjectNotFoundException, ServiceAccessException {
        Document document = getFile(id);
        document.updateProperties(metadata, true);
    }

    @Override
    public void updateFileContents(String id, InputStream fileStream, long filesize, String contentType) throws CmisObjectNotFoundException, ServiceAccessException {
        Document document = getFile(id);
        ContentStream contentStream = getSessionForCurrentUser().getObjectFactory().createContentStream(document.getName(), filesize, contentType, fileStream);
        document.setContentStream(contentStream, true);
    }

    @Override
    public void deleteFile(String id) throws CmisObjectNotFoundException, ServiceAccessException {
        Document document = getFile(id);
        document.delete();
    }

    private CmisObject getObjectById(String id) throws ServiceAccessException {
        CmisObject object = getSessionForCurrentUser().getObject(id);
        return object;
    }

}
