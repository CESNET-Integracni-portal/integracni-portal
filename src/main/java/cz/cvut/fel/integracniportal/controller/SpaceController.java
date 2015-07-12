package cz.cvut.fel.integracniportal.controller;

import cz.cvut.fel.integracniportal.model.FileMetadata;
import cz.cvut.fel.integracniportal.model.Folder;
import cz.cvut.fel.integracniportal.model.UserDetails;
import cz.cvut.fel.integracniportal.representation.*;
import cz.cvut.fel.integracniportal.service.FileMetadataService;
import cz.cvut.fel.integracniportal.service.FolderService;
import cz.cvut.fel.integracniportal.service.SpaceService;
import cz.cvut.fel.integracniportal.service.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author Radek Jezdik
 */
@Controller
@RequestMapping("/rest")
public class SpaceController extends AbstractController {

    @Autowired
    private SpaceService spaceService;

    @Autowired
    private FolderService folderService;

    @Autowired
    private UserDetailsService userService;

    @Autowired
    private FileMetadataService fileMetadataService;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/space", method = GET)
    public ResponseEntity<List<SpaceRepresentation>> getSpaces() {
        return new ResponseEntity<List<SpaceRepresentation>>(spaceService.getTypes(), HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/space/{spaceId}", method = GET)
    public ResponseEntity<TopLevelFolderRepresentation> getRoot(
            @PathVariable String spaceId,
            @RequestParam(required = false) List<String> labels) {

        // TODO labels
        TopLevelFolderRepresentation topLevelFolder = folderService.getTopLevelFolder(spaceId, userService.getCurrentUser());
        return new ResponseEntity<TopLevelFolderRepresentation>(topLevelFolder, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/space/{spaceId}/shared", method = GET)
    public ResponseEntity<List<SpaceRepresentation>> getShared(
            @PathVariable String spaceId) {

        throw new UnsupportedOperationException("Not implemented");
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/space/{spaceId}/folder", method = POST)
    public ResponseEntity<FolderRepresentation> createFolder(
            @PathVariable String spaceId,
            @RequestBody NameRepresentation representation) {

        UserDetails currentUser = userService.getCurrentUser();
        Folder folder = folderService.createTopLevelFolder(representation.getName(), spaceId, currentUser);
        FolderRepresentation folderRepresentation = new FolderRepresentation(folder, currentUser);
        return new ResponseEntity<FolderRepresentation>(folderRepresentation, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/space/{spaceId}/file", method = POST)
    public ResponseEntity<FolderRepresentation> uploadFile(
            @PathVariable String spaceId,
            @RequestParam MultipartFile file) {

        FileMetadata fileMetadata = fileMetadataService.uploadFileToRoot(spaceId, file);
        return new ResponseEntity(new FileMetadataRepresentation(fileMetadata), HttpStatus.CREATED);
    }

}
