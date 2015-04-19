package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.dao.LabelDao;
import cz.cvut.fel.integracniportal.exceptions.AlreadyExistsException;
import cz.cvut.fel.integracniportal.exceptions.NotFoundException;
import cz.cvut.fel.integracniportal.model.FileMetadata;
import cz.cvut.fel.integracniportal.model.Folder;
import cz.cvut.fel.integracniportal.model.Label;
import cz.cvut.fel.integracniportal.representation.FileMetadataRepresentation;
import cz.cvut.fel.integracniportal.representation.FolderRepresentation;
import cz.cvut.fel.integracniportal.representation.LabelMigrationRepresentation;
import cz.cvut.fel.integracniportal.representation.LabelRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vavat on 24. 3. 2015.
 */
@Service
@Transactional
public class LabelServiceImpl implements LabelService {

    @Autowired
    private LabelDao labelDao;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private ArchiveFolderService archiveFolderService;

    @Autowired
    private ArchiveFileMetadataService archiveFileMetadataService;

    @Override
    public List<Label> getAllLabels() {
        return labelDao.getAllLabels();
    }

    @Override
    public List<Label> getUserLabels(long userId) {
        List<Label> result = labelDao.getUserLabels(userId);
        if(result == null){
            throw new NotFoundException("labels.notFound.user.id", userId);
        }
        return result;
    }

    @Override
    public Label getLabelByName(String name) {
        return labelDao.getLabelByName(name);
    }

    @Override
    public Label getLabelById(long labelId) {
        Label label = labelDao.getLabelById(labelId);
        if(label == null){
            throw new NotFoundException("label.notFound.id", labelId);
        }
        return label;
    }

    @Override
    public Label createLabel(LabelRepresentation labelRepresentation) {
        if(getLabelByName(labelRepresentation.getName()) != null){
            throw new AlreadyExistsException("label.alreadyExists");
        }
        Label label = new Label();
        updateLabelFromRepresentation(label, labelRepresentation);
        labelDao.save(label);
        return label;
    }

    @Override
    public Label updateLabel(Long labelId, LabelRepresentation labelRepresentation) {
        Label label = getLabelById(labelId);
        updateLabelFromRepresentation(label, labelRepresentation);
        labelDao.save(label);
        return label;
    }

    private void updateLabelFromRepresentation(Label label, LabelRepresentation labelRepresentation){
        if(labelRepresentation.getId() != null){
            label.setId(labelRepresentation.getId());
        }
        if(labelRepresentation.getName() != null){
            label.setName(labelRepresentation.getName());
        }
        if(labelRepresentation.getColor() != null){
            label.setColor(labelRepresentation.getColor());
        }
        if(labelRepresentation.getOwner() != null){
            label.setOwner(userDetailsService.getUserById(labelRepresentation.getOwner()));
        }
    }

    @Override
    public void saveLabel(Label label) {
        labelDao.save(label);
    }

    @Override
    public void removeLabel(Label label) {
        labelDao.delete(label);
    }

    @Override
    public void addLabelToFile(String fileUuid, LabelMigrationRepresentation representation) {
        FileMetadata file = archiveFileMetadataService.getFileMetadataByUuid(fileUuid);
        Label label = getLabelById(representation.getLabelId());
        if (file.getLabels() == null){
            file.setLabels(new ArrayList<Label>());
        }
        if(containLabel(label,file.getLabels())){
            throw new AlreadyExistsException("label.alreadyExists.onFile.id",file.getId());
        }else{
            file.getLabels().add(label);
        }
        archiveFileMetadataService.updateFileMetadata(file);
    }

    @Override
    public void removeLabelFromFile(String fileUuid, LabelMigrationRepresentation representation) {
        FileMetadata file = archiveFileMetadataService.getFileMetadataByUuid(fileUuid);
        List<Label> labels = file.getLabels();
        if (labels == null){
            throw new NotFoundException("label.onFile.notFound.id", representation.getLabelId());
        }
        for (Label tmp: labels){
            if(tmp.getLabelId() == representation.getLabelId()){
                labels.remove(tmp);
                break;
            }
        }
        file.setLabels(labels);
        archiveFileMetadataService.updateFileMetadata(file);
    }

    @Override
    public void addLabelToFolder(Long folderId, LabelMigrationRepresentation representation) {
        Folder folder = archiveFolderService.getFolderById(folderId);
        Label label = getLabelById(representation.getLabelId());
        if(folder.getLabels() == null){
            folder.setLabels(new ArrayList<Label>());
        }
        if(containLabel(label,folder.getLabels())){
            throw new AlreadyExistsException("label.alreadyExists.onFolder.id",folder.getId());
        }else{
            folder.getLabels().add(label);
        }
        archiveFolderService.updateFolder(folder);
    }

    @Override
    public void removeLabelFromFolder(Long folderId, LabelMigrationRepresentation representation) {
        Folder folder = archiveFolderService.getFolderById(folderId);
        List<Label> labels = folder.getLabels();
        if(labels == null){
            throw new NotFoundException("label.onFolder.notFound.id", representation.getLabelId());
        }
        for (Label tmp: labels){
            if(tmp.getLabelId() == representation.getLabelId()){
                labels.remove(tmp);
                break;
            }
        }
        folder.setLabels(labels);
        archiveFolderService.updateFolder(folder);
    }

    private boolean containLabel(Label label, List<Label> list){
        boolean res = false;
        for (Label tmp: list){
            if(tmp.getLabelId() == label.getLabelId()){
                res = true;
                break;
            }
        }
        return res;
    }

    public void setLabelDao(LabelDao labelDao) {
        this.labelDao = labelDao;
    }

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public void setArchiveFolderService(ArchiveFolderService archiveFolderService) {
        this.archiveFolderService = archiveFolderService;
    }

    public void setArchiveFileMetadataService(ArchiveFileMetadataService archiveFileMetadataService) {
        this.archiveFileMetadataService = archiveFileMetadataService;
    }
}
