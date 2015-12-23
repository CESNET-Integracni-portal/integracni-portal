package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.exceptions.FileAccessException;
import cz.cvut.fel.integracniportal.model.FileMetadata;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class CronService {

    private static final Logger logger = Logger.getLogger(CronService.class);

    @Autowired
    private FileMetadataService archiveFileMetadataService;

    @Autowired
    private PolicyService policyService;

    @Scheduled(cron = "${cron.deleteOldArchivedFiles}")
    public void deleteOldArchivedFiles() {
        List<FileMetadata> fileMetadataList = archiveFileMetadataService.getOldFilesForDeletion();
        logger.info("Deleting " + fileMetadataList.size() + " old archived files.");
        try {
            for (FileMetadata fileMetadata : fileMetadataList) {
                try {
                    archiveFileMetadataService.deleteFile(fileMetadata.getId());
                } catch (FileAccessException e) {
                    logger.error("Unable to delete old archived file '" + fileMetadata.getName() + "' (ID " + fileMetadata.getId() + "): file not found.");
                }
            }
        } catch (Error e) {
            logger.error("Unable to delete old archived files: Cesnet service unavailable", e);
        }
    }

    @Scheduled(cron = "${cron.processNodePolicies}")
    public void processNodePolicies() {
        Date date = this.resetTime(new Date());

        logger.info("Processing policies for date: " + date.toString());

        policyService.processByDate(date);

        logger.info("Policy processing done.");
    }

    /**
     * Reset time part to 00:00:00:00
     *
     * @param date to reset time on
     * @return Date object with "date" only
     */
    private Date resetTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

}
