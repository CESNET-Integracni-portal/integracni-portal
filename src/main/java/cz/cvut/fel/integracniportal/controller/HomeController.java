package cz.cvut.fel.integracniportal.controller;

import com.jcraft.jsch.SftpException;
import cz.cvut.fel.integracniportal.cesnet.CesnetService;
import cz.cvut.fel.integracniportal.model.UserDetails;
import cz.cvut.fel.integracniportal.resource.UserDetailsResource;
import cz.cvut.fel.integracniportal.service.UserDetailsService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.apache.commons.io.IOUtils;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Controller
@RequestMapping("/rest")
public class HomeController {

    private static final Logger logger = Logger.getLogger(HomeController.class);

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    CesnetService cesnetService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<String> test() {
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    @RequestMapping(value = "user/{username}", method = RequestMethod.GET)
    @ResponseBody
    public UserDetailsResource user(@PathVariable("username") String username) {
        UserDetails userDetails = userDetailsService.getUserByUsername(username);
        if (userDetails != null) {
            UserDetailsResource userDetailsResource = new UserDetailsResource(userDetails);
            return userDetailsResource;
        }
        return null;
    }

    @RequestMapping(value = "secure/user/{username}", method = RequestMethod.GET)
    @ResponseBody
    @Secured("ROLE_ADMIN")
    public UserDetailsResource secure(@PathVariable("username") String username) {
        UserDetails userDetails = userDetailsService.getUserByUsername(username);
        if (userDetails != null) {
            UserDetailsResource userDetailsResource = new UserDetailsResource(userDetails);
            return userDetailsResource;
        }
        return null;
    }

    @RequestMapping(value = "ssh/ls", method = RequestMethod.GET)
    @ResponseBody
    public List<String> cesnetLs() {
        return cesnetService.getFileList();
    }

    @RequestMapping(value = "ssh/get", method = RequestMethod.GET)
    public void cesnetGet(HttpServletResponse response) {
        String filename = "VO_storage-cache_tape/laser-game.jpg";
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
}
