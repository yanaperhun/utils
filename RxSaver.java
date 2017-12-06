package ai.pokup.app.mvp.common;

import android.content.Context;

import java.io.File;
import java.io.IOException;

import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

public class RxSaver implements Func1<Response<ResponseBody>, Observable<File>> {
    private Context context;

    public RxSaver(Context context) {
        this.context = context;
    }

    @Override
    public Observable<File> call(Response<ResponseBody> responseBodyResponse) {
        return saveToDiskRx(responseBodyResponse);
    }

    private Observable<File> saveToDiskRx(final Response<ResponseBody> response) {
        return Observable.create(new Observable.OnSubscribe<File>() {
            @Override
            public void call(Subscriber<? super File> subscriber) {
                try {
                    String header = response.headers().get("Content-Disposition");
                    String filename = "cities.json.gz";
                    File destinationFile = new File(context.getFilesDir(), filename);

                    BufferedSink bufferedSink = Okio.buffer(Okio.sink(destinationFile));
                    bufferedSink.writeAll(response.body().source());
                    bufferedSink.close();
                    subscriber.onNext(destinationFile);
                    subscriber.onCompleted();
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        });
    }
}
