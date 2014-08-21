package cz.cvut.fel.integracniportal.controller;

import com.jcraft.jsch.SftpException;
import cz.cvut.fel.integracniportal.cesnet.CesnetService;
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

    @RequestMapping(value = "ssh/ls", method = RequestMethod.GET)
    @ResponseBody
    public List<String> cesnetLs() {
        return cesnetService.getFileList();
    }

    @RequestMapping(value = "ssh/{filename:[a-zA-Z0-9\\._-]+}", method = RequestMethod.GET)
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

    @RequestMapping(value = "ssh/upload", method = RequestMethod.POST)
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
