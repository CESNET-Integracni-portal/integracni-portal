package cz.cvut.fel.integracniportal.controller;

import cz.cvut.fel.integracniportal.model.FileMetadata;
import cz.cvut.fel.integracniportal.representation.*;
import cz.cvut.fel.integracniportal.service.FileMetadataService;
import cz.cvut.fel.integracniportal.service.LabelService;
import cz.cvut.fel.integracniportal.service.SpaceService;
import cz.cvut.fel.integracniportal.service.UserDetailsService;
import org.apache.commons.io.IOUtils;
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

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author Radek Jezdik
 */
@Controller
@RequestMapping("/rest")
public class FileController extends AbstractController {

    @Autowired
    private UserDetailsService userService;


    @Autowired
    private SpaceService spaceService;

    @Autowired
    private FileMetadataService fileMetadataService;

    @Autowired
    private LabelService labelService;

    /**
     * Returns a file metadata by its id.
     *
     * @return A file representation object.
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/space/{spaceId}/file/{fileId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getFileMetadata(@PathVariable String spaceId,
                                          @PathVariable String fileId) {
        ensureSpace(spaceId);
        FileMetadataRepresentation representation = fileMetadataService.getFileMetadataRepresentationByUuid(fileId);
        return new ResponseEntity(representation, HttpStatus.OK);
    }

    /**
     * Renames the file.
     *
     * @param representation representation with new file name
     * @return
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/space/{spaceId}/file/{fileId}/nameChange", method = POST)
    @ResponseBody
    public ResponseEntity renameFile(@PathVariable String spaceId,
                                     @PathVariable String fileId,
                                     @RequestBody NameRepresentation representation) {
        ensureSpace(spaceId);
        fileMetadataService.renameFile(fileId, representation.getName());
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
     * Moves the file to different folder.
     *
     * @param representation representation with new parent folder ID.
     * @return
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/space/{spaceId}/file/{fileId}/parentChange", method = POST)
    @ResponseBody
    public ResponseEntity moveFile(@PathVariable String spaceId,
                                   @PathVariable String fileId,
                                   @RequestBody FolderParentRepresentation representation) {
        ensureSpace(spaceId);
        fileMetadataService.moveFile(fileId, representation.getParentId());
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
     * Moves the file to bin.
     *
     * @param fileId the ID of the file to be move to trash
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/space/{spaceId}/file/{fileId}/trash", method = POST)
    public ResponseEntity moveFileToBin(@PathVariable String spaceId,
                                        @PathVariable String fileId) {
        ensureSpace(spaceId);
        fileMetadataService.deleteFile(fileId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
     * Moves the file to online state.
     *
     * @param fileId the ID of the file to be move online
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/space/{spaceId}/file/{fileId}/online", method = POST)
    public ResponseEntity moveToOnline(@PathVariable String spaceId,
                                       @PathVariable String fileId) {
        ensureSpace(spaceId);
        fileMetadataService.moveFileOnline(fileId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
     * Moves the file to offline state.
     *
     * @param fileId the ID of the file to be move offline
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/space/{spaceId}/file/{fileId}/offline", method = POST)
    public ResponseEntity moveToOffline(@PathVariable String spaceId,
                                        @PathVariable String fileId) {
        ensureSpace(spaceId);
        fileMetadataService.moveFileOffline(fileId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
     * Adds a user label to the file
     *
     * @param fileId the ID of the file to add the label to
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/space/{spaceId}/file/{fileId}/addLabel", method = POST)
    public ResponseEntity addLabel(@PathVariable String spaceId,
                                   @PathVariable String fileId,
                                   @RequestBody LabelIdRepresentation representation) {
        ensureSpace(spaceId);
        labelService.addLabelToFile(fileId, representation);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
     * Removes a user label from the file
     *
     * @param fileId the ID of the file to remove the label from
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/space/{spaceId}/file/{fileId}/removeLabel", method = POST)
    public ResponseEntity removeLabel(@PathVariable String spaceId,
                                      @PathVariable String fileId,
                                      @RequestBody LabelIdRepresentation representation) {
        ensureSpace(spaceId);
        labelService.removeLabelFromFile(fileId, representation);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
     * Marks the file as favorite for user.
     *
     * @param fileId the ID of the file to favorite
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/space/{spaceId}/file/{fileId}/favorite", method = POST)
    public ResponseEntity favoriteFile(@PathVariable String spaceId,
                                       @PathVariable String fileId) {
        ensureSpace(spaceId);
        fileMetadataService.favoriteFile(fileId, userService.getCurrentUser());
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
     * Unmarks the file as favorite for user.
     *
     * @param fileId the ID of the file to unfavorite
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/space/{spaceId}/file/{fileId}/unfavorite", method = POST)
    public ResponseEntity unfavoriteFile(@PathVariable String spaceId,
                                         @PathVariable String fileId) {
        ensureSpace(spaceId);
        fileMetadataService.unfavoriteFile(fileId, userService.getCurrentUser());
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
     * Shares the file with given list of users.
     *
     * @param fileId         the ID of file to share
     * @param representation the representation with users to share the file with
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/space/{spaceId}/file/{fileId}/share", method = POST)
    public ResponseEntity shareFile(@PathVariable String spaceId,
                                    @PathVariable String fileId,
                                    @RequestBody ShareRepresentation representation) {
        ensureSpace(spaceId);
        fileMetadataService.shareFile(fileId, representation.getShareWith(), userService.getCurrentUser());
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
     * Returns file contents.
     *
     * @param fileId the file id to get
     * @return
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/space/{spaceId}/file/{fileId}/contents", method = GET)
    @ResponseBody
    public void getFileContents(@PathVariable String spaceId,
                                @PathVariable String fileId,
                                HttpServletResponse response) throws IOException {
        ensureSpace(spaceId);

        FileMetadata fileMetadata = fileMetadataService.getFileMetadataByUuid(fileId);
        InputStream fileStream = fileMetadataService.getFileAsInputStream(fileId);

        response.setContentType(fileMetadata.getMimetype());
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileMetadata.getFilename() + "\"");
        IOUtils.copy(fileStream, response.getOutputStream());

        response.flushBuffer();
    }

    /**
     * Upload a file.
     *
     * @param file File to be uploaded
     * @return
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/space/{spaceId}/file/{fileId}/contents", method = POST)
    @ResponseBody
    public ResponseEntity updateFileContent(@PathVariable String spaceId,
                                            @PathVariable String fileId,
                                            @RequestParam MultipartFile file) {
        ensureSpace(spaceId);

        fileMetadataService.updateFile(fileId, file);
        return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
    }

    private void ensureSpace(String space) {
        spaceService.getOfType(space); // throws exception if space doesn't exist
    }
}
