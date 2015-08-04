package cz.cvut.fel.integracniportal.api;

/**
 * @author Radek Jezdik
 */
public interface OfflinableFileRepository {

    public void moveFileOffline(FileDefinition file);

    public void moveFileOnline(FileDefinition file);

}
