package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.api.FileRepository;
import cz.cvut.fel.integracniportal.representation.SpaceRepresentation;

import java.util.List;

/**
 * Space service is a provider to {@link cz.cvut.fel.integracniportal.api.FileRepository file repositories} registered
 * in the system.
 *
 * @author Radek Jezdik
 */
public interface SpaceService {

    /**
     * Returns the {@link cz.cvut.fel.integracniportal.api.FileRepository file repository} of the given type.
     *
     * @param type the type (identification) of the file repository
     * @return
     */
    public FileRepository getOfType(String type);

    /**
     * Returns all types of registered {@link cz.cvut.fel.integracniportal.api.FileRepository file repositories} registered.
     *
     * @return all the types (identifications) of registered spaces.
     */
    public List<SpaceRepresentation> getTypes();

}
