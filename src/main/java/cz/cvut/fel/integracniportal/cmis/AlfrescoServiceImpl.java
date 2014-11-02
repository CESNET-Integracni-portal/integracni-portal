package cz.cvut.fel.integracniportal.cmis;

import cz.cvut.fel.integracniportal.exceptions.ServiceAccessException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class AlfrescoServiceImpl implements AlfrescoService {

    @Autowired
    private Session alfrescoSession;

    @Override
    public Document getFile(String filename) throws CmisObjectNotFoundException {
        return getDocumentByName(filename);
    }

    @Override
    public InputStream getFileContent(String filename) throws CmisObjectNotFoundException {
        Document document = getDocumentByName(filename);
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
    public Document uploadFile(String filename, InputStream fileStream, long filesize, String contentType) throws ServiceAccessException, CmisContentAlreadyExistsException {
        Folder rootFolder = getAlfrescoRootFolder();

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
        properties.put(PropertyIds.NAME, filename);
        ContentStream contentStream = alfrescoSession.getObjectFactory().createContentStream(filename, filesize, contentType, fileStream);

        Document document = rootFolder.createDocument(properties, contentStream, VersioningState.MAJOR);
        return document;
    }

    @Override
    public void updateFileMetadata(String filename, Map<String, String> metadata) throws CmisObjectNotFoundException {
        Document document = getDocumentByName(filename);
        document.updateProperties(metadata, true);
    }

    @Override
    public void updateFileContents(String filename, InputStream fileStream, long filesize, String contentType) throws CmisObjectNotFoundException {
        Document document = getDocumentByName(filename);
        ContentStream contentStream = alfrescoSession.getObjectFactory().createContentStream(filename, filesize, contentType, fileStream);
        document.setContentStream(contentStream, true);
    }

    @Override
    public void deleteFile(String filename) throws CmisObjectNotFoundException {
        Document document = getDocumentByName(filename);
        document.delete();
    }

    private Document getDocumentByName(String filename) throws CmisObjectNotFoundException {
        CmisObject documentObject = getObjectByName(filename);
        if (documentObject.getBaseTypeId().equals(BaseTypeId.CMIS_DOCUMENT)) {
            return (Document) documentObject;
        } else {
            throw new CmisObjectNotFoundException();
        }
    }

    private CmisObject getObjectByName(String filename) throws CmisObjectNotFoundException {
        CmisObject object = alfrescoSession.getObjectByPath("/Integracni-portal/"+filename);
        return object;
    }

    private Folder getAlfrescoRootFolder() throws ServiceAccessException {
        try {
            CmisObject folderObject = alfrescoSession.getObjectByPath("/Integracni-portal");
            if (folderObject != null && folderObject.getBaseTypeId().equals(BaseTypeId.CMIS_FOLDER)) {
                return (Folder) folderObject;
            } else {
                throw new ServiceAccessException("Unable to access alfresco root folder for the application.");
            }
        } catch (CmisObjectNotFoundException e) {
            throw new ServiceAccessException("Unable to access alfresco root folder for the application.");
        }
    }
}
