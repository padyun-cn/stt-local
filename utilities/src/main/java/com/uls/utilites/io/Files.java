package com.uls.utilites.io;

import android.text.TextUtils;

import com.uls.utilites.content.SProgressor;
import com.uls.utilites.exceptions.ReadingTooLargeException;
import com.uls.utilites.exceptions.UiThreadProhibitedExcetption;
import com.uls.utilites.un.Useless;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CancellationException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import androidx.core.os.CancellationSignal;
import androidx.core.util.Consumer;


/**
 * Created by daiepngfei on 2020-12-15
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public final class Files {

    private static final long LARGE_FILE_LIMIT = 100 * 1024 * 1024;

    /**
     *
     */
    private static final String EMT = "";

    /**
     *
     * @param path
     * @return
     */
    public static boolean exists(String path){
        return path != null && new File(path).exists();
    }

    /**
     *
     * @param f
     * @return
     */
    public static boolean exists(File f){
        return f != null && f.exists();
    }


    /**
     *
     * @param f
     * @return
     */
    public static String name(File f){
        return f == null ? EMT : f.getName();
    }

    /**
     *
     * @param f
     * @return
     */
    public static String name(String f){
        return f == null ? EMT : new File(f).getName();
    }

    /**
     *
     * @param f
     * @return
     */
    public static String ext(File f){
        return f == null ? EMT : ext(f.getName());
    }

    /**
     *
     * @param f
     * @return
     */
    public static String ext(String f){
        return f == null ? EMT : f.lastIndexOf('.') < 0 ? "" : f.substring(f.lastIndexOf('.') + 1);
    }

    /**
     *
     * @param f
     * @return
     */
    public static File dir(File f) {
        if (f == null) {
            return null;
        }
        return f.getParentFile();
    }

    /**
     *
     * @param f
     * @return
     */
    public static File dir(String f) {
        return dir(new File(f));
    }

    /**
     *
     * @param f
     * @return
     */
    public static String dirPath(String f) {
        return dirPath(new File(f));
    }

    /**
     *
     * @param f
     * @return
     */
    public static String dirPath(File f){
        return f == null ? EMT : dir(f).getAbsolutePath();
    }

    /**
     *
     * @param f
     * @return
     */
    public static String nameSfx(File f){
        return f == null ? EMT : nameSfx(f.getName());
    }

    /**
     *
     * @param f
     * @return
     */
    public static String nameSfx(String f){
        if(f == null || f.length() == 0){
            return EMT;
        }
        final String name = name(f);
        final int lastIndex = name.lastIndexOf('.');
        return lastIndex < 0 ? name : name.substring(0,  lastIndex);
    }

    /**
     * 
     * @param f
     * @return
     */
    public static String parent(String f){
        return f == null ? EMT : new File(f).getParent();
    }

    /**
     * 
     * @param f
     * @return
     */
    public static String parent(File f){
        return f == null ? EMT : f.getParent();
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args){
        final String s = "abc";
        final String s1 = "abc.txt";
        final String s2 = "//haha/abc.java";
        final String s3 = "bm..";
        System.out.println(nameSfx(s));
        System.out.println(nameSfx(s1));
        System.out.println(nameSfx(s2));
        System.out.println(nameSfx(s3));
        System.out.println(nameSfx(s3));

    }

    /**
     *
     * @param f
     * @return
     * @throws IOException
     */
    public static String readLines(File f) throws IOException {
        return readLines(f, null);
    }

    /**
     *
     * @param f
     * @param lineConsumer
     * @throws IOException
     */
    public static String readLines(File f, ILineReader lineConsumer) throws IOException {
        if (!exists(f)) {
            throw new FileNotFoundException();
        }
        return readLines(new FileInputStream(f), lineConsumer);
    }

    /**
     *
     * @param f
     * @param lineConsumer
     * @return
     * @throws IOException
     */
    public static String readLines(InputStream f, ILineReader lineConsumer) throws IOException {
        if(f == null){
            throw new NullPointerException();
        }
        StringBuilder sb = new StringBuilder();
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(f))){
            String line;
            while ( (line = reader.readLine()) != null) {
                boolean breaking = false;
                if(lineConsumer != null) {
                    breaking = lineConsumer.onReadLine(line);
                }
                sb.append(line).append("\n");
                if(breaking){
                    break;
                }
            }
            if(sb.length() > 0){
                sb.delete(sb.length() - 1, sb.length());
            }
        }
        return sb.toString();
    }

    public interface ILineReader {
        boolean onReadLine(String currentLine);
    }

    /**
     *
     * @param f
     * @return
     * @throws ReadingTooLargeException
     * @throws InterruptedException
     */
    public static String readWholeFileString(File f) throws ReadingTooLargeException, InterruptedException {
        if (f == null || !f.exists()) return null;
        try {
            byte[] buffer = readBytes(f);
            //noinspection CharsetObjectCanBeUsed
            return new String(buffer, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param f
     *
     * @return
     */
    public static byte[] readBytes(File f) throws ReadingTooLargeException, InterruptedException {
        if (f == null || !f.exists()) return null;
        if (f.length() > LARGE_FILE_LIMIT) {
            throw new ReadingTooLargeException();
        }
        try(FileInputStream is = new FileInputStream(f)) {
            byte[] buffer = new byte[8192];
            byte[] result = new byte[(int) f.length()];
            int read;
            int sum = 0;
            while ((read = is.read(buffer, 0, buffer.length)) != -1) {
                Useless.assertThreadInterruption();
                System.arraycopy(buffer, 0, result, sum, read);
                sum += read;
            }
            if (sum == f.length()) {
                return result;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param f
     * @return
     * @throws InterruptedException
     */
    public static String md5(File f) throws InterruptedException {
        return md5(f, null);
    }

    /**
     *
     * @param f
     * @param consumer
     * @return
     * @throws InterruptedException
     */
    public static String md5(File f, Consumer<Long> consumer) throws InterruptedException {

        if(!exists(f)){
            return null;
        }

        try(FileInputStream fis = new FileInputStream(f)) {

            // set md5-type
            final MessageDigest MD5 = MessageDigest.getInstance("MD5");

            // prepare for reading
            final byte[] buffer = new byte[1024 * 8];
            int readLen;
            long sum = 0L;
            long startTime = System.currentTimeMillis();

            // read & update as md5 bytes
            while ((readLen = fis.read(buffer)) > 0) {
                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException();
                }
                MD5.update(buffer, 0, readLen);
                sum += readLen;
                final long now = System.currentTimeMillis();
                if (now - startTime > 100) {
                    startTime = now;
                    if (consumer != null) {
                        consumer.accept(sum);
                    }
                }
            }
            // flush consumer
            if (consumer != null) {
                consumer.accept(sum);
            }

            // cast bytes to hex-string
            final byte[] src = MD5.digest();
            if (src == null || src.length <= 0) {
                return null;
            }
            final StringBuilder stringBuilder = new StringBuilder();
            for (byte b : src) {
                int v = b & 0xFF;
                stringBuilder.append(String.format("%2s", Integer.toHexString(v)).replace(' ', '0'));
            }

            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param file
     * @return
     */
    public static boolean delete(File file) {
        if (file != null && file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     *
     * @param f
     * @return
     */
    public static boolean delete(String f) {
        if (!Useless.isEmpty(f)) {
            return delete(new File(f));
        }
        return false;
    }

    /**
     *
     * @param f
     * @param data
     * @return
     */
    public static boolean writeBytes(File f, byte[] data) {
        try(FileOutputStream outputStream = new FileOutputStream(f)) {
            outputStream.write(data);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     *
     * @param inputStream
     * @param destFile
     * @param closeInputStream
     * @param cancellationSignal
     * @param sProgressor
     * @throws IOException
     * @throws InterruptedException
     */
    public static boolean writeBytes(InputStream inputStream, File destFile, boolean closeInputStream, CancellationSignal cancellationSignal, SProgressor sProgressor) throws IOException, InterruptedException {
        if (inputStream == null || destFile == null) {
            return false;
        }

        File tempFile = new File(destFile.getParent(), destFile.getName() + ".writting");

        if (tempFile.exists()) {
            if (!tempFile.delete()) {
                if (closeInputStream) {
                    inputStream.close();
                }
                throw new IOException();
            }
        }

        try(FileOutputStream fileOutputStream = new FileOutputStream(tempFile)) {
            final byte[] readBuffer = new byte[8192];
            int nread;
            while ((nread = inputStream.read(readBuffer)) != -1) {
                if (cancellationSignal != null && cancellationSignal.isCanceled() || Thread.currentThread().isInterrupted()) {
                    //noinspection ResultOfMethodCallIgnored
                    destFile.delete();
                    throw new InterruptedException();
                }
                fileOutputStream.write(readBuffer, 0, nread);
                if (sProgressor != null) {
                    sProgressor.updateProgressBy(nread);
                }
            }
        }
        if (closeInputStream) {
            inputStream.close();
        }

        if (!tempFile.renameTo(destFile)) {
            throw new IOException();
        }
        return true;

    }

    /**
     * @param inputStream
     * @param totalLength
     * @param destFile
     * @param bufferSize
     *
     * @return
     *
     * @throws IOException
     */
    @Deprecated
    @SuppressWarnings({"ResultOfMethodCallIgnored", "SynchronizationOnLocalVariableOrMethodParameter"})
    public static boolean writeFileBuffered(InputStream inputStream, long totalLength, File destFile, int bufferSize/*, final DownloadFlag flag*/) throws IOException, InterruptedException {

        if (inputStream == null || destFile == null || totalLength <= 0) {
            return false;
        }

        if (destFile.exists()) {
            return true;
        }

        final File downloadingFile = new File(destFile.getAbsoluteFile() + ".download");
        if (downloadingFile.exists()) {
            downloadingFile.delete();
        }

        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(downloadingFile);
            bufferSize = ((bufferSize % 1024) == 0 && bufferSize > 0) ? bufferSize : 8 * 1024;
            final byte[] data = new byte[bufferSize];
            long progressLength = 0L;

            /*if (flag != null) {
                synchronized (flag) {
                    for (; ; ) {
                        if (flag.caceled) {
                            throw new IOException("canceled");
                        }
                        while (flag.paused) {
                            flag.doWait();
                        }
                        final int nread = inputStream.read(data);
                        if (nread == -1) {
                            break;
                        }
                        outputStream.write(data, 0, nread);
                        progressLength += nread;
                    }
                }
            } else {

            }*/

            for (; ; ) {
                final int nread = inputStream.read(data);
                if (nread == -1) {
                    break;
                }

                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException();
                }

                outputStream.write(data, 0, nread);
                progressLength += nread;
            }

            if (progressLength == totalLength) {
                if (!downloadingFile.renameTo(destFile)) {
                    downloadingFile.delete();
                    throw new IOException();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            /*if(destFile.exists()){
                destFile.delete();
            }*/
            if (downloadingFile.exists()) {
                downloadingFile.delete();
            }
            throw e;
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     *
     * @param f
     * @throws IOException
     */
    public static void createDirectory(File f) throws IOException {
        if (f == null) {
            return;
        }

        if (f.exists()) {
            if (f.isFile()) {
                throw new FileNotFoundException();
            }
        } else if (!f.mkdirs()) {
            throw new IOException();
        }
    }

    /**
     *
     * @param src
     * @param dest
     */
    public static void copy(File src, File dest) {
        if (dest != null && exists(src) && src.isFile()) {
            if (exists(dest)) {
                delete(dest);
            }
            try {
                FileChannel input = new FileInputStream(src).getChannel();
                FileChannel output = new FileOutputStream(dest).getChannel();
                output.transferFrom(input, 0, input.size());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     *
     * @param files
     * @param destPath
     * @return
     */
    public static boolean zip(File[] files, String destPath, CancellationSignal signal) throws InterruptedException, UiThreadProhibitedExcetption, IOException, CancellationException {
        return zip(files, destPath, 8192, signal, null);
    }

    /**
     *
     * @param files
     * @param destPath
     * @return
     */
    public static boolean zip(File[] files, String destPath, CancellationSignal signal, SProgressor progressor) throws InterruptedException, UiThreadProhibitedExcetption, IOException, CancellationException {
        return zip(files, destPath, 8192, signal, progressor);
    }

    /**
     *
     * @param files
     * @param destPath
     * @param bufferSize
     * @return
     */
    public static boolean zip(File[] files, String destPath, int bufferSize, CancellationSignal signal, SProgressor progressor) throws InterruptedException, UiThreadProhibitedExcetption, IOException, CancellationException {

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

    private static boolean zipFiles(ZipOutputStream zipOutputStream, File[] files, byte[] buffer, String curEntryBaseName, CancellationSignal signal, SProgressor progressor) throws CancellationException, InterruptedException, IOException {
        if (Useless.uselessArgs(zipOutputStream, files, buffer, curEntryBaseName)) {
            return false;
        }
        if (files != null && files.length > 0) {
            for (File f : files) {
                if(signal != null && signal.isCanceled()){
                    throw new CancellationException();
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
                                throw new CancellationException();
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


    public static class Ext {
        public static final String PNG = ".png";
    }



    public static boolean mkFile(String path) throws IOException {
        boolean result;
        if (!exists(path)) {
            File f = new File(path);
            result = f.createNewFile();
        } else result = true;
        return result;
    }
}
