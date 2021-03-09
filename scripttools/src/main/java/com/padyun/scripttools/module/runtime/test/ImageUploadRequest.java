package com.padyun.scripttools.module.runtime.test;

import com.padyun.scripttoolscore.compatible.data.model.SEImage;
import com.padyun.scripttoolscore.compatible.data.util.SEModelUtil;
import com.padyun.scripttoolscore.compatible.plugin.FairyProtocol;
import com.padyun.scripttoolscore.compatible.plugin.ImageModule;
import com.padyun.scripttoolscore.compatible.plugin.StringModule;
import com.padyun.scripttoolscore.content.network.SockResponseUtils;
import com.spring.network.socket.compat2.AbsSockTransactionRequest;
import com.spring.network.socket.compat2.CoReader;
import com.spring.network.socket.compat2.CoWriter;
import com.spring.network.socket.compat2.ISockSendResponse;
import com.uls.utilites.io.Files;
import com.uls.utilites.un.Useless;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by daiepngfei on 2020-06-16
 */
public class ImageUploadRequest extends AbsSockTransactionRequest {

    private final List<SEImage> images;
    private ISockSendResponse response;

    ImageUploadRequest(SEImage image, ISockSendResponse response) {
        this.images = new ArrayList<>();
        this.images.add(image);
        this.response = response;
    }

    ImageUploadRequest(List<SEImage> images, ISockSendResponse response) {
        this.images = images;
        this.response = response;
    }

    @Override
    protected void onTransactions(CoWriter writer, CoReader reader) throws Exception {
        if (images != null) {
            // make request
            final StringModule stringModule = new StringModule(FairyProtocol.TYPE_IMAGELIST);
            final Map<String, SEImage> map = new HashMap<>();
            final StringBuilder builder = new StringBuilder();
            Useless.foreach(images, img -> {
                final String path = img.getImageCropPath();
                if (!Files.exists(path)) return;
                String md5 = null;
                try {
                    md5 = Files.md5(new File(path));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                final String name = Files.name(path);
                if (!Useless.noEmptyStr(md5, name)) return;
                builder.append(md5).append(":").append(name).append("\n");
                map.put(name, img);
            });
            stringModule.str = builder.toString();
            // send local image list
            writer.write(stringModule.toDataWithLen().array());
            // handle response
            handleImageListResponse(writer, map, SockResponseUtils.readNextData(reader));
            if (response != null) {
                response.onSendingCompelete();
            }
        } else {
            if (response != null) {
                response.onSendFail(ERR_TYPE_ERROR, "网络错误", new IllegalArgumentException());
            }
        }
    }

    /**
     * @param writer
     * @param map
     * @param data
     *
     * @throws Exception
     */
    private void handleImageListResponse(CoWriter writer, Map<String, SEImage> map, byte[] data) throws Exception {
        if (data != null) {

            final StringModule stringModule1 = new StringModule(data, 2, data.length - 2);
            final BufferedReader bufferedReader = new BufferedReader(new StringReader(stringModule1.str));
            final List<SEImage> uploadImages = new ArrayList<>();
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException();
                }

                final String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }

                final SEImage image = map.get(line);
                if (image != null) {
                    uploadImages.add(image);
                }
            }

            // upload images
            uploadImageFiles(writer, uploadImages);
        }
    }

    /**
     * @param writer
     * @param uploadImages
     *
     * @throws IOException
     */
    static void uploadImageFiles(CoWriter writer, List<SEImage> uploadImages) throws IOException {
        for (SEImage image : uploadImages) {
            if (image == null) {
                continue;
            }
            final String path = image.getImageCropPath();
            if (Useless.isEmpty(path)) {
                continue;
            }
            final File f = new File(path);
            final int filelen = (int) f.length();
            final FileInputStream fileInputStream = new FileInputStream(f);
            final byte[] b = new byte[filelen];
            final int result = fileInputStream.read(b);
            fileInputStream.close();
            if (result > 0) {
                final ImageModule imageModule = SEModelUtil.getImageMoudleFromSEImage(image);
                if (imageModule != null) {
                    imageModule.pic = ByteBuffer.wrap(b);
                    writer.write(imageModule.toDataWithLen().array());
                } else {
                    throw new IOException();
                }
            }
        }
    }
}
