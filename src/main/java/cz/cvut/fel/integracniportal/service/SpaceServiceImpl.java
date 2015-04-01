package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.api.FileRepository;
import cz.cvut.fel.integracniportal.exceptions.NotFoundException;
import cz.cvut.fel.integracniportal.representation.SpaceRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Radek Jezdik
 */
@Service
public class SpaceServiceImpl implements SpaceService {

    @Autowired
    private List<FileRepository> fileRepositories;

    @Override
    public FileRepository getOfType(String type) {
        for (FileRepository repository : fileRepositories) {
            if (repository.getType().equals(type)) {
                return repository;
            }
        }

        throw new NotFoundException("Could not found file repository for type '" + type + "'");
    }

    @Override
    public List<SpaceRepresentation> getTypes() {
        List<SpaceRepresentation> spaces = new ArrayList<SpaceRepresentation>();
        for (FileRepository fileRepository : fileRepositories) {
            spaces.add(new SpaceRepresentation(fileRepository.getType(), fileRepository.getName()));
        }
        return spaces;
    }

}
