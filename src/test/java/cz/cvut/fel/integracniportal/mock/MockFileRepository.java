package cz.cvut.fel.integracniportal.mock;

import cz.cvut.fel.integracniportal.api.BinFileRepository;
import cz.cvut.fel.integracniportal.api.FileDefinition;
import cz.cvut.fel.integracniportal.api.FileRepository;
import cz.cvut.fel.integracniportal.api.FolderDefinition;
import cz.cvut.fel.integracniportal.exceptions.ServiceAccessException;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
        try {
            IOUtils.toString(stream);
        } catch (IOException e) {
            new ServiceAccessException("Eh");
        }
    }

    @Override
    public void getFile(FileDefinition file, OutputStream outputStream) {
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
    public void deleteFile(FileDefinition file) {

    }

    @Override
    public void deleteFolder(FolderDefinition folderDef) {

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
