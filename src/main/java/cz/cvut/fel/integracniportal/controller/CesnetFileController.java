package cz.cvut.fel.integracniportal.controller;

import com.jcraft.jsch.SftpException;
import cz.cvut.fel.integracniportal.cesnet.CesnetService;
import cz.cvut.fel.integracniportal.cesnet.FileState;
import cz.cvut.fel.integracniportal.exceptions.FileAccessException;
import cz.cvut.fel.integracniportal.exceptions.NotFoundException;
import cz.cvut.fel.integracniportal.exceptions.ServiceAccessException;
import cz.cvut.fel.integracniportal.model.FileMetadata;
import cz.cvut.fel.integracniportal.model.Folder;
import cz.cvut.fel.integracniportal.representation.CesnetFileMetadataRepresentation;
import cz.cvut.fel.integracniportal.representation.FileMetadataRepresentation;
import cz.cvut.fel.integracniportal.representation.FolderRepresentation;
import cz.cvut.fel.integracniportal.service.FileMetadataService;
import cz.cvut.fel.integracniportal.service.FolderService;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Controller
@RequestMapping("/rest")
public class CesnetFileController extends AbstractController {

    private static final Logger logger = Logger.getLogger(CesnetFileController.class);

    @Autowired
    private CesnetService cesnetService;

    @Autowired
    private FileMetadataService fileMetadataService;

    @Autowired
    private FolderService folderService;

    /**
     * Returns list of all top level folders.
     * @return List of {@link cz.cvut.fel.integracniportal.representation.FolderRepresentation}.
     */
    @RequestMapping(value = "/v0.1/archive", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<FolderRepresentation>> cesnetGetTopLevelFolders() {
        List<FolderRepresentation> folderRepresentations = folderService.getTopLevelFolderRepresentations();
        return new ResponseEntity<List<FolderRepresentation>>(folderRepresentations, HttpStatus.OK);
    }

    /**
     * Returns a folder by its id.
     * @return A folder representation object.
     */
    @RequestMapping(value = "/v0.1/archive/folder/{folderid}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity cesnetGetFolder(@PathVariable("folderid") Long folderId) {
        try {

            FolderRepresentation folderRepresentation = folderService.getFolderRepresentationById(folderId);
            return new ResponseEntity(folderRepresentation, HttpStatus.OK);

        } catch (NotFoundException e) {
            return new ResponseEntity(resolveError(e.getErrorObject()), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Create a top level folder.
     * @param folderRepresentation    Folder representation of the folder to be created.
     * @return
     */
    @RequestMapping(value = "/v0.1/archive", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<FolderRepresentation> cesnetCreateTopLevelFolder(@RequestBody FolderRepresentation folderRepresentation) {
        Folder newFolder = folderService.createTopLevelFolder(folderRepresentation.getName());
        return new ResponseEntity<FolderRepresentation>(new FolderRepresentation(newFolder, false), HttpStatus.CREATED);
    }

    /**
     * Create a subfolder.
     * @param parentFolderId    Id of a parent folder in which the new one should be created.
     * @param folderRepresentation    Folder representation of the folder to be created.
     * @return
     */
    @RequestMapping(value = "/v0.1/archive/folder/{parentfolderid}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity cesnetCreateSubFolder(@PathVariable("parentfolderid") Long parentFolderId,
                                                @RequestBody FolderRepresentation folderRepresentation) {
        try {

            Folder newFolder = folderService.createSubFolder(folderRepresentation.getName(), parentFolderId);
            return new ResponseEntity(new FolderRepresentation(newFolder, false), HttpStatus.CREATED);

        } catch (NotFoundException e) {
            return new ResponseEntity(resolveError(e.getErrorObject()), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Update folder metadata.
     * @param folderRepresentation  A folder representation containing new metadata. Currently, only 'name' field is supported.
     * @return
     */
    @RequestMapping(value = "/v0.1/archive/folder/{folderid}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity cesnetUpdateFolder(@PathVariable("folderid") Long folderId,
                                             @RequestBody FolderRepresentation folderRepresentation) {
        try {

            folderService.updateFolder(folderId, folderRepresentation);
            return new ResponseEntity(HttpStatus.NO_CONTENT);

        } catch (NotFoundException e) {
            return new ResponseEntity(resolveError(e.getErrorObject()), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Delete a folder.
     * @param folderId  Id of the folder to be deleted.
     */
    @RequestMapping(value = "/v0.1/archive/folder/{folderid}", method = RequestMethod.DELETE)
    public ResponseEntity<String> cesnetDeleteFolder(@PathVariable("folderid") Long folderId) {
        try {

            folderService.removeFolder(folderId);
            return new ResponseEntity<String>(HttpStatus.NO_CONTENT);

        } catch (NotFoundException e) {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        } catch (ServiceAccessException e) {
            return new ResponseEntity<String>(resolveError(e.getErrorObject()), HttpStatus.SERVICE_UNAVAILABLE);
        }
    }



    /**
     * Return metadata of a file.
     * @param fileuuid    The uuid identifier of the file.
     * @return File metadata.
     */
    @RequestMapping(value = "/v0.1/archive/file/{fileuuid}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity cesnetGetFileState(@PathVariable("fileuuid") String fileuuid) {
        try {

            CesnetFileMetadataRepresentation fileMetadataResource = fileMetadataService.getFileMetadataResource(fileuuid);
            return new ResponseEntity(fileMetadataResource, HttpStatus.OK);

        } catch (ServiceAccessException e) {
            return new ResponseEntity(resolveError(e.getErrorObject()), HttpStatus.SERVICE_UNAVAILABLE);
        } catch (FileAccessException e) {
            return new ResponseEntity(resolveError(e.getErrorObject()), HttpStatus.SERVICE_UNAVAILABLE);
        } catch (FileNotFoundException e) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Updates metadata for file
     * @param fileuuid                      The uuid identifier of the file.
     * @param fileMetadataRepresentation    New metadata, see {@link cz.cvut.fel.integracniportal.representation.CesnetFileMetadataRepresentation} for the list of fields.
     *                                      The only accepted values for 'state' field are OFL and REG for archiving/restoring a file.
     * @return
     */
    @RequestMapping(value = "/v0.1/archive/file/{fileuuid}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity cesnetSetFileState(@PathVariable("fileuuid") String fileuuid,
                                             @RequestBody CesnetFileMetadataRepresentation fileMetadataRepresentation) {
        FileMetadata fileMetadata = null;
        try {
            fileMetadata = fileMetadataService.getFileMetadataByUuid(fileuuid);
        } catch (FileNotFoundException e) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        FileState newFileState = fileMetadataRepresentation.getState();
        if (newFileState != null) {
            try {
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
            } catch (ServiceAccessException e) {
                return new ResponseEntity(resolveError(e.getErrorObject()), HttpStatus.SERVICE_UNAVAILABLE);
            } catch (FileNotFoundException e) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
        }

        fileMetadata.setFilename(fileMetadataRepresentation.getFilename());
        fileMetadata.setMimetype(fileMetadataRepresentation.getMimetype());
        fileMetadata.setArchiveOn(fileMetadataRepresentation.getArchiveOn());
        fileMetadata.setDeleteOn(fileMetadataRepresentation.getDeleteOn());
        fileMetadataService.updateFileMetadata(fileMetadata);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
     * Download a file.
     * @param fileuuid    The uuid identifier of the file.
     */
    @RequestMapping(value = "/v0.1/archive/file/{fileuuid}/content", method = RequestMethod.GET)
    public void cesnetGet(HttpServletResponse response, @PathVariable("fileuuid") String fileuuid) {
        try {

            CesnetFileMetadataRepresentation fileMetadataResource = fileMetadataService.getFileMetadataResource(fileuuid);
            response.setContentType(fileMetadataResource.getMimetype());
            response.setHeader("Content-Disposition", "attachment; filename=\""+fileMetadataResource.getFilename()+"\"");
            InputStream remoteFileInputStream = cesnetService.getFile(fileuuid);
            IOUtils.copy(remoteFileInputStream, response.getOutputStream());
            response.flushBuffer();

        } catch (ServiceAccessException e) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        } catch (FileAccessException e) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        } catch (FileNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (SftpException e) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        }
    }

    /**
     * Update a file.
     * @param fileuuid    The uuid identifier of the file.
     * @param file        New file to replace the original one.
     * @return
     */
    @RequestMapping(value = "/v0.1/archive/file/{fileuuid}/content", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<String> cesnetUpdate(@PathVariable("fileuuid") String fileuuid, @RequestParam(value = "file", required = true) MultipartFile file) {
        try {

            fileMetadataService.updateFile(fileuuid, file);
            return new ResponseEntity<String>(HttpStatus.NO_CONTENT);

        } catch (FileNotFoundException e) {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        } catch (ServiceAccessException e) {
            return new ResponseEntity<String>(resolveError(e.getErrorObject()), HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    /**
     * Delete a file.
     * @param fileuuid    The uuid identifier of the file.
     */
    @RequestMapping(value = "/v0.1/archive/file/{fileuuid}", method = RequestMethod.DELETE)
    public ResponseEntity<String> cesnetDelete(@PathVariable("fileuuid") String fileuuid) {
        try {
            fileMetadataService.deleteFile(fileuuid);
            return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
        } catch (ServiceAccessException e) {
            return new ResponseEntity<String>(resolveError(e.getErrorObject()), HttpStatus.SERVICE_UNAVAILABLE);
        } catch (FileNotFoundException e) {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Upload a file.
     * @param file    File to be uploaded.
     * @return
     */
    @RequestMapping(value = "/v0.1/archive/folder/{folderid}/files", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity cesnetUploadFile(@PathVariable("folderid") Long folderId, @RequestParam(value = "file", required = true) MultipartFile file) {
        try {

            FileMetadata fileMetadata = fileMetadataService.uploadFile(folderId, file);
            return new ResponseEntity(new FileMetadataRepresentation(fileMetadata), HttpStatus.CREATED);

        } catch (NotFoundException e) {
            return new ResponseEntity(resolveError(e.getErrorObject()), HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        } catch (ServiceAccessException e) {
            return new ResponseEntity(resolveError(e.getErrorObject()), HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

}
