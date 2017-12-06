package com.zinier.entel.mvp.common;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.zinier.base.R;
import com.zinier.base.ui.fragments.SearchableRVAdapter;
import com.zinier.base.utils.SystemUtils;
import com.zinier.base.utils.ViewUtils;
import com.zinier.entel.mvp.presenter.BaseListPresenter;
import com.zinier.entel.mvp.view.BaseListView;

import java.util.ArrayList;

import butterknife.BindView;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Created by janaperhun on 13.04.17.
 */

public abstract class BaseListWithRefreshFragment<ElementType, ObservableType> extends MvpFragment
        implements SwipeRefreshLayout.OnRefreshListener, BaseListView<ObservableType> {

    @InjectPresenter BaseListPresenter presenter;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.swipeLayout) SwipeRefreshLayout swipeLayout;
    @BindView(R.id.llEmpty) View llEmpty;
    @BindView(R.id.ivEmpty) ImageView ivEmpty;
    @BindView(R.id.tvEmpty) TextView tvEmpty;
    @BindView(R.id.progressBar) View progressBar;

    private LinearLayoutManager layoutManager;
    protected int currentPage = 1;
    protected boolean hasNextPage;
    protected boolean mIsLoadingData = false;
    protected SearchableRVAdapter adapter;
    protected Parcelable recyclerViewState;
    private Subscription searchSubscription;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = createAdapter();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_with_refresh, container, false);
        bindBaseUI(v);
        return v;
    }

    @Override
    protected void init() {
        super.init();
        initRV();
        initEmptyControls();
        loadData();
        searchSubscription = getBaseActivity().subscribeToSearchEvent()
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        log("search on next");
                        adapter.getFilter().filter(s);
                    }
                });

    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
        checkEmptyList();
        log("BaseListWithRefreshFragment : onResume");
    }


    private void initRV() {
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(listScrollListener);
        recyclerView.setAdapter(adapter);
        swipeLayout.setEnabled(true);
        swipeLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        reloadData();
    }

    @Override
    public void onDataLoaded(ObservableType result) {
        if (currentPage == 1) {
            getArray().clear();
        }
        getArray().addAll(map(result));
        log("size: " + getArray().size());
        adapter.notifyDataSetChanged();
        hasNextPage = getArray().size() % 20 == 0;
        if (hasNextPage) currentPage++;
    }

    public void loadData() {
        if (SystemUtils.isNetworkAvailable(getActivity())) {
            presenter.load(getObservableForLoading(), actionTerminate, actionOnSubscribe);
        } else {
            actionTerminate.call();
        }
    }

    public Action0 actionOnSubscribe = new Action0() {
        @Override
        public void call() {
            mIsLoadingData = true;
            if (currentPage == 1) {
                if (getArray().size() != 0) {
                    swipeLayout.setRefreshing(true);
                } else {
                    swipeLayout.setEnabled(false);
                }
                llEmpty.setVisibility(View.GONE);
                checkHeader();
            }
            ViewUtils.makeVisible(progressBar);
        }
    };

    private void checkHeader() {
        //// TODO: 14.04.17
    }

    public Action0 actionTerminate = new Action0() {
        @Override
        public void call() {
            mIsLoadingData = false;
            swipeLayout.setEnabled(true);
            swipeLayout.setRefreshing(false);
            checkFooter();
            checkHeader();
            checkEmptyList();
            ViewUtils.makeGone(progressBar);
        }
    };

    private void checkEmptyList() {
        ViewUtils.setVisibleOrGone(llEmpty, getArray() == null || getArray().size() == 0);
    }

    private void checkFooter() {
        //// TODO: 14.04.17
    }

    public void reloadData() {
        currentPage = 1;
        loadData();
    }

    private void initEmptyControls() {
        ivEmpty.setImageDrawable(getDrawable(getEmptyImageResId()));
        tvEmpty.setText(getString(getEmptyStringResId()));
    }

    private RecyclerView.OnScrollListener listScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int scrollVertical) {
            super.onScrolled(recyclerView, dx, scrollVertical);
            if (scrollVertical > 0) {
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                if (!mIsLoadingData) {
                    log("has next page "  + currentPage);
                    log("current page "  + hasNextPage);
                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount && hasNextPage) {
                        mIsLoadingData = true;
                        hasNextPage = false;
                        loadData();
                    }
                }
            }
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        searchSubscription.unsubscribe();
    }

    public abstract Observable<ObservableType> getObservableForLoading();

    public abstract ArrayList<ElementType> getArray();

    public abstract ArrayList<ElementType> map(ObservableType data);

    public abstract SearchableRVAdapter createAdapter();

    public abstract int getEmptyImageResId();

    public abstract int getEmptyStringResId();
}
