package cz.cvut.fel.integracniportal.dao;

import cz.cvut.fel.integracniportal.exceptions.FileNotFoundException;
import cz.cvut.fel.integracniportal.model.FileMetadata;
import cz.cvut.fel.integracniportal.model.QFileMetadata;
import cz.cvut.fel.integracniportal.model.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

import static cz.cvut.fel.integracniportal.model.QFileMetadata.fileMetadata;
import static cz.cvut.fel.integracniportal.model.QLabel.label;

/**
 * Hibernate implementation of the FileMetadataDao interface.
 */
@Repository
public class FileMetadataDaoImpl extends GenericHibernateDao<FileMetadata> implements FileMetadataDao {

    public FileMetadataDaoImpl() {
        super(FileMetadata.class);
    }

    @Override
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
                .where(fileMetadata.deleted)
                .where(fileMetadata.deletedOn.isNotNull())
                .where(fileMetadata.deletedOn.lt(new Date()))
                .list(fileMetadata);
    }

    @Override
    public List<FileMetadata> getAllTopLevelFiles(String spaceId, UserDetails owner) {
        return from(fileMetadata)
                .where(fileMetadata.owner.eq(owner))
                .where(fileMetadata.parent.isNull())
                .where(fileMetadata.space.eq(spaceId))
                .orderBy(fileMetadata.name.asc())
                .list(fileMetadata);
    }

    @Override
    public List<FileMetadata> getFilesByLabels(String spaceId, List<String> labelIds, UserDetails owner) {
        QFileMetadata file2 = new QFileMetadata("file2");
        return from(fileMetadata)
                .where(fileMetadata.owner.eq(owner))
                .where(fileMetadata.space.eq(spaceId))
                .where(fileMetadata.notIn(subQuery()
                        .from(file2)
                        .leftJoin(file2.labels, label)
                        .where(label.labelId.notIn(labelIds).or(file2.labels.isEmpty()))
                        .list(file2)))
                .orderBy(fileMetadata.name.asc())
                .distinct()
                .list(fileMetadata);
    }

}
