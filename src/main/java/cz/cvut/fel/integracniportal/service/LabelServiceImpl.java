package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.dao.LabelDao;
import cz.cvut.fel.integracniportal.exceptions.AlreadyExistsException;
import cz.cvut.fel.integracniportal.exceptions.InvalidStateException;
import cz.cvut.fel.integracniportal.exceptions.NotFoundException;
import cz.cvut.fel.integracniportal.model.FileMetadata;
import cz.cvut.fel.integracniportal.model.Folder;
import cz.cvut.fel.integracniportal.model.Label;
import cz.cvut.fel.integracniportal.model.UserDetails;
import cz.cvut.fel.integracniportal.representation.LabelIdRepresentation;
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
    private FolderService folderService;

    @Autowired
    private FileMetadataService fileMetadataService;

    @Override
    public List<Label> getAllLabels() {
        return labelDao.getAllLabels();
    }

    @Override
    public List<Label> getUserLabels(UserDetails owner) {
        return labelDao.getUserLabels(owner.getId());
    }

    @Override
    public Label getLabelByName(String name) {
        return labelDao.getLabelByName(name);
    }

    @Override
    public Label getLabelById(long labelId) {
        Label label = labelDao.get(labelId);
        if (label == null) {
            throw new NotFoundException("label.notFound.id", labelId);
        }
        return label;
    }

    @Override
    public Label createLabel(LabelRepresentation labelRepresentation, UserDetails owner) {
        if (getLabelByName(labelRepresentation.getName()) != null) {
            throw new AlreadyExistsException("label.alreadyExists");
        }
        Label label = new Label();
        updateLabelFromRepresentation(label, labelRepresentation);
        label.setOwner(owner);
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

    private void updateLabelFromRepresentation(Label label, LabelRepresentation labelRepresentation) {
        if (labelRepresentation.getName() != null) {
            label.setName(labelRepresentation.getName());
        }
        if (labelRepresentation.getColor() != null) {
            label.setColor(labelRepresentation.getColor());
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
    public void addLabelToFile(Long fileId, LabelIdRepresentation representation, UserDetails currentUser) {
        FileMetadata file = fileMetadataService.getFileMetadataById(fileId);
        Label label = getLabelById(representation.getLabelId());
        if (label.getOwner().getId().equals(currentUser.getId()) == false) {
            throw new InvalidStateException("Could not add label of other user");
        }
        if (file.getLabels() == null) {
            file.setLabels(new ArrayList<Label>());
        }
        if (containsLabel(label, file.getLabels())) {
            throw new AlreadyExistsException("label.alreadyExists.onFile.id", file.getId());
        } else {
            file.getLabels().add(label);
        }
        fileMetadataService.updateFileMetadata(file);
    }

    @Override
    public void removeLabelFromFile(Long fileId, LabelIdRepresentation representation, UserDetails currentUser) {
        FileMetadata file = fileMetadataService.getFileMetadataById(fileId);
        List<Label> labels = file.getLabels();
        if (labels == null) {
            throw new NotFoundException("label.onFile.notFound.id", representation.getLabelId());
        }
        for (Label label : labels) {
            if (label.getLabelId().equals(representation.getLabelId())) {
                if (label.getOwner().getId().equals(currentUser.getId()) == false) {
                    throw new InvalidStateException("Could not add label of other user");
                }
                labels.remove(label);
                break;
            }
        }
        file.setLabels(labels);
        fileMetadataService.updateFileMetadata(file);
    }

    @Override
    public void addLabelToFolder(Long folderId, LabelIdRepresentation representation, UserDetails currentUser) {
        Folder folder = folderService.getFolderById(folderId);
        Label label = getLabelById(representation.getLabelId());
        if (label.getOwner().getId().equals(currentUser.getId()) == false) {
            throw new InvalidStateException("Could not add label of other user");
        }
        if (folder.getLabels() == null) {
            folder.setLabels(new ArrayList<Label>());
        }
        if (folder.getLabels().contains(label)) {
            throw new AlreadyExistsException("label.alreadyExists.onFolder.id", folder.getId());
        } else {
            folder.getLabels().add(label);
        }
        folderService.updateFolder(folder);
    }

    @Override
    public void removeLabelFromFolder(Long folderId, LabelIdRepresentation representation, UserDetails currentUser) {
        Folder folder = folderService.getFolderById(folderId);
        List<Label> labels = folder.getLabels();
        if (labels == null) {
            throw new NotFoundException("label.onFolder.notFound.id", representation.getLabelId());
        }
        for (Label label : labels) {
            if (label.getLabelId().equals(representation.getLabelId())) {
                if (label.getOwner().getId().equals(currentUser.getId()) == false) {
                    throw new InvalidStateException("Could not add label of other user");
                }
                labels.remove(label);
                break;
            }
        }
        folder.setLabels(labels);
        folderService.updateFolder(folder);
    }

    private boolean containsLabel(Label label, List<Label> list) {
        for (Label tmp : list) {
            if (tmp.getLabelId().equals(label.getLabelId())) {
                return true;
            }
        }
        return false;
    }
}
