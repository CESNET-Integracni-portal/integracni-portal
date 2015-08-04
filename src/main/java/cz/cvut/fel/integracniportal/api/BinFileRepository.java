package cz.cvut.fel.integracniportal.api;

/**
 * @author Radek Jezdik
 */
public interface BinFileRepository {

    public void moveFolderToBin(FolderDefinition folder);

    public void moveFileToBin(FileDefinition file);

}
