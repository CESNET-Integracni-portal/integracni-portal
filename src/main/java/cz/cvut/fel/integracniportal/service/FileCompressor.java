package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.model.FileMetadata;

import java.io.OutputStream;

/**
 * Created by mata on 14.1.16.
 */
public interface FileCompressor {

    /**
     * Returns outputStream through which compressed data is sent.
     */
    OutputStream getOutputStream();

    /**
     * Inserts a new file into the archive with its default name
     *
     * @param fileMetadata
     */
    void putFile(FileMetadata fileMetadata);

    /**
     * Inserts a new file into the archive with name given in path variable.
     * Used in archiving subfolders.
     *
     * @param fileMetadata
     * @param path
     */
    void putFile(FileMetadata fileMetadata, String path);

    /**
     * Finishes the archived file. Should be called right after last item was put.
     *
     */
    void finish();


    /**
     * Returns extension used by archiving method. e.g. ".zip".
     *
     * @return
     */
    String getExtension();


    /**
     * Flushes the inner outputStream.
     *
     */
    void flush();

}
