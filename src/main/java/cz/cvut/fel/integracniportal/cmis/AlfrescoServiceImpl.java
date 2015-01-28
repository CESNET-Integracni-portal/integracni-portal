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
import org.apache.chemistry.opencmis.commons.data.*;
import org.apache.chemistry.opencmis.commons.enums.AclPropagation;
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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AlfrescoServiceImpl implements AlfrescoService {

    private static final Logger logger = Logger.getLogger(AlfrescoServiceImpl.class);

    @Value("${alfresco.addPersonUrl}")
    private String addPersonUrl;

    @Value("${alfresco.path}")
    private String rootFolder;

    @Value("${alfresco.usernamePrefix}")
    private String usernamePrefix;

    @Autowired
    private CmisSessionFactory alfrescoSessionFactory;

    @Autowired
    private UserDetailsService userDetailsService;

    private Pattern sharedFolderPathPattern;

    @PostConstruct
    private void prepareSharedFolderPathPattern() {
        StringBuilder sb = new StringBuilder(rootFolder);
        if (!rootFolder.endsWith("/")) {
            sb.append("/");
        }
        sb.append("([^/]*)/shared(?:/.*)?");
        sharedFolderPathPattern = Pattern.compile(sb.toString());
    }

    @Override
    public Session getSessionForUser(String username, String password) {
        return alfrescoSessionFactory.createSessionForUser(username, password);
    }

    @Override
    @Transactional(rollbackFor = ServiceAccessException.class)
    public Session getSessionForCurrentUser() throws ServiceAccessException {
        UserDetails currentUser = userDetailsService.getCurrentUser();
        // Create alfresco account for the user if he doesn't have one.
        createAlfrescoAccountIfNotExist(currentUser);

        return alfrescoSessionFactory.createSessionForUser(currentUser.getAlfrescoUsername(), currentUser.getAlfrescoPassword());
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
        jsonRestTemplate.addParameter("email", username + "@int-portal.cvut.cz");
        jsonRestTemplate.addParameter("password", password);
        ResponseEntity<String> response = jsonRestTemplate.post(addPersonUrl, String.class);
        if (!response.getStatusCode().series().equals(HttpStatus.Series.SUCCESSFUL)) {
            throw new ServiceAccessException("alfresco.unableToCreateUser");
        }
    }

    @Override
    public Folder getAlfrescoRootFolder(Session session) throws ServiceAccessException {
        try {
            CmisObject folderObject = session.getObjectByPath(rootFolder);
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
    public Folder getHomeFolderForCurrentUser() throws ServiceAccessException {
        return getHomeFolderForUser(userDetailsService.getCurrentUser(), getSessionForCurrentUser());
    }

    @Override
    public Folder getHomeFolderForUser(UserDetails userDetails, Session session) throws ServiceAccessException {
        Folder folder = getFolderByName(getAlfrescoRootFolder(session), userDetails.getAlfrescoUsername() + "/home", session);
        return folder;
    }

    @Override
    public Folder getSharedFolderForCurrentUser() throws ServiceAccessException {
        return getSharedFolderForUser(userDetailsService.getCurrentUser(), getSessionForCurrentUser());
    }

    @Override
    public Folder getSharedFolderForUser(UserDetails userDetails, Session session) throws ServiceAccessException {
        Folder folder = getFolderByName(getAlfrescoRootFolder(session), userDetails.getAlfrescoUsername() + "/shared", session);
        return folder;
    }

    @Override
    public Folder getFolder(String id, Session session) throws CmisObjectNotFoundException, ServiceAccessException {
        CmisObject foldertObject = getObjectById(id, session);
        if (foldertObject.getBaseTypeId().equals(BaseTypeId.CMIS_FOLDER)) {
            return (Folder) foldertObject;
        } else {
            throw new CmisObjectNotFoundException();
        }
    }

    @Override
    public Folder getFolderByName(Folder parent, String folderName, Session session) throws CmisObjectNotFoundException, ServiceAccessException {
        CmisObject folderObject = getObjectByName(parent, folderName, session);
        if (folderObject.getBaseTypeId().equals(BaseTypeId.CMIS_FOLDER)) {
            return (Folder) folderObject;
        } else {
            throw new CmisObjectNotFoundException();
        }
    }

    @Override
    public Document getFile(String id, Session session) throws CmisObjectNotFoundException, ServiceAccessException {
        CmisObject documentObject = getObjectById(id, session);
        if (documentObject.getBaseTypeId().equals(BaseTypeId.CMIS_DOCUMENT)) {
            return (Document) documentObject;
        } else {
            throw new CmisObjectNotFoundException();
        }
    }

    @Override
    public Document getFileByName(Folder parent, String fileName, Session session) throws CmisObjectNotFoundException, ServiceAccessException {
        CmisObject documentObject = getObjectByName(parent, fileName, session);
        if (documentObject.getBaseTypeId().equals(BaseTypeId.CMIS_DOCUMENT)) {
            return (Document) documentObject;
        } else {
            throw new CmisObjectNotFoundException();
        }
    }

    @Override
    public InputStream getFileContent(String id, Session session) throws CmisObjectNotFoundException, ServiceAccessException {
        Document document = getFile(id, session);
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
    public Folder createFolder(Folder parent, String folderName) {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
        properties.put(PropertyIds.NAME, folderName);
        Folder folder = parent.createFolder(properties);
        return folder;
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
    public void updateFileMetadata(String id, Map<String, String> metadata, Session session) throws CmisObjectNotFoundException, ServiceAccessException {
        Document document = getFile(id, session);
        document.updateProperties(metadata, true);
    }

    @Override
    public void updateFileContents(String id, InputStream fileStream, long filesize, String contentType, Session session) throws CmisObjectNotFoundException, ServiceAccessException {
        Document document = getFile(id, session);
        ContentStream contentStream = getSessionForCurrentUser().getObjectFactory().createContentStream(document.getName(), filesize, contentType, fileStream);
        document.setContentStream(contentStream, true);
    }

    @Override
    public void shareFileWithUsers(String fileId, List<UserDetails> targetUsers) throws ServiceAccessException {
        // First, make sure that the file is accessible to current user
        getFile(fileId, getSessionForCurrentUser());

        Session adminSession = getSessionForAdmin();
        Document document = getFile(fileId, adminSession);

        // Remove the file from all shared folders
        List<Folder> fileParents = new ArrayList<Folder>(document.getParents());
        for (Folder fileParent : fileParents) {
            String path = fileParent.getPath();
            if (sharedFolderPathPattern.matcher(path).matches()) {
                document.removeFromFolder(fileParent);
            }
        }

        for (UserDetails targetUser : targetUsers) {
            // Make sure that the target user has an Alfresco account created
            createAlfrescoAccountIfNotExist(targetUser);

            // Add the folder to 'shared' folder in target user's home
            Folder targetUserShareFolder = getSharedFolderForUser(targetUser, adminSession);
            document.addToFolder(targetUserShareFolder, true);
        }
    }

    @Override
    public List<String> getSharedWith(Document document) {
        List<String> sharedWithList = new ArrayList<String>();
        for (Folder fileParent : document.getParents()) {
            String path = fileParent.getPath();
            Matcher pathMatcher = sharedFolderPathPattern.matcher(path);
            if (pathMatcher.matches()) {
                String sharedWith = pathMatcher.group(1).replaceFirst(usernamePrefix, "");
                sharedWithList.add(sharedWith);
            }
        }
        return sharedWithList;
    }

    @Override
    public void addPermission(CmisObject object, String principal, String... permissions) {
        Session adminSession = getSessionForAdmin();
        RepositoryInfo repositoryInfo = adminSession.getRepositoryInfo();
        AclCapabilities aclCapabilities = repositoryInfo.getAclCapabilities();
        Map<String, PermissionMapping> permissionMappings = aclCapabilities.getPermissionMapping();

        List<Ace> addAces = new LinkedList<Ace>();
        for (String permission : permissions) {
            PermissionMapping permissionMapping = permissionMappings.get(permission);
            List<String> permissionList = permissionMapping.getPermissions();
            // We need to filter out all "cmis:*" permissions, because those cause problems when they are inherited
            List<String> addPermissionList = new ArrayList<String>();
            for (String p : permissionList) {
                if (!p.startsWith("cmis:")) {
                    addPermissionList.add(p);
                }
            }
            Ace addAce = adminSession.getObjectFactory().createAce(principal, addPermissionList);
            addAces.add(addAce);
        }
        object.addAcl(addAces, AclPropagation.REPOSITORYDETERMINED);
    }

    private Folder getUserFolderForUser(UserDetails userDetails, Session session) throws ServiceAccessException {
        Folder folder = getFolderByName(getAlfrescoRootFolder(session), userDetails.getAlfrescoUsername(), session);
        return folder;
    }

    private CmisObject getObjectById(String id, Session session) throws ServiceAccessException {
        CmisObject object = session.getObject(id);
        return object;
    }

    private CmisObject getObjectByName(Folder parent, String objectName, Session session) throws ServiceAccessException {
        StringBuilder pathBuilder = new StringBuilder();
        if (parent == null) {
            pathBuilder.append("/");
        } else {
            pathBuilder.append(parent.getPath());
            pathBuilder.append("/");
        }
        pathBuilder.append(objectName);
        CmisObject object = session.getObjectByPath(pathBuilder.toString());
        return object;
    }

    private void createAlfrescoAccountIfNotExist(UserDetails user) throws ServiceAccessException {
        if (user.getAlfrescoUsername() == null) {
            StringBuilder sb = new StringBuilder(usernamePrefix);
            sb.append(user.getUsername());
            String alfrescoUsername = sb.toString();
            String alfrescoPassword = UUID.randomUUID().toString();
            createAlfrescoAccount(alfrescoUsername, alfrescoPassword);
            user.setAlfrescoUsername(alfrescoUsername);
            user.setAlfrescoPassword(alfrescoPassword);
            userDetailsService.saveUser(user);

            // Create user folder for the newly created user. Currently, the name will be the same as username.
            Session adminSession = getSessionForAdmin();
            Folder userFolder = createFolder(getAlfrescoRootFolder(adminSession), alfrescoUsername);

            // Create home folder
            Folder homeFolder = createFolder(userFolder, "home");
            addPermission(homeFolder, alfrescoUsername, PermissionMapping.CAN_GET_CHILDREN_FOLDER, PermissionMapping.CAN_VIEW_CONTENT_OBJECT,
                    PermissionMapping.CAN_GET_PARENTS_FOLDER, PermissionMapping.CAN_GET_ALL_VERSIONS_VERSION_SERIES,
                    PermissionMapping.CAN_CREATE_DOCUMENT_FOLDER, PermissionMapping.CAN_CREATE_FOLDER_FOLDER, PermissionMapping.CAN_UPDATE_PROPERTIES_OBJECT,
                    PermissionMapping.CAN_DELETE_OBJECT, PermissionMapping.CAN_SET_CONTENT_DOCUMENT);

            // Create shared folder
            Folder sharedFolder = createFolder(userFolder, "shared");
            addPermission(sharedFolder, alfrescoUsername, PermissionMapping.CAN_GET_CHILDREN_FOLDER, PermissionMapping.CAN_VIEW_CONTENT_OBJECT,
                    PermissionMapping.CAN_GET_PARENTS_FOLDER, PermissionMapping.CAN_GET_ALL_VERSIONS_VERSION_SERIES);
        }
    }

}
