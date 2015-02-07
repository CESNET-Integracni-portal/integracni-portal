package cz.cvut.fel.integracniportal.controller;

import cz.cvut.fel.integracniportal.cmis.AlfrescoService;
import cz.cvut.fel.integracniportal.cmis.AlfrescoUtils;
import cz.cvut.fel.integracniportal.exceptions.NotFoundException;
import cz.cvut.fel.integracniportal.model.UserDetails;
import cz.cvut.fel.integracniportal.representation.FileMetadataRepresentation;
import cz.cvut.fel.integracniportal.representation.FolderRepresentation;
import cz.cvut.fel.integracniportal.service.UserDetailsService;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/rest")
public class HomeController extends AbstractController {

    private static final Logger logger = Logger.getLogger(HomeController.class);

    @Autowired
    private AlfrescoService alfrescoService;

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * Get contents of the home folder.
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.1/home", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> alfrescoGetHome() {
        Folder homeFolder = alfrescoService.getHomeFolderForCurrentUser();
        FolderRepresentation homeFolderRepresentation = new FolderRepresentation(homeFolder, homeFolder);
        return new ResponseEntity<Object>(homeFolderRepresentation, HttpStatus.OK);
    }

    /**
     * Create a subfolder
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.1/home", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public ResponseEntity<String> alfrescoCreateFolderInHome(@RequestBody FolderRepresentation folderRepresentation) {
        Folder targetFolder = alfrescoService.getHomeFolderForCurrentUser();
        Folder folder = alfrescoService.createFolder(targetFolder, folderRepresentation.getName());
        if (folder == null) {
            return new ResponseEntity<String>(HttpStatus.SERVICE_UNAVAILABLE);
        }
        return new ResponseEntity<String>("/rest/v0.1/home/folder/" + AlfrescoUtils.parseId(folder), HttpStatus.CREATED);
    }

    /**
     * Upload a file to home folder.
     *
     * @param file File to be uploaded.
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.1/home", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> alfrescoUploadToHome(@RequestParam(value = "file", required = true) MultipartFile file) throws IOException {
        Document document = alfrescoService.uploadFile(alfrescoService.getHomeFolderForCurrentUser(),
                file.getOriginalFilename(), file.getInputStream(), file.getSize(), file.getContentType());
        if (document == null) {
            return new ResponseEntity<String>(HttpStatus.SERVICE_UNAVAILABLE);
        }
        return new ResponseEntity<String>("/rest/v0.1/home/file/" + document.getContentStreamFileName(), HttpStatus.CREATED);
    }


    /**
     * Return metadata of a file.
     *
     * @param uuid The uuid of the file.
     * @return File metadata.
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = {"/v0.1/home/file/{uuid}", "/v0.1/shared/file/{uuid}"}, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> alfrescoGetFileMetadata(@PathVariable("uuid") String uuid) {
        Document document = alfrescoService.getFile(uuid, alfrescoService.getSessionForCurrentUser());
        FileMetadataRepresentation metadata = new FileMetadataRepresentation(document);
        metadata.setSharedWith(alfrescoService.getSharedWith(document));
        return new ResponseEntity<Object>(metadata, HttpStatus.OK);
    }

    /**
     * Update metadata of a file.
     *
     * @param uuid The uuid of the file.
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.1/home/file/{uuid}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<Object> alfrescoUpdateFileMetadata(@PathVariable("uuid") String uuid,
                                                             @RequestBody FileMetadataRepresentation fileMetadataRepresentation) {
        Document document = alfrescoService.getFile(uuid, alfrescoService.getSessionForCurrentUser());
        if (fileMetadataRepresentation.getFilename() != null) {
            document.rename(fileMetadataRepresentation.getFilename());
        }

        if (fileMetadataRepresentation.getSharedWith() != null) {
            List<UserDetails> shareWithList = new ArrayList<UserDetails>();
            for (String shareWithUsername : fileMetadataRepresentation.getSharedWith()) {
                UserDetails shareWith = userDetailsService.getUserByUsername(shareWithUsername);
                if (shareWith == null) {
                    throw new NotFoundException("user.notFound.name", shareWithUsername);
                }
                shareWithList.add(shareWith);
            }
            alfrescoService.shareFileWithUsers(uuid, shareWithList);
        }

        return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
    }

    /**
     * Delete a file.
     *
     * @param uuid The uuid of the file.
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.1/home/file/{uuid}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> alfrescoDelete(@PathVariable("uuid") String uuid) {
        Document document = alfrescoService.getFile(uuid, alfrescoService.getSessionForCurrentUser());
        document.delete();
        return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
    }

    /**
     * Download a file.
     *
     * @param uuid The uuid of the file.
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = {"/v0.1/home/file/{uuid}/content", "/v0.1/shared/file/{uuid}/content"}, method = RequestMethod.GET)
    public void alfrescoGet(HttpServletResponse response, @PathVariable("uuid") String uuid) throws IOException {
        Document document = alfrescoService.getFile(uuid, alfrescoService.getSessionForCurrentUser());
        response.setContentType(document.getContentStreamMimeType());
        response.setHeader("Content-Disposition", "attachment; filename=\"" + document.getName() + "\"");
        InputStream documentContentStream = alfrescoService.getFileContent(document);
        IOUtils.copy(documentContentStream, response.getOutputStream());
        response.flushBuffer();
    }

    /**
     * Update a file.
     *
     * @param uuid The uuid of the file.
     * @param file New file to replace the original one.
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.1/home/file/{uuid}/content", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<Object> alfrescoUpdate(@PathVariable("uuid") String uuid,
                                                 @RequestParam(value = "file", required = true) MultipartFile file) throws IOException {
        alfrescoService.updateFileContents(uuid, file.getInputStream(), file.getSize(), file.getContentType(),
                alfrescoService.getSessionForCurrentUser());
        return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
    }


    /**
     * Get contents of a folder.
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.1/home/folder/{uuid}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> alfrescoGetFolder(@PathVariable("uuid") String uuid) {
        Folder folder = alfrescoService.getFolder(uuid, alfrescoService.getSessionForCurrentUser());
        FolderRepresentation folderRepresentation = new FolderRepresentation(folder, alfrescoService.getHomeFolderForCurrentUser());
        return new ResponseEntity<Object>(folderRepresentation, HttpStatus.OK);
    }

    /**
     * Get contents of a folder.
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.1/home/folder/{uuid}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<Object> alfrescoUpdateFolder(@PathVariable("uuid") String uuid,
                                                       @RequestBody FolderRepresentation folderRepresentation) {
        Folder folder = alfrescoService.getFolder(uuid, alfrescoService.getSessionForCurrentUser());
        if (folderRepresentation.getName() != null) {
            folder.rename(folderRepresentation.getName());
        }

        return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
    }

    /**
     * Delete a folder.
     *
     * @param uuid The uuid of the folder.
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.1/home/folder/{uuid}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> alfrescoDeleteFolder(@PathVariable("uuid") String uuid) {
        Folder folder = alfrescoService.getFolder(uuid, alfrescoService.getSessionForCurrentUser());
        folder.delete();
        return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
    }

    /**
     * Create a subfolder
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.1/home/folder/{uuid}", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public ResponseEntity<String> alfrescoCreateSubfolder(@PathVariable("uuid") String uuid,
                                                          @RequestBody FolderRepresentation folderRepresentation) {
        Folder targetFolder = alfrescoService.getFolder(uuid, alfrescoService.getSessionForCurrentUser());
        Folder folder = alfrescoService.createFolder(targetFolder, folderRepresentation.getName());
        if (folder == null) {
            return new ResponseEntity<String>(HttpStatus.SERVICE_UNAVAILABLE);
        }
        return new ResponseEntity<String>("/rest/v0.1/home/folder/" + AlfrescoUtils.parseId(folder), HttpStatus.CREATED);
    }

    /**
     * Upload file to a folder.
     *
     * @param file File to be uploaded.
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.1/home/folder/{uuid}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> alfrescoUploadToFolder(@PathVariable("uuid") String uuid,
                                                         @RequestParam(value = "file", required = true) MultipartFile file) throws IOException {
        Folder targetFolder = alfrescoService.getFolder(uuid, alfrescoService.getSessionForCurrentUser());
        Document document = alfrescoService.uploadFile(targetFolder,
                file.getOriginalFilename(), file.getInputStream(), file.getSize(), file.getContentType());
        if (document == null) {
            return new ResponseEntity<String>(HttpStatus.SERVICE_UNAVAILABLE);
        }
        return new ResponseEntity<String>("/rest/v0.1/home/file/" + AlfrescoUtils.parseId(document), HttpStatus.CREATED);
    }

    /**
     * Get contents of the shared folder.
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.1/shared", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> alfrescoGetShared() {
        Folder homeFolder = alfrescoService.getSharedFolderForCurrentUser();
        FolderRepresentation homeFolderRepresentation = new FolderRepresentation(homeFolder, homeFolder);
        return new ResponseEntity<Object>(homeFolderRepresentation, HttpStatus.OK);
    }

}
