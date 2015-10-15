package cz.cvut.fel.integracniportal.controller;

import cz.cvut.fel.integracniportal.model.FileMetadata;
import cz.cvut.fel.integracniportal.model.Folder;
import cz.cvut.fel.integracniportal.model.UserDetails;
import cz.cvut.fel.integracniportal.representation.*;
import cz.cvut.fel.integracniportal.service.*;
import cz.cvut.fel.integracniportal.utils.UploadUtils;
import org.apache.commons.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author Radek Jezdik
 */
@Controller
@RequestMapping("/rest")
public class FolderController extends AbstractController {

    @Autowired
    private UserDetailsService userService;

    @Autowired
    private FolderService folderService;

    @Autowired
    private SpaceService spaceService;

    @Autowired
    private FileMetadataService fileMetadataService;

    @Autowired
    private LabelService labelService;

    /**
     * Returns a folder by its id.
     *
     * @return A folder representation object.
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/space/{spaceId}/folder/{folderId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getFolder(@PathVariable String spaceId,
                                    @PathVariable Long folderId) {
        ensureSpace(spaceId);
        FolderRepresentation folderRepresentation = folderService.getFolderRepresentationById(folderId, userService.getCurrentUser());
        return new ResponseEntity(folderRepresentation, HttpStatus.OK);
    }

    /**
     * Create a subfolder.
     *
     * @param parentFolderId       Id of a parent folder in which the new one should be created.
     * @param folderRepresentation Folder representation of the folder to be created.
     * @return
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/space/{spaceId}/folder/{parentFolderId}/folder", method = POST, consumes = "application/json")
    @ResponseBody
    public ResponseEntity createSubFolder(@PathVariable String spaceId,
                                          @PathVariable Long parentFolderId,
                                          @RequestBody NameRepresentation folderRepresentation) {
        ensureSpace(spaceId);
        UserDetails currentUser = userService.getCurrentUser();
        Folder newFolder = folderService.createSubFolder(folderRepresentation.getName(), parentFolderId, currentUser);
        return new ResponseEntity(new FolderRepresentation(newFolder, currentUser, true), HttpStatus.CREATED);
    }

    /**
     * Upload a file.
     *
     * @return
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/space/{spaceId}/folder/{folderId}/file", method = POST)
    @ResponseBody
    public ResponseEntity uploadFile(@PathVariable String spaceId,
                                     @PathVariable Long folderId,
                                     HttpServletRequest request) throws IOException, FileUploadException {
        ensureSpace(spaceId);

        FileUpload fileUpload = UploadUtils.handleFileUpload(request);

        FileMetadata fileMetadata = fileMetadataService.uploadFileToFolder(folderId, fileUpload);
        return new ResponseEntity(new FileMetadataRepresentation(fileMetadata), HttpStatus.CREATED);
    }

    /**
     * Renames the folder.
     *
     * @param representation representation with new folder name
     * @return
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/space/{spaceId}/folder/{folderId}/nameChange", method = POST)
    @ResponseBody
    public ResponseEntity renameFolder(@PathVariable String spaceId,
                                       @PathVariable Long folderId,
                                       @RequestBody NameRepresentation representation) {
        ensureSpace(spaceId);
        folderService.renameFolder(folderId, representation.getName());
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
     * Moves the folder.
     *
     * @param representation representation with new parent folder ID.
     * @return
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/space/{spaceId}/folder/{folderId}/parentChange", method = POST)
    @ResponseBody
    public ResponseEntity moveFolder(@PathVariable String spaceId,
                                     @PathVariable Long folderId,
                                     @RequestBody FolderParentRepresentation representation) {
        ensureSpace(spaceId);
        folderService.moveFolder(folderId, representation.getParentId());
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
     * Moves the folder to bin.
     *
     * @param folderId the ID of the folder to be move to trash
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/space/{spaceId}/folder/{folderId}/trash", method = POST)
    public ResponseEntity moveFolderToBin(@PathVariable String spaceId,
                                          @PathVariable Long folderId) {
        ensureSpace(spaceId);
        folderService.removeFolder(folderId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
     * Moves the folder to online state.
     *
     * @param folderId the ID of the folder to be move online
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/space/{spaceId}/folder/{folderId}/online", method = POST)
    public ResponseEntity moveToOnline(@PathVariable String spaceId,
                                       @PathVariable Long folderId) {
        ensureSpace(spaceId);
        folderService.moveFolderOnline(folderId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
     * Moves the folder to offline state.
     *
     * @param folderId the ID of the folder to be move offline
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/space/{spaceId}/folder/{folderId}/offline", method = POST)
    public ResponseEntity moveToOffline(@PathVariable String spaceId,
                                        @PathVariable Long folderId) {
        ensureSpace(spaceId);
        folderService.moveFolderOffline(folderId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
     * Adds a user label to the folder
     *
     * @param folderId the ID of the folder to add the label to
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/space/{spaceId}/folder/{folderId}/addLabel", method = POST)
    public ResponseEntity addLabel(@PathVariable String spaceId,
                                   @PathVariable Long folderId,
                                   @RequestBody LabelIdRepresentation representation) {
        ensureSpace(spaceId);
        labelService.addLabelToFolder(folderId, representation, userService.getCurrentUser());
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
     * Removes a user label from the folder
     *
     * @param folderId the ID of the folder to remove the label from
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/space/{spaceId}/folder/{folderId}/removeLabel", method = POST)
    public ResponseEntity removeLabel(@PathVariable String spaceId,
                                      @PathVariable Long folderId,
                                      @RequestBody LabelIdRepresentation representation) {
        ensureSpace(spaceId);
        labelService.removeLabelFromFolder(folderId, representation, userService.getCurrentUser());
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
     * Marks the folder as favorite for user.
     *
     * @param folderId the ID of the file to favorite
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/space/{spaceId}/folder/{folderId}/favorite", method = POST)
    public ResponseEntity favoriteFolder(@PathVariable String spaceId,
                                         @PathVariable Long folderId) {
        ensureSpace(spaceId);
        folderService.favoriteFolder(folderId, userService.getCurrentUser());
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
     * Unmarks the folder as favorite for user.
     *
     * @param folderId the ID of the folder to unfavorite
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/space/{spaceId}/folder/{folderId}/unfavorite", method = POST)
    public ResponseEntity unfavoriteFolder(@PathVariable String spaceId,
                                           @PathVariable Long folderId) {
        ensureSpace(spaceId);
        folderService.unfavoriteFolder(folderId, userService.getCurrentUser());
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
     * Shares the folder with given list of users.
     *
     * @param folderId       the ID of folder to share
     * @param representation the representation with users to share the folder with
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/space/{spaceId}/folder/{folderId}/share", method = POST)
    public ResponseEntity shareFolder(@PathVariable String spaceId,
                                      @PathVariable Long folderId,
                                      @RequestBody ShareRepresentation representation) {
        ensureSpace(spaceId);
        folderService.shareFolder(folderId, representation.getShareWith(), userService.getCurrentUser());
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
     * Downloads the whole contents of folder as a compressed archive.
     *
     * @param folderId       the ID of folder to share
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/space/{spaceId}/folder/{folderId}/download", method = GET)
    public ResponseEntity downloadFolder(@PathVariable String spaceId,
                                      @PathVariable Long folderId) {
        ensureSpace(spaceId);
        // TODO async processing
//        folderService.downloadFolder(folderId, representation.getShareWith(), userService.getCurrentUser());
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    private void ensureSpace(String space) {
        spaceService.getOfType(space); // throws exception if space doesn't exist
    }

}
