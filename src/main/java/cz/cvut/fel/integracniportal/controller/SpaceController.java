package cz.cvut.fel.integracniportal.controller;

import cz.cvut.fel.integracniportal.model.FileMetadata;
import cz.cvut.fel.integracniportal.model.Folder;
import cz.cvut.fel.integracniportal.model.UserDetails;
import cz.cvut.fel.integracniportal.representation.*;
import cz.cvut.fel.integracniportal.service.*;
import cz.cvut.fel.integracniportal.utils.UploadUtils;
import cz.cvut.fel.integracniportal.validator.AggregateValidator;
import cz.cvut.fel.integracniportal.validator.FileSelectionValidator;
import org.apache.commons.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

    @Autowired
    FileSelectionValidator fileSelectionValidator;

    @InitBinder()
    public void initBinderUserDetailsResource(WebDataBinder binder) {

        Validator[] validatorArray = {fileSelectionValidator};

        binder.setValidator(new AggregateValidator(validatorArray));
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/space", method = GET)
    public ResponseEntity<List<SpaceRepresentation>> getSpaces() {
        return new ResponseEntity<List<SpaceRepresentation>>(spaceService.getTypes(), HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/space/{spaceId}", method = GET)
    public ResponseEntity<TopLevelFolderRepresentation> getRoot(
            @PathVariable String spaceId,
            @RequestParam(required = false) List<Long> labels) {

        TopLevelFolderRepresentation topLevelFolder;
        if (labels != null && labels.isEmpty() == false) {
            topLevelFolder = folderService.getTopLevelFolderByLabels(spaceId, labels, userService.getCurrentUser());
        } else {
            topLevelFolder = folderService.getTopLevelFolder(spaceId, userService.getCurrentUser());
        }
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
    public ResponseEntity<FolderRepresentation> uploadFile(@PathVariable String spaceId,
                                                           HttpServletRequest request) throws IOException, FileUploadException {

        FileUpload fileUpload = UploadUtils.handleFileUpload(request);

        FileMetadata fileMetadata = fileMetadataService.uploadFileToRoot(spaceId, fileUpload);
        return new ResponseEntity(new FileMetadataRepresentation(fileMetadata), HttpStatus.CREATED);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/space/{spaceId}/favorites", method = GET)
    public ResponseEntity<TopLevelFolderRepresentation> getFavorites(
            @PathVariable String spaceId) {

//        List<Folder> folders = folderService.getFavorites(spaceId, userService.getCurrentUser());
//        List<FileMetadata> fileMetadata = fileMetadataService.getFavorites(spaceId, userService.getCurrentUser());
        return new ResponseEntity(new TopLevelFolderRepresentation(Collections.<Folder>emptyList(), Collections.<FileMetadata>emptyList(), userService.getCurrentUser()), HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/space/{spaceId}/downloadSelection", method = POST)
    public void downloadSelection(@PathVariable String spaceId, @RequestBody FileSelectionRepresentation fileSelection, HttpServletResponse response) throws IOException {

        ensureSpace(spaceId);

        FileCompressor zipCompressor = new ZipFileCompressorImpl(response.getOutputStream(), fileMetadataService);

        response.setContentType("application/zip");

        response.setHeader("Content-Disposition", "attachment; filename=\"selection.zip\"");


        Map<FileMetadata, String> map = new HashMap<FileMetadata, String>();
        for(String folderId : fileSelection.getFolders()) {
            folderService.getFileMetadataPathMap(map, Long.valueOf(folderId), folderService.getFolderById(Long.valueOf(folderId)).getName());
        }

        zipCompressor.addFiles(map);

        for(String fileId : fileSelection.getFiles()){
            zipCompressor.addFile(fileMetadataService.getFileMetadataByUuid(fileId));
        }

        ExecutorService exec = Executors.newSingleThreadExecutor();
        Future f = exec.submit(zipCompressor);

        while(!f.isDone()){
            //
        }

        response.flushBuffer();
    }

    private void ensureSpace(String space) {
        spaceService.getOfType(space); // throws exception if space doesn't exist
    }

}
