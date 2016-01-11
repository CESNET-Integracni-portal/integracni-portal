package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.dao.NodeDao;
import cz.cvut.fel.integracniportal.exceptions.NotFoundException;
import cz.cvut.fel.integracniportal.model.FileMetadata;
import cz.cvut.fel.integracniportal.model.Folder;
import cz.cvut.fel.integracniportal.model.Node;
import cz.cvut.fel.integracniportal.model.UserDetails;
import cz.cvut.fel.integracniportal.representation.FileMetadataRepresentation;
import cz.cvut.fel.integracniportal.representation.FolderRepresentation;
import cz.cvut.fel.integracniportal.representation.SharedNodeRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Eldar Iosip
 */
@Service
@Transactional
public class NodeServiceImpl implements NodeService {

    @Autowired
    private NodeDao nodeDao;

    @Autowired
    private AclService aclService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private FileMetadataService fileMetadataService;

    @Autowired
    private FolderService folderService;

    @Autowired
    private SpaceService spaceService;

    @Override
    public Node getNodeById(Long nodeId) {
        return nodeDao.get(nodeId);
    }

    @Override
    public SharedNodeRepresentation getSharedNodeRepresentation(String spaceId) {
        UserDetails currentUser = userDetailsService.getCurrentUser();

        Set<Node> sharedNodes = aclService.getSharedNodes(spaceId, currentUser);

        Set<FolderRepresentation> folderRepresentations = new HashSet<FolderRepresentation>();
        Set<FileMetadataRepresentation> fileRepresentations = new HashSet<FileMetadataRepresentation>();

        for (Node node : sharedNodes) {
            if (node.isFolder()) {
                folderRepresentations.add(new FolderRepresentation((Folder) node, currentUser, false));
            } else {
                fileRepresentations.add(new FileMetadataRepresentation((FileMetadata) node));
            }
        }

        return new SharedNodeRepresentation(folderRepresentations, fileRepresentations);
    }

    @Override
    public void saveNode(Node node) {
        nodeDao.save(node);
    }

    @Override
    public void removeNode(Node node, boolean removeFromRepository) {

        for (Folder subFolder : node.getFolders()) {
            folderService.removeFolder(subFolder, true);
        }

        for (FileMetadata fileMetadata : node.getFiles()) {
            fileMetadataService.deleteFile(fileMetadata, true);
        }

        if (node.isFolder() && removeFromRepository) {
            getFileApi(node.getSpace()).moveFolderToBin((Folder) node);
        } else if (!node.isFolder() && removeFromRepository) {
            getFileApi(node.getSpace()).moveFileToBin((FileMetadata) node);
        }

        nodeDao.delete(node);
    }

    private FileApiAdapter getFileApi(String type) {
        return new FileApiAdapter(spaceService.getOfType(type));
    }
}
