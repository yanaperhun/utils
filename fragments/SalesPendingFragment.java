package com.zinier.entel.ui.fragment.sales;

import android.content.Intent;
import android.os.Bundle;

import com.zinier.base.api.Api;
import com.zinier.base.ui.fragments.SearchableRVAdapter;
import com.zinier.entel.EntelData;
import com.zinier.entel.R;
import com.zinier.entel.api.IEntelApi;
import com.zinier.entel.di.ComponentManager;
import com.zinier.entel.mvp.common.BaseListWithRefreshFragment;
import com.zinier.entel.mvp.models.sales.PendingSale;
import com.zinier.entel.mvp.models.sales.pending.PendingSalesArrayResponse;
import com.zinier.entel.ui.activity.CompleteSaleActivity;
import com.zinier.entel.ui.activity.SaleActivity;
import com.zinier.entel.ui.adapter.SalesListRVAdapter;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.Observable;

import static com.zinier.entel.mvp.models.sales.AnswersCollection.ID;


/**
 * Created by janaperhun on 13.04.17.
 */

public class SalesPendingFragment extends BaseListWithRefreshFragment<PendingSale, PendingSalesArrayResponse> {
    @Inject Api<IEntelApi> api;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ComponentManager.get().getAppComponent().inject(this);
    }

    @Override
    public ArrayList<PendingSale> getArray() {
        return EntelData.get().getPendingSales();
    }

    @Override
    public SearchableRVAdapter<SalesListRVAdapter.VH> createAdapter() {
        return new SalesListRVAdapter(getArray()) {
            @Override
            public void onItemClick(int pos) {
                if (getArray().get(pos).isSuccessfulConfirmation()) {
                    Intent i = new Intent(getActivity(), CompleteSaleActivity.class);
                    i.putExtra(CompleteSaleActivity.ID, getArray().get(pos).getCollectionId());
                    startActivity(i);
                } else if (getArray().get(pos).isPendingSale()) {
                    Intent i = new Intent(getActivity(), SaleActivity.class);
                    i.putExtra(ID, getArray().get(pos).getCollectionId());
                    startActivity(i);
                }
            }
        };
    }

    @Override
    public Observable<PendingSalesArrayResponse> getObservableForLoading() {
        return api.getProjectApi().getPendingSales(currentPage);
    }

    @Override
    public ArrayList<PendingSale> map(PendingSalesArrayResponse data) {
        return data.getData().getSales();
    }

    @Override
    public int getEmptyImageResId() {
        return R.drawable.ic_sales_170;
    }

    @Override
    public int getEmptyStringResId() {
        return R.string.pending_sales_empty_string;
    }
}
