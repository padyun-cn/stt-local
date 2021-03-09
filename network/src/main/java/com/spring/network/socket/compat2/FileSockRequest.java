package com.spring.network.socket.compat2;

import com.uls.utilites.un.Useless;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by daiepngfei on 11/11/19
 */
@SuppressWarnings("unused")
public class FileSockRequest extends AbsBaseRDRequest {

    private List<String> pathes = new ArrayList<>();

    public FileSockRequest(){}

    public FileSockRequest(String... path){
        addParams(path);
    }

    public void addParams(String... path) {
        Useless.foreach(path, p -> pathes.add(p));
    }

    @Override
    public final void onWriting(CoWriter writer) throws Exception {
        for(String path : pathes){
            File f = new File(path);
            int filelen = (int) f.length();
            FileInputStream fileInputStream = null;
            fileInputStream = new FileInputStream(f);
            byte b[] = new byte[filelen];
            //noinspection ResultOfMethodCallIgnored
            fileInputStream.read(b);
            fileInputStream.close();
            onFileWriting(writer, b);
        }
    }

    protected void onFileWriting(CoWriter writer, byte[] data) throws IOException {
        writer.write(data);
    }
}
