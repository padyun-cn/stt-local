package com.uls.utilites.io;


import com.uls.utilites.content.SProgressor;
import com.uls.utilites.exceptions.CoreCancellationException;
import com.uls.utilites.un.Useless;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import androidx.core.os.CancellationSignal;
import androidx.core.util.Consumer;


/**
 * Created by daiepngfei on 2020-06-02
 */
public class UnzipTool {

    /**
     * @param zipFile
     * @param directory
     * @param unzipFilter
     *
     * @throws IOException
     */
    public static void unzip(File zipFile, File directory, CancellationSignal cancellationSignal, Consumer<Float> onProgress, UnzipCustomizer unzipFilter) throws Exception {
        if (zipFile == null || !zipFile.exists() || directory == null) {
            throw new IOException();
        }

        if (!directory.exists()) {
            if (directory.mkdirs()) {
                throw new IOException();
            }
        }
        try(ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile))) {
            final SProgressor sProgressor = onProgress == null ? null : new SProgressor(zipFile.length(), onProgress);
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                // canceled
                if (cancellationSignal != null && cancellationSignal.isCanceled()) {
                    throw new CoreCancellationException();
                }
                // interruption
                Useless.assertThreadInterruption();
                // directory
                File directoryFile = directory;
                if (unzipFilter != null) {
                    directoryFile = unzipFilter.filteredDirectoryPath(directory, entry.getName());
                }

                if (!directoryFile.exists()) {
                    if (!directoryFile.mkdirs()) {
                        throw new IOException();
                    }
                }
                // do working
                final File f = new File(directoryFile, entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectory(f);
                } else {
                    if (unzipFilter == null || !unzipFilter.onCustomWrite(f, zipInputStream, cancellationSignal, sProgressor)) {
                        Files.writeBytes(zipInputStream, f, false, cancellationSignal, sProgressor);
                    }
                }
            }
            if (sProgressor != null) {
                sProgressor.updateProgressTo(zipFile.length());
            }
        }

    }

    public interface UnzipCustomizer {
        File filteredDirectoryPath(File defaultDir, String zipEntryName);
        boolean onCustomWrite(File f, InputStream inputStream, CancellationSignal signal, SProgressor sProgressor) throws Exception;
    }
}
