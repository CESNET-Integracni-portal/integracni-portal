package cz.cvut.fel.integracniportal.api;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Radek Jezdik
 */
public interface FileRepository extends TypedBean {

    public String getName();

    public void createFolder(FolderDefinition folder);

    public void moveFolder(FolderDefinition from, FolderDefinition to);

    public void renameFolder(String newName, FolderDefinition folder);

    public void putFile(FileDefinition file, InputStream stream);

    public void getFile(FileDefinition file, OutputStream outputStream);

    public void moveFile(FileDefinition file, FolderDefinition to);

    public void renameFile(String newName, FileDefinition file);

    public FileDefinition getFileMetadata(FileDefinition file);

    public void deleteFile(FileDefinition file);

    public void deleteFolder(FolderDefinition folderDef);

}
