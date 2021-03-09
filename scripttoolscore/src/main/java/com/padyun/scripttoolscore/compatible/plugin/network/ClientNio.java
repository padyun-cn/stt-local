package com.padyun.scripttoolscore.compatible.plugin.network;

import android.util.Log;

import com.padyun.scripttoolscore.compatible.plugin.LtLog;
import com.padyun.scripttoolscore.compatible.plugin.Utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeoutException;

/**
 * Created by litao on 2017/2/24.
 */
public class ClientNio {
    private static final int MAX_CACHE = 1024 * 1024 * 4;
    private SocketChannel mChannel;
    private int mMaxCache = MAX_CACHE;
    private int mTimeout;
    private final Object mLock = new Object();

    public ClientNio(int timeout) {
        mTimeout = timeout;
    }

    public void setMaxCache(int cache) {
        mMaxCache = cache;
    }

    public boolean connect(String ip, int port) {
        try {
            LtLog.i("connect:" + ip + " port:" + port);
            mChannel = SocketChannel.open();
            System.out.println("kkkk - mChannel");
            synchronized (mLock) {
                System.out.println("kkkk - mLock");
                if(mChannel == null) return false;
                System.out.println("kkkk - mLock2");
                InetSocketAddress inetSocketAddress = new InetSocketAddress(ip, port);
                mChannel.configureBlocking(false);
                mChannel.connect(inetSocketAddress);
                int timeout = mTimeout <= 0 ? 1000 : mTimeout;
                while (timeout > 0) {
                    if (mChannel.finishConnect()) {
                        break;
                    }
                    timeout -= 10;
                    Thread.sleep(10);
                }
                System.out.println("kkkk - mLock4 " + mChannel.isConnected());
                return mChannel.isConnected();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("kkkk -", "mLock5", e);

//            System.out.println("kkkk - mLock5, " + e.getMessage());
        }
        return false;
    }

    public void disconnect() {
        try {
            if (mChannel != null) {
                synchronized (mLock) {
                    System.out.println("kkkk - disconnnect");
//                    Thread.currentThread().interrupt();
                    if(mChannel != null) {
                        mChannel.close();
                    }
                    mChannel = null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int send(ByteBuffer byteBuffer) throws TimeoutException {
        if (mChannel == null) {
            return -1;
        }
        int writeSize = 0;
        synchronized (mLock) {
            if(mChannel == null) return -1;
            if(!mChannel.isConnected()){
                return  -1 ;
            }
            byteBuffer.flip();
            System.out.println("呜呜2 " + byteBuffer.array().length);
            long writeStart = System.currentTimeMillis();
            int timeout = mTimeout;
            while (byteBuffer.hasRemaining()) {
                if (mTimeout > 0 && System.currentTimeMillis() - writeStart >= timeout) {
                    throw new TimeoutException("发送数据超时");
                }
                try {
                    writeSize += mChannel.write(byteBuffer);
                } catch (SocketException e) {
                    try {
                        mChannel.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    return -1;
                } catch (IOException e) {
                    e.printStackTrace();
                    return -1;
                }

            }
        }

        return writeSize;
    }

    public ByteBuffer readPackage() throws TimeoutException, InterruptedException {
        ByteBuffer sizeBuffer = read(4);
        int size = 0;
        if (sizeBuffer != null && sizeBuffer.position() == 4) {
            sizeBuffer.flip();
            size = sizeBuffer.getInt();
        }
        if (size < 0 || size > mMaxCache) {
            LtLog.i("read package error:" + size);
            return null;
        }

        return read(size);
    }

    public ByteBuffer read() throws TimeoutException, InterruptedException {
        if (mChannel == null ) {
            return null;
        }
        ByteBuffer byteBuffer = ByteBuffer.allocate(mMaxCache);
        synchronized (mLock) {
            if(mChannel == null) return null;
            if(!mChannel.isConnected()){
                return  null ;
            }
            long readStart = System.currentTimeMillis();
            while (true) {

                if(!Thread.currentThread().isInterrupted()){
                    throw new InterruptedException();
                }
                try {
                    if (byteBuffer.position() == byteBuffer.capacity()) {
                        if (byteBuffer.position() > mMaxCache) {
                            System.out.println("clientnio buffer error:" + byteBuffer.position());
                            break;
                        }
                        byteBuffer = Utils.expend(byteBuffer);
                        System.out.println("clientnio expend buffer");
                    }
                    int readSize = mChannel.read(byteBuffer);
                    if (readSize < 0) {
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (mTimeout > 0 && System.currentTimeMillis() - readStart >= mTimeout) {
                    throw new TimeoutException("读取数据超时");
                }
            }
        }
        return byteBuffer;
    }

    public ByteBuffer read(int size) throws TimeoutException, InterruptedException {
        if (mChannel == null){
            return null;
        }
        ByteBuffer byteBuffer = ByteBuffer.allocate(size);
        synchronized (mLock) {
            if(mChannel == null) return null;
            if(!mChannel.isConnected()){
                return  null ;
            }
            int readSize = 0;
            long readStart = System.currentTimeMillis();
            try {
                while (readSize < size) {
                    if(Thread.currentThread().isInterrupted()){
                        throw new InterruptedException();
                    }
                    if (mTimeout > 0 && System.currentTimeMillis() - readStart >= mTimeout) {
                        throw new TimeoutException("读取数据超时 read:"+readSize +"request:"+size);
                    }
                    readSize += mChannel.read(byteBuffer);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return byteBuffer;
    }
}
