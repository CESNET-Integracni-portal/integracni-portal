package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.exceptions.FileAccessException;
import cz.cvut.fel.integracniportal.model.FileMetadata;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CronService {

    private static final Logger logger = Logger.getLogger(CronService.class);

    @Autowired
    private FileMetadataService archiveFileMetadataService;

    @Scheduled(cron = "${cron.deleteOldArchivedFiles}")
    public void deleteOldArchivedFiles() {
        List<FileMetadata> fileMetadataList = archiveFileMetadataService.getOldFilesForDeletion();
        logger.info("Deleting " + fileMetadataList.size() + " old archived files.");
        try {
            for (FileMetadata fileMetadata : fileMetadataList) {
                try {
                    archiveFileMetadataService.deleteFile(fileMetadata.getId());
                } catch (FileAccessException e) {
                    logger.error("Unable to delete old archived file '" + fileMetadata.getFilename() + "' (UUID " + fileMetadata.getId() + "): file not found.");
                }
            }
        } catch (Error e) {
            logger.error("Unable to delete old archived files: Cesnet service unavailable", e);
        }
    }

}
