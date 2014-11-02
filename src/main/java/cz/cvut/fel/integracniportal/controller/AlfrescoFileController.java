package cz.cvut.fel.integracniportal.controller;

import cz.cvut.fel.integracniportal.cmis.AlfrescoService;
import cz.cvut.fel.integracniportal.exceptions.ServiceAccessException;
import cz.cvut.fel.integracniportal.resource.FileMetadataResource;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.commons.exceptions.CmisContentAlreadyExistsException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

@Controller
@RequestMapping("/rest")
public class AlfrescoFileController {

    private static final Logger logger = Logger.getLogger(AlfrescoFileController.class);

    @Autowired
    private AlfrescoService alfrescoService;

    /**
     * Return metadata of a file.
     * @param fileUri    The URI of the file.
     * @return File metadata.
     */
    @RequestMapping(value = "/v0.1/alfresco/{fileuri}/metadata", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<FileMetadataResource> alfrescoGetFileMetadata(@PathVariable("fileuri") String fileUri) {
        try {

            Document document = alfrescoService.getFile(fileUri);
            FileMetadataResource metadata = new FileMetadataResource();
            metadata.setFilename(document.getName());
            metadata.setFilesize(document.getContentStreamLength());
            metadata.setCreatedOn(document.getCreationDate().getTime());
            metadata.setChangedOn(document.getLastModificationDate().getTime());
            metadata.setMimetype(document.getContentStreamMimeType());
            return new ResponseEntity<FileMetadataResource>(metadata, HttpStatus.OK);

        } catch (CmisObjectNotFoundException e) {
            return new ResponseEntity<FileMetadataResource>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Download a file.
     * @param fileUri    The URI of the file.
     */
    @RequestMapping(value = "/v0.1/alfresco/{fileuri}", method = RequestMethod.GET)
    public void alfrescoGet(HttpServletResponse response, @PathVariable("fileuri") String fileUri) {
        try {

            Document document = alfrescoService.getFile(fileUri);
            response.setContentType(document.getContentStreamMimeType());
            response.setHeader("Content-Disposition", "attachment; filename=\""+document.getName()+"\"");
            InputStream documentContentStream = alfrescoService.getFileContent(document);
            IOUtils.copy(documentContentStream, response.getOutputStream());
            response.flushBuffer();

        } catch (CmisObjectNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        }
    }

    /**
     * Update a file.
     * @param fileUri    The URI of the file.
     * @param file        New file to replace the original one.
     */
    @RequestMapping(value = "/v0.1/alfresco/{fileuri}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<String> alfrescoUpdate(@PathVariable("fileuri") String fileUri, @RequestParam(value = "file", required = true) MultipartFile file) {
        try {

            alfrescoService.updateFileContents(fileUri, file.getInputStream(), file.getSize(), file.getContentType());
            return new ResponseEntity<String>(HttpStatus.NO_CONTENT);

        } catch (CmisObjectNotFoundException e) {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Delete a file.
     * @param fileUri    The URI of the file.
     */
    @RequestMapping(value = "/v0.1/alfresco/{fileuri}", method = RequestMethod.DELETE)
    public ResponseEntity<String> alfrescoDelete(@PathVariable("fileuri") String fileUri) {
        try {
            alfrescoService.deleteFile(fileUri);
            return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
        } catch (CmisObjectNotFoundException e) {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Upload a file.
     * @param file    File to be uploaded.
     */
    @RequestMapping(value = "/v0.1/alfresco", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> alfrescoUpload(@RequestParam(value = "file", required = true) MultipartFile file) {
        try {

            Document document = alfrescoService.uploadFile(file.getOriginalFilename(), file.getInputStream(), file.getSize(), file.getContentType());
            if (document == null) {
                return new ResponseEntity<String>(HttpStatus.SERVICE_UNAVAILABLE);
            }
            return new ResponseEntity<String>("/rest/v0.1/alfresco/" + document.getContentStreamFileName(), HttpStatus.CREATED);

        } catch (CmisContentAlreadyExistsException e) {
            return new ResponseEntity<String>(HttpStatus.CONFLICT);
        } catch (IOException e) {
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        } catch (ServiceAccessException e) {
            return new ResponseEntity<String>(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
}
