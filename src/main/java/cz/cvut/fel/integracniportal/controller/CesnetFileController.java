package cz.cvut.fel.integracniportal.controller;

import com.jcraft.jsch.SftpException;
import cz.cvut.fel.integracniportal.cesnet.CesnetService;
import cz.cvut.fel.integracniportal.cesnet.FileState;
import cz.cvut.fel.integracniportal.exceptions.FileAccessException;
import cz.cvut.fel.integracniportal.exceptions.FolderNotFoundException;
import cz.cvut.fel.integracniportal.exceptions.ServiceAccessException;
import cz.cvut.fel.integracniportal.model.FileMetadata;
import cz.cvut.fel.integracniportal.model.Folder;
import cz.cvut.fel.integracniportal.representation.CesnetFileMetadataRepresentation;
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
public class CesnetFileController {

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
    public ResponseEntity<FolderRepresentation> cesnetGetFolder(@PathVariable("folderid") Long folderId) {
        FolderRepresentation folderRepresentation = folderService.getFolderRepresentationById(folderId);
        if (folderRepresentation == null) {
            return new ResponseEntity<FolderRepresentation>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<FolderRepresentation>(folderRepresentation, HttpStatus.OK);
    }

    /**
     * Create a top level folder.
     * @param folderRepresentation    Folder representation of the folder to be created.
     * @return
     */
    @RequestMapping(value = "/v0.1/archive", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> cesnetCreateTopLevelFolder(@RequestBody FolderRepresentation folderRepresentation) {

        Folder newFolder = folderService.createFolder(folderRepresentation.getName(), null);
        return new ResponseEntity<String>("/rest/v0.1/archive/folder/"+newFolder.getFolderId(), HttpStatus.CREATED);
    }

    /**
     * Create a subfolder.
     * @param parentFolderId    Id of a parent folder in which the new one should be created.
     * @param folderRepresentation    Folder representation of the folder to be created.
     * @return
     */
    @RequestMapping(value = "/v0.1/archive/folder/{parentfolderid}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> cesnetCreateSubFolder(@PathVariable("parentfolderid") Long parentFolderId,
                                                        @RequestBody FolderRepresentation folderRepresentation) {

        Folder parentFolder = folderService.getFolderById(parentFolderId);
        if (parentFolder == null) {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }

        Folder newFolder = folderService.createFolder(folderRepresentation.getName(), parentFolder);
        return new ResponseEntity<String>("/rest/v0.1/archive/folder/"+newFolder.getFolderId(), HttpStatus.CREATED);
    }

    /**
     * Update folder metadata.
     * @param folderRepresentation  A folder representation containing new metadata. Currently, only 'name' field is supported.
     * @return
     */
    @RequestMapping(value = "/v0.1/archive/folder/{folderid}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<FolderRepresentation> cesnetUpdateFolder(@PathVariable("folderid") Long folderId,
                                                                   @RequestBody FolderRepresentation folderRepresentation) {

        Folder folder = folderService.getFolderById(folderId);
        if (folder == null) {
            return new ResponseEntity<FolderRepresentation>(HttpStatus.NOT_FOUND);
        }

        folder.setName(folderRepresentation.getName());
        folderService.updateFolder(folder);

        return new ResponseEntity<FolderRepresentation>(HttpStatus.NO_CONTENT);
    }

    /**
     * Delete a folder.
     * @param folderId  Id of the folder to be deleted.
     */
    @RequestMapping(value = "/v0.1/archive/folder/{folderid}", method = RequestMethod.DELETE)
    public ResponseEntity<String> cesnetDeleteFolder(@PathVariable("folderid") Long folderId) {
        Folder folder = folderService.getFolderById(folderId);
        if (folder == null) {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }

        folderService.removeFolder(folder);
        return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
    }



    /**
     * Return metadata of a file.
     * @param fileuuid    The uuid identifier of the file.
     * @return File metadata.
     */
    @RequestMapping(value = "/v0.1/archive/file/{fileuuid}/metadata", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<CesnetFileMetadataRepresentation> cesnetGetFileState(@PathVariable("fileuuid") String fileuuid) {
        try {

            CesnetFileMetadataRepresentation fileMetadataResource = fileMetadataService.getFileMetadataResource(fileuuid);
            return new ResponseEntity<CesnetFileMetadataRepresentation>(fileMetadataResource, HttpStatus.OK);

        } catch (ServiceAccessException e) {
            return new ResponseEntity<CesnetFileMetadataRepresentation>(HttpStatus.SERVICE_UNAVAILABLE);
        } catch (FileAccessException e) {
            return new ResponseEntity<CesnetFileMetadataRepresentation>(HttpStatus.SERVICE_UNAVAILABLE);
        } catch (FileNotFoundException e) {
            return new ResponseEntity<CesnetFileMetadataRepresentation>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Updates metadata for file
     * @param fileuuid                      The uuid identifier of the file.
     * @param fileMetadataRepresentation    New metadata, see {@link cz.cvut.fel.integracniportal.representation.CesnetFileMetadataRepresentation} for the list of fields.
     *                                      The only accepted values for 'state' field are OFL and REG for archiving/restoring a file.
     * @return
     */
    @RequestMapping(value = "/v0.1/archive/file/{fileuuid}/metadata", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<CesnetFileMetadataRepresentation> cesnetSetFileState(@PathVariable("fileuuid") String fileuuid,
                                                                   @RequestBody CesnetFileMetadataRepresentation fileMetadataRepresentation) {
        FileMetadata fileMetadata = null;
        try {
            fileMetadata = fileMetadataService.getFileMetadataByUuid(fileuuid);
        } catch (FileNotFoundException e) {
            return new ResponseEntity<CesnetFileMetadataRepresentation>(HttpStatus.NOT_FOUND);
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
                        return new ResponseEntity<CesnetFileMetadataRepresentation>(HttpStatus.BAD_REQUEST);
                }
            } catch (ServiceAccessException e) {
                return new ResponseEntity<CesnetFileMetadataRepresentation>(HttpStatus.SERVICE_UNAVAILABLE);
            } catch (FileNotFoundException e) {
                return new ResponseEntity<CesnetFileMetadataRepresentation>(HttpStatus.NOT_FOUND);
            }
        }

        fileMetadata.setFilename(fileMetadataRepresentation.getFilename());
        fileMetadata.setMimetype(fileMetadataRepresentation.getMimetype());
        fileMetadata.setArchiveOn(fileMetadataRepresentation.getArchiveOn());
        fileMetadata.setDeleteOn(fileMetadataRepresentation.getDeleteOn());
        fileMetadataService.updateFileMetadata(fileMetadata);
        return new ResponseEntity<CesnetFileMetadataRepresentation>(HttpStatus.NO_CONTENT);
    }

    /**
     * Download a file.
     * @param fileuuid    The uuid identifier of the file.
     */
    @RequestMapping(value = "/v0.1/archive/file/{fileuuid}", method = RequestMethod.GET)
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
    @RequestMapping(value = "/v0.1/archive/file/{fileuuid}", method = RequestMethod.PUT)
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
            return new ResponseEntity<String>(HttpStatus.SERVICE_UNAVAILABLE);
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
            return new ResponseEntity<String>(HttpStatus.SERVICE_UNAVAILABLE);
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
    public ResponseEntity<String> cesnetUploadFile(@PathVariable("folderid") Long folderId, @RequestParam(value = "file", required = true) MultipartFile file) {
        try {

            FileMetadata fileMetadata = fileMetadataService.uploadFile(folderId, file);
            return new ResponseEntity<String>("/rest/v0.1/archive/file/"+fileMetadata.getUuid(), HttpStatus.CREATED);

        } catch (FolderNotFoundException e) {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        } catch (ServiceAccessException e) {
            return new ResponseEntity<String>(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

}
