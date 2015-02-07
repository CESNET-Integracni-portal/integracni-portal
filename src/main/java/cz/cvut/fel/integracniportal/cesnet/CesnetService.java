package cz.cvut.fel.integracniportal.cesnet;

import java.io.InputStream;
import java.util.List;


public interface CesnetService {

    public List<CesnetFileMetadata> getFileList();

    public List<CesnetFileMetadata> getFileListByType(FileState fileState);

    public InputStream getFile(String filename);

    public void deleteFile(String filename);

    public CesnetFileMetadata getFileMetadata(String filename);

    public void moveFileOffline(String filename);

    public void moveFileOnline(String filename);

    public void uploadFile(InputStream fileStream, String filename);

}
