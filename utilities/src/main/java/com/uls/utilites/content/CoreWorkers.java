package com.uls.utilites.content;

import android.os.AsyncTask;

/**
 * Created by daiepngfei on 1/29/19
 */
public class CoreWorkers<T> extends AsyncTask<Void, T, T>{

    public static <T> CoreWorkers<T> on(Work<T> work){
        CoreWorkers<T> workers = new CoreWorkers<T>();
        workers.work = work;
        return workers;
    }

    public void then(Then<T> then) {
        if(then != null) {
            this.result = then::then;
        }
        if(this.work != null) {
            start();
        }
    }

    public static <T> CoreWorkers<T> exec(Work<T> dealer, IResult<T> result){
        CoreWorkers<T> task = new CoreWorkers<>(dealer, result);
        task.start();
        return task;
    }

    public CoreWorkers() {

    }

    private Work<T> work;
    private IResult<T> result;
    public CoreWorkers(Work<T> dealer, IResult<T> result){
        this.work = dealer;
        this.result = result;
    }

    @Override
    protected T doInBackground(Void... voids) {
        return work != null ? work.work() : null;
    }

    @Override
    protected void onPostExecute(T t) {
        super.onPostExecute(t);
        if(result != null) result.result(t);
    }

    public void start(){
        executeOnExecutor(THREAD_POOL_EXECUTOR, (Void)null);
    }

    public interface Work<T> {
        T work();
    }

    public interface Then<T> {
        void then(T t);
    }

    public interface Otherwise {
        void otherwise(Throwable e);
    }


    public interface IResult<T> {
        void result(T r);
    }
}
