package cz.cvut.fel.integracniportal.controller;

import com.jcraft.jsch.SftpException;
import cz.cvut.fel.integracniportal.cesnet.CesnetService;
import cz.cvut.fel.integracniportal.cesnet.FileAccessException;
import cz.cvut.fel.integracniportal.model.UserDetails;
import cz.cvut.fel.integracniportal.resource.UserDetailsResource;
import cz.cvut.fel.integracniportal.service.UserDetailsService;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import resourceitems.CesnetFileMetadata;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Controller
@RequestMapping("/rest")
public class FileController {

    private static final Logger logger = Logger.getLogger(FileController.class);

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    CesnetService cesnetService;

    @RequestMapping(value = "files", method = RequestMethod.GET)
    @ResponseBody
    public List<CesnetFileMetadata> cesnetLs() {
        return cesnetService.getFileList();
    }

    @RequestMapping(value = "files/{filename:[a-zA-Z0-9\\._-]+}/metadata", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<CesnetFileMetadata> cesnetGetFileState(@PathVariable("filename") String filename) {
        CesnetFileMetadata fileMetadata = cesnetService.getFileMetadata(filename);
        if (fileMetadata == null) {
            return new ResponseEntity<CesnetFileMetadata>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<CesnetFileMetadata>(fileMetadata, HttpStatus.OK);
    }

    @RequestMapping(value = "files/{filename:[a-zA-Z0-9\\._-]+}/archive", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<String> cesnetMoveFileOffline(@PathVariable("filename") String filename) {
        try {
            cesnetService.moveFileOffline(filename);
            return new ResponseEntity<String>(HttpStatus.OK);
        } catch (FileAccessException fae) {
            return new ResponseEntity<String>(fae.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "files/{filename:[a-zA-Z0-9\\._-]+}/restore", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<String> cesnetMoveFileOnline(@PathVariable("filename") String filename) {
        try {
            cesnetService.moveFileOnline(filename);
            return new ResponseEntity<String>(HttpStatus.OK);
        } catch (FileAccessException fae) {
            return new ResponseEntity<String>(fae.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "files/{filename:[a-zA-Z0-9\\._-]+}", method = RequestMethod.GET)
    public void cesnetGet(HttpServletResponse response, @PathVariable("filename") String filename) {
        MimetypesFileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap();
        String mimeType = mimetypesFileTypeMap.getContentType(filename);

        try {
            InputStream remoteFileInputStream = cesnetService.getFile(filename);
            IOUtils.copy(remoteFileInputStream, response.getOutputStream());
            response.setContentType(mimeType);
            response.flushBuffer();
        } catch (SftpException e) {
            logger.error("Unable to read the remote file '" + filename + "'.");
        } catch (IOException e) {
            logger.error("Unable to write remote file '" + filename + "' into response output stream.");
        }
    }

    @RequestMapping(value = "files", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> cesnetUpload(@RequestParam(value = "file", required = true) MultipartFile file) {
        String filename = file.getOriginalFilename();
        try {
            cesnetService.uploadFile(file.getInputStream(), filename);
            return new ResponseEntity<String>("/rest/ssh/"+filename, HttpStatus.CREATED);
        } catch (SftpException e) {
            return new ResponseEntity<String>(HttpStatus.SERVICE_UNAVAILABLE);
        } catch (IOException e) {
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
    }
}
