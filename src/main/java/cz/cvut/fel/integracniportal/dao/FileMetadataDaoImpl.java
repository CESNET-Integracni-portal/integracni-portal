package cz.cvut.fel.integracniportal.dao;

import cz.cvut.fel.integracniportal.exceptions.FileNotFoundException;
import cz.cvut.fel.integracniportal.model.FileMetadata;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static cz.cvut.fel.integracniportal.model.QFileMetadata.fileMetadata;

/**
 * Hibernate implementation of the FileMetadataDao interface.
 */
@Repository
public class FileMetadataDaoImpl extends GenericHibernateDao<FileMetadata> implements FileMetadataDao {

    public FileMetadataDaoImpl() {
        super(FileMetadata.class);
    }

    @Override
    @Transactional(readOnly = true)
    public FileMetadata getByUUID(String fileMetadataUuid) {
        FileMetadata fileMetadata = get(fileMetadataUuid);
        if (fileMetadata == null) {
            throw new FileNotFoundException();
        }
        return fileMetadata;
    }

    @Override
    public List<FileMetadata> getAllFileMetadatas() {
        return from(fileMetadata)
                .orderBy(fileMetadata.createdOn.asc())
                .list(fileMetadata);
    }

    @Override
    @Transactional
    public void createFileMetadata(FileMetadata fileMetadata) {
        Date currentDate = new Date();
        fileMetadata.setCreatedOn(currentDate);
        fileMetadata.setChangedOn(currentDate);
        save(fileMetadata);
    }

    @Override
    public void update(FileMetadata fileMetadata) {
        fileMetadata.setChangedOn(new Date());
        super.update(fileMetadata);
    }

    @Override
    public List<FileMetadata> getFilesForDeletion() {
        return from(fileMetadata)
                .where(fileMetadata.deleteOn.isNotNull())
                .where(fileMetadata.deleteOn.lt(new Date()))
                .list(fileMetadata);
    }
}
