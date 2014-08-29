package cz.cvut.fel.integracniportal.service;

import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class CronService {

    private static final Logger logger = Logger.getLogger(CronService.class);

    @Scheduled(cron="${cron.deleteOldArchivedFiles}")
    public void deleteOldArchivedFiles() {
        logger.info("Deleting old archived files.");
    }
}
