package cz.cvut.fel.integracniportal.controller;

import com.jcraft.jsch.SftpException;
import cz.cvut.fel.integracniportal.cesnet.CesnetService;
import cz.cvut.fel.integracniportal.cesnet.FileState;
import cz.cvut.fel.integracniportal.exceptions.FileAccessException;
import cz.cvut.fel.integracniportal.exceptions.ServiceAccessException;
import cz.cvut.fel.integracniportal.model.FileMetadata;
import cz.cvut.fel.integracniportal.resource.CesnetFileMetadataResource;
import cz.cvut.fel.integracniportal.service.FileMetadataService;
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

    /**
     * Returns list of all files.
     * @return List of {@link cz.cvut.fel.integracniportal.resource.CesnetFileMetadataResource} metadata.
     */
    @RequestMapping(value = "/v0.1/files", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<CesnetFileMetadataResource>> cesnetLs() {
        try {

            List<CesnetFileMetadataResource> fileMetadataResources = fileMetadataService.getFileMetadataResources();
            return new ResponseEntity<List<CesnetFileMetadataResource>>(fileMetadataResources, HttpStatus.OK);

        } catch (ServiceAccessException e) {
            return new ResponseEntity<List<CesnetFileMetadataResource>>(HttpStatus.SERVICE_UNAVAILABLE);
        } catch (FileAccessException e) {
            return new ResponseEntity<List<CesnetFileMetadataResource>>(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    /**
     * Returns list of all files in a certain state.
     * @param fileState State by which the files will be filtered.
     * @return List of {@link cz.cvut.fel.integracniportal.resource.CesnetFileMetadataResource} metadata.
     */
    @RequestMapping(value = "/v0.1/files/{filestate}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<CesnetFileMetadataResource>> cesnetLsByState(@PathVariable("filestate") FileState fileState) {
        try {

            List<CesnetFileMetadataResource> fileMetadataResources = fileMetadataService.getFileMetadataResources();
            return new ResponseEntity<List<CesnetFileMetadataResource>>(fileMetadataResources, HttpStatus.OK);

        } catch (ServiceAccessException e) {
            return new ResponseEntity<List<CesnetFileMetadataResource>>(HttpStatus.SERVICE_UNAVAILABLE);
        } catch (FileAccessException e) {
            return new ResponseEntity<List<CesnetFileMetadataResource>>(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    /**
     * Return metadata of a file.
     * @param fileuuid    The uuid identifier of the file.
     * @return File metadata.
     */
    @RequestMapping(value = "/v0.1/file/{fileuuid}/metadata", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<CesnetFileMetadataResource> cesnetGetFileState(@PathVariable("fileuuid") String fileuuid) {
        try {

            CesnetFileMetadataResource fileMetadataResource = fileMetadataService.getFileMetadataResource(fileuuid);
            return new ResponseEntity<CesnetFileMetadataResource>(fileMetadataResource, HttpStatus.OK);

        } catch (ServiceAccessException e) {
            return new ResponseEntity<CesnetFileMetadataResource>(HttpStatus.SERVICE_UNAVAILABLE);
        } catch (FileAccessException e) {
            return new ResponseEntity<CesnetFileMetadataResource>(HttpStatus.SERVICE_UNAVAILABLE);
        } catch (FileNotFoundException e) {
            return new ResponseEntity<CesnetFileMetadataResource>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Updates metadata for file
     * @param fileuuid                The uuid identifier of the file.
     * @param fileMetadataResource    New metadata, see {@link cz.cvut.fel.integracniportal.resource.CesnetFileMetadataResource} for the list of fields.
     *                                The only accepted values for 'state' field are OFL and REG for archiving/restoring a file.
     * @return
     */
    @RequestMapping(value = "/v0.1/file/{fileuuid}/metadata", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<CesnetFileMetadataResource> cesnetSetFileState(@PathVariable("fileuuid") String fileuuid,
                                                                   @RequestBody CesnetFileMetadataResource fileMetadataResource) {
        FileMetadata fileMetadata = null;
        try {
            fileMetadata = fileMetadataService.getFileMetadataByUuid(fileuuid);
        } catch (FileNotFoundException e) {
            return new ResponseEntity<CesnetFileMetadataResource>(HttpStatus.NOT_FOUND);
        }

        FileState newFileState = fileMetadataResource.getState();
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
                        return new ResponseEntity<CesnetFileMetadataResource>(HttpStatus.BAD_REQUEST);
                }
            } catch (ServiceAccessException e) {
                return new ResponseEntity<CesnetFileMetadataResource>(HttpStatus.SERVICE_UNAVAILABLE);
            } catch (FileNotFoundException e) {
                return new ResponseEntity<CesnetFileMetadataResource>(HttpStatus.NOT_FOUND);
            }
        }

        fileMetadata.setFilename(fileMetadataResource.getFilename());
        fileMetadata.setMimetype(fileMetadataResource.getMimetype());
        fileMetadata.setArchiveOn(fileMetadataResource.getArchiveOn());
        fileMetadata.setDeleteOn(fileMetadataResource.getDeleteOn());
        fileMetadataService.updateFileMetadata(fileMetadata);
        return new ResponseEntity<CesnetFileMetadataResource>(HttpStatus.NO_CONTENT);
    }

    /**
     * Download a file.
     * @param fileuuid    The uuid identifier of the file.
     */
    @RequestMapping(value = "/v0.1/file/{fileuuid}", method = RequestMethod.GET)
    public void cesnetGet(HttpServletResponse response, @PathVariable("fileuuid") String fileuuid) {
        try {

            CesnetFileMetadataResource fileMetadataResource = fileMetadataService.getFileMetadataResource(fileuuid);
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
    @RequestMapping(value = "/v0.1/file/{fileuuid}", method = RequestMethod.PUT)
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
    @RequestMapping(value = "/v0.1/file/{fileuuid}", method = RequestMethod.DELETE)
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
    @RequestMapping(value = "/v0.1/files", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> cesnetUpload(@RequestParam(value = "file", required = true) MultipartFile file) {
        try {

            String uuid = fileMetadataService.uploadFile(file);
            return new ResponseEntity<String>("/rest/v0.1/file/"+uuid, HttpStatus.CREATED);

        } catch (IOException e) {
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        } catch (ServiceAccessException e) {
            return new ResponseEntity<String>(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
}
