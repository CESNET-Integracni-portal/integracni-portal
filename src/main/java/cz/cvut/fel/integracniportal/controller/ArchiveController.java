package cz.cvut.fel.integracniportal.controller;

import cz.cvut.fel.integracniportal.cesnet.CesnetService;
import cz.cvut.fel.integracniportal.cesnet.FileState;
import cz.cvut.fel.integracniportal.model.FileMetadata;
import cz.cvut.fel.integracniportal.model.Folder;
import cz.cvut.fel.integracniportal.representation.CesnetFileMetadataRepresentation;
import cz.cvut.fel.integracniportal.representation.FileMetadataRepresentation;
import cz.cvut.fel.integracniportal.representation.FolderRepresentation;
import cz.cvut.fel.integracniportal.service.ArchiveFileMetadataService;
import cz.cvut.fel.integracniportal.service.ArchiveFolderService;
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
import java.util.List;

@Controller
@RequestMapping("/rest")
public class ArchiveController extends AbstractController {

    private static final Logger logger = Logger.getLogger(ArchiveController.class);

    @Autowired
    private CesnetService cesnetService;

    @Autowired
    private ArchiveFileMetadataService archiveFileMetadataService;

    @Autowired
    private ArchiveFolderService archiveFolderService;

    /**
     * Returns list of all top level folders.
     *
     * @return List of {@link cz.cvut.fel.integracniportal.representation.FolderRepresentation}.
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.1/archive", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<FolderRepresentation>> cesnetGetTopLevelFolders() {
        List<FolderRepresentation> folderRepresentations = archiveFolderService.getTopLevelFolderRepresentations();
        return new ResponseEntity<List<FolderRepresentation>>(folderRepresentations, HttpStatus.OK);
    }

    /**
     * Returns a folder by its id.
     *
     * @return A folder representation object.
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.1/archive/folder/{folderid}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity cesnetGetFolder(@PathVariable("folderid") Long folderId) {
        FolderRepresentation folderRepresentation = archiveFolderService.getFolderRepresentationById(folderId);
        return new ResponseEntity(folderRepresentation, HttpStatus.OK);
    }

    /**
     * Create a top level folder.
     *
     * @param folderRepresentation Folder representation of the folder to be created.
     * @return
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.1/archive", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public ResponseEntity<FolderRepresentation> cesnetCreateTopLevelFolder(@RequestBody FolderRepresentation folderRepresentation) {
        Folder newFolder = archiveFolderService.createTopLevelFolder(folderRepresentation.getName());
        return new ResponseEntity<FolderRepresentation>(new FolderRepresentation(newFolder, false), HttpStatus.CREATED);
    }

    /**
     * Create a subfolder.
     *
     * @param parentFolderId       Id of a parent folder in which the new one should be created.
     * @param folderRepresentation Folder representation of the folder to be created.
     * @return
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.1/archive/folder/{parentfolderid}", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public ResponseEntity cesnetCreateSubFolder(@PathVariable("parentfolderid") Long parentFolderId,
                                                @RequestBody FolderRepresentation folderRepresentation) {
        Folder newFolder = archiveFolderService.createSubFolder(folderRepresentation.getName(), parentFolderId);
        return new ResponseEntity(new FolderRepresentation(newFolder, false), HttpStatus.CREATED);
    }

    /**
     * Update folder metadata.
     *
     * @param folderRepresentation A folder representation containing new metadata. Currently, only 'name' field is supported.
     * @return
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.1/archive/folder/{folderid}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity cesnetUpdateFolder(@PathVariable("folderid") Long folderId,
                                             @RequestBody FolderRepresentation folderRepresentation) {
        archiveFolderService.updateFolder(folderId, folderRepresentation);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
     * Delete a folder.
     *
     * @param folderId Id of the folder to be deleted.
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.1/archive/folder/{folderid}", method = RequestMethod.DELETE)
    public ResponseEntity<String> cesnetDeleteFolder(@PathVariable("folderid") Long folderId) {
        archiveFolderService.removeFolder(folderId);
        return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
    }


    /**
     * Return metadata of a file.
     *
     * @param fileuuid The uuid identifier of the file.
     * @return File metadata.
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.1/archive/file/{fileuuid}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity cesnetGetFileState(@PathVariable("fileuuid") String fileuuid) {
        CesnetFileMetadataRepresentation fileMetadataResource = archiveFileMetadataService.getFileMetadataResource(fileuuid);
        return new ResponseEntity(fileMetadataResource, HttpStatus.OK);
    }

    /**
     * Updates metadata for file
     *
     * @param fileuuid                   The uuid identifier of the file.
     * @param fileMetadataRepresentation New metadata, see {@link cz.cvut.fel.integracniportal.representation.CesnetFileMetadataRepresentation} for the list of fields.
     *                                   The only accepted values for 'state' field are OFL and REG for archiving/restoring a file.
     * @return
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.1/archive/file/{fileuuid}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity cesnetSetFileState(@PathVariable("fileuuid") String fileuuid,
                                             @RequestBody CesnetFileMetadataRepresentation fileMetadataRepresentation) {

        FileMetadata fileMetadata = archiveFileMetadataService.getFileMetadataByUuid(fileuuid);
        FileState newFileState = fileMetadataRepresentation.getState();

        if (newFileState != null) {
            switch (newFileState) {
                case REG:
                    cesnetService.moveFileOnline(fileuuid);
                    break;
                case OFL:
                    cesnetService.moveFileOffline(fileuuid);
                    break;
                default:
                    return new ResponseEntity(HttpStatus.BAD_REQUEST);
            }
        }

        if (fileMetadataRepresentation.getFilename() != null) {
            fileMetadata.setFilename(fileMetadataRepresentation.getFilename());
        }
        if (fileMetadataRepresentation.getMimetype() != null) {
            fileMetadata.setMimetype(fileMetadataRepresentation.getMimetype());
        }
        if (fileMetadataRepresentation.getArchiveOn() != null) {
            fileMetadata.setArchiveOn(fileMetadataRepresentation.getArchiveOn());
        }
        if (fileMetadataRepresentation.getDeleteOn() != null) {
            fileMetadata.setDeleteOn(fileMetadataRepresentation.getDeleteOn());
        }
        archiveFileMetadataService.updateFileMetadata(fileMetadata);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
     * Download a file.
     *
     * @param fileuuid The uuid identifier of the file.
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.1/archive/file/{fileuuid}/content", method = RequestMethod.GET)
    public void cesnetGet(HttpServletResponse response, @PathVariable("fileuuid") String fileuuid) throws IOException {
        CesnetFileMetadataRepresentation fileMetadataResource = archiveFileMetadataService.getFileMetadataResource(fileuuid);
        response.setContentType(fileMetadataResource.getMimetype());
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileMetadataResource.getFilename() + "\"");
        InputStream remoteFileInputStream = cesnetService.getFile(fileuuid);
        IOUtils.copy(remoteFileInputStream, response.getOutputStream());
        response.flushBuffer();
    }

    /**
     * Update a file.
     *
     * @param fileuuid The uuid identifier of the file.
     * @param file     New file to replace the original one.
     * @return
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.1/archive/file/{fileuuid}/content", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<String> cesnetUpdate(@PathVariable("fileuuid") String fileuuid, @RequestParam(value = "file", required = true) MultipartFile file) {
        archiveFileMetadataService.updateFile(fileuuid, file);
        return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
    }

    /**
     * Delete a file.
     *
     * @param fileuuid The uuid identifier of the file.
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.1/archive/file/{fileuuid}", method = RequestMethod.DELETE)
    public ResponseEntity<String> cesnetDelete(@PathVariable("fileuuid") String fileuuid) {
        archiveFileMetadataService.deleteFile(fileuuid);
        return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
    }

    /**
     * Upload a file.
     *
     * @param file File to be uploaded.
     * @return
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.1/archive/folder/{folderid}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity cesnetUploadFile(@PathVariable("folderid") Long folderId, @RequestParam(value = "file", required = true) MultipartFile file) {
        FileMetadata fileMetadata = archiveFileMetadataService.uploadFileToFolder(folderId, file);
        return new ResponseEntity(new FileMetadataRepresentation(fileMetadata), HttpStatus.CREATED);
    }

}
