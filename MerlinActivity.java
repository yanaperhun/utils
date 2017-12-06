package com.aj.base.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.novoda.merlin.Merlin;
import com.novoda.merlin.MerlinsBeard;
import com.aj.base.R;
import com.aj.base.api.ActionConnectionStatus;
import com.aj.base.utils.ViewUtils;

import java.util.ArrayList;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

public class MerlinActivity extends BaseActivity {

    private static final String TAG = "MerlinActivity";

    protected Merlin merlin;
    protected Observable<Merlin.ConnectionStatus> connectionStatusObservable;
    protected Subscription rxSubscription;
    private ArrayList<Subscription> subscriptions = new ArrayList<>();

    @Nullable
    private View flConnectionState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        merlin = createMerlin();
        connectionStatusObservable = merlin.getConnectionStatusObservable();
    }


    protected Merlin createMerlin() {
        return new Merlin.Builder()
                .withRxCallbacks()
                .withLogging(true)
                .build(this);
    }

    protected Subscription createRxSubscription() {
        return connectionStatusObservable.subscribe(
                new Action1<Merlin.ConnectionStatus>() {
                    @Override
                    public void call(Merlin.ConnectionStatus connectionStatus) {
                        onConnectionChange(getConnectionState());

                        ViewUtils.setVisibleOrGone(flConnectionState, !getConnectionState());
                        if (getConnectionState()) {
                            Log.d(TAG, "rx connected");
                        } else {
                            Log.d(TAG, "rx disconnected");
                        }
                    }
                }
        );
    }

    public void onConnectionChange(boolean isConnected) {}

    public void addConnectionRxSubscription(ActionConnectionStatus status) {
        subscriptions.add(connectionStatusObservable.subscribe(status));
    }

    public void removeConnectionRxSubscription(ActionConnectionStatus status) {
        subscriptions.remove(status);
    }

    @Override
    protected void onStart() {
        super.onStart();
        merlin.bind();
        flConnectionState = findViewById(R.id.flConnectionState);
        if (findViewById(R.id.btnCloseError) != null) {
            findViewById(R.id.btnCloseError).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ViewUtils.makeGone(flConnectionState);
                }
            });
        }
        if (flConnectionState != null) {
            ViewUtils.setVisibleOrGone(flConnectionState, !getConnectionState());
        }
        rxSubscription = createRxSubscription();
    }

    @Override
    protected void onStop() {
        super.onStop();
        merlin.unbind();
        rxSubscription.unsubscribe();
        for (Subscription o : subscriptions) {
            o.unsubscribe();
        }
    }

    public boolean getConnectionState() {
        return MerlinsBeard.from(this).isConnected();
    }

}