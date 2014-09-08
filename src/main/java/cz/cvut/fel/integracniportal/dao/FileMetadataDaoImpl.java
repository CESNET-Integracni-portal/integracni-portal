package cz.cvut.fel.integracniportal.dao;

import cz.cvut.fel.integracniportal.model.FileMetadata;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;

/**
 * Hibernate implementation of the FileMetadataDao interface.
 */
@Repository
public class FileMetadataDaoImpl extends HibernateDao implements FileMetadataDao {

    @Override
    @Transactional(readOnly = true)
    public FileMetadata getFileMetadataByUuid(String fileMetadataUuid) throws FileNotFoundException {
        FileMetadata fileMetadata = getHibernateTemplate().get(FileMetadata.class, fileMetadataUuid);
        if (fileMetadata == null) {
            throw new FileNotFoundException();
        }
        return fileMetadata;
    }

    @Override
    public List<FileMetadata> getAllFileMetadatas() {
        Criteria c = getCriteria(FileMetadata.class, "rf");
        c.addOrder(Order.asc("rf.createdOn"));
        return c.list();
    }

    @Override
    @Transactional
    public void createFileMetadata(FileMetadata fileMetadata) {
        Date currentDate = new Date();
        fileMetadata.setCreatedOn(currentDate);
        fileMetadata.setChangedOn(currentDate);
        getHibernateTemplate().save(fileMetadata);
    }

    @Override
    public void updateFileMetadata(FileMetadata fileMetadata) {
        fileMetadata.setChangedOn(new Date());
        getHibernateTemplate().update(fileMetadata);
    }

    @Override
    @Transactional
    public void removeFileMetadata(FileMetadata fileMetadata) {
        getHibernateTemplate().delete(fileMetadata);
    }
}
