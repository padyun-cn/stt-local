package com.uls.utilites.io;

import android.os.CancellationSignal;
import android.text.TextUtils;

import com.uls.utilites.content.SProgressor;
import com.uls.utilites.exceptions.CoreCancellationException;
import com.uls.utilites.exceptions.RunOnUIThreadException;
import com.uls.utilites.un.Useless;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * Created by daiepngfei on 2020-05-18
 */
public class ZipTool {

    /**
     *
     * @param files
     * @param destPath
     * @return
     */
    public static boolean zip(File[] files, String destPath, CancellationSignal signal) throws InterruptedException, RunOnUIThreadException, IOException, CoreCancellationException {
        return zip(files, destPath, 8192, signal, null);
    }

    /**
     *
     * @param files
     * @param destPath
     * @return
     */
    public static boolean zip(File[] files, String destPath, CancellationSignal signal, SProgressor progressor) throws InterruptedException, RunOnUIThreadException, IOException, CoreCancellationException {
        return zip(files, destPath, 8192, signal, progressor);
    }

    /**
     *
     * @param files
     * @param destPath
     * @param bufferSize
     * @return
     */
    public static boolean zip(File[] files, String destPath, int bufferSize, CancellationSignal signal, SProgressor progressor) throws InterruptedException, RunOnUIThreadException, IOException, CoreCancellationException {



        if (files == null || files.length == 0 || TextUtils.isEmpty(destPath)) {
            return false;
        }

        final File zippedFile = new File(destPath + ".zipping");
        if (zippedFile.exists() && !zippedFile.delete()) {
            return false;
        }

        BufferedOutputStream bufferedOutputStream = null;
        ZipOutputStream zipOutputStream = null;
        boolean exeResult = false;
        try {
            bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(zippedFile));
            zipOutputStream = new ZipOutputStream(bufferedOutputStream);
            zipOutputStream.setLevel(1);
            if (zipFiles(zipOutputStream, files, new byte[bufferSize / 2 * 2], Files.name(destPath), signal, progressor)
                    && zippedFile.exists()
                    && zippedFile.renameTo(new File(destPath))) {
                exeResult = true;
            }
        }  finally {
            if (zippedFile.exists()) {
                //noinspection ResultOfMethodCallIgnored
                zippedFile.delete();
            }
            if (zipOutputStream != null) {
                try {
                    zipOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        bufferedOutputStream.close();
                    } catch (IOException ioe) {
                        ioe.addSuppressed(e);
                        ioe.printStackTrace();
                    }
                }
            }
        }

        return exeResult;

    }

    private static boolean zipFiles(ZipOutputStream zipOutputStream, File[] files, byte[] buffer, String curEntryBaseName, CancellationSignal signal, SProgressor progressor) throws CoreCancellationException, InterruptedException, IOException {
        if (Useless.uselessArgs(zipOutputStream, files, buffer, curEntryBaseName)) {
            return false;
        }
        if (files != null && files.length > 0) {
            for (File f : files) {
                if(signal != null && signal.isCanceled()){
                    throw new CoreCancellationException();
                }
                Useless.assertThreadInterruption();
                if (f == null) continue;
                if (f.isDirectory()) {
                    final String tarBase = curEntryBaseName + File.separator + f.getName();
                    zipOutputStream.putNextEntry(new ZipEntry(tarBase));
                    zipFiles(zipOutputStream, f.listFiles(), buffer, tarBase, signal, progressor);
                } else {
                    zipOutputStream.putNextEntry(new ZipEntry(f.getName()));
                    try(FileInputStream fis = new FileInputStream(f)) {
                        int readLength;
                        while ((readLength = fis.read(buffer, 0, buffer.length)) != -1) {
                            if(signal != null && signal.isCanceled()){
                                throw new CoreCancellationException();
                            }
                            Useless.assertThreadInterruption();
                            zipOutputStream.write(buffer, 0, readLength);
                            if(progressor != null){
                                progressor.updateProgressBy(readLength);
                            }
                        }
                    }
                }
            }
        }
        return true;
    }


}
