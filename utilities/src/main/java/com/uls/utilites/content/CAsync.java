package com.uls.utilites.content;

import android.os.AsyncTask;

import com.uls.utilites.common.IAccept;
import com.uls.utilites.common.IReject;


/**
 * Created by daiepngfei on 2019-12-02
 */
@Deprecated
public class CAsync {

    public interface ITaskRunnable<T> {
        T run() throws Exception;
    }

    static class Result<T> {
        T t;
        Exception e;
    }

    public static <T> void excute(ITaskRunnable<T> backgroundTask, IAccept<T> accepter){
        excute(backgroundTask, accepter, null);
    }

    public static <T> void excute(ITaskRunnable<T> backgroundTask, IAccept<T> accepter, IReject reject){

        if(backgroundTask == null){
            return;
        }

        final AsyncTask asyncTask = new AsyncTask<Object, Void, Result<T>>(){
            @Override
            protected Result<T> doInBackground(Object... voids) {
                Result<T> r = new Result<>();
                try {
                    r.t = backgroundTask.run();
                } catch (Exception e){
                    r.e = e;
                    e.printStackTrace();
                }
                return r;
            }

            @Override
            protected void onPostExecute(Result<T> t) {
                if(t.e == null){
                    if(accepter != null) {
                        accepter.accept(t.t);
                    }
                } else {
                    if(reject != null) {
                        reject.reject(t.e);
                    } else {
                        if(accepter != null) {
                            accepter.accept(null);
                        }
                    }
                }
            }
        };

        //noinspection unchecked
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

}
