package cz.cvut.fel.integracniportal.mock;

import cz.cvut.fel.integracniportal.api.BinFileRepository;
import cz.cvut.fel.integracniportal.api.FileDefinition;
import cz.cvut.fel.integracniportal.api.FileRepository;
import cz.cvut.fel.integracniportal.api.FolderDefinition;

import java.io.InputStream;

/**
 * Mock class for {@link cz.cvut.fel.integracniportal.api.FileRepository} used in tests. As we do not test this class
 * in the core project.
 *
 * @author Radek Jezdik
 */
public class MockFileRepository implements FileRepository, BinFileRepository {
    @Override
    public void moveFolderToBin(FolderDefinition folder) {

    }

    @Override
    public void moveFileToBin(FileDefinition file) {

    }

    @Override
    public void createFolder(FolderDefinition folder) {

    }

    @Override
    public void moveFolder(FolderDefinition from, FolderDefinition to) {

    }

    @Override
    public void renameFolder(String newName, FolderDefinition folder) {

    }

    @Override
    public void putFile(FileDefinition file, InputStream stream) {

    }

    @Override
    public InputStream getFile(FileDefinition file) {
        return null;
    }

    @Override
    public void moveFile(FileDefinition file, FolderDefinition to) {

    }

    @Override
    public void renameFile(String newName, FileDefinition file) {

    }

    @Override
    public FileDefinition getFileMetadata(FileDefinition file) {
        return null;
    }

    @Override
    public String getType() {
        return "cesnet";
    }

    @Override
    public String getName() {
        return "CESNET";
    }
}
