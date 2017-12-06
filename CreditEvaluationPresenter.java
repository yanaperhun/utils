package com.zinier.entel.mvp.presenter;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.zinier.base.api.Api;
import com.zinier.entel.api.IEntelApi;
import com.zinier.entel.di.ComponentManager;
import com.zinier.entel.mvp.managers.SaleManager;
import com.zinier.entel.mvp.models.EvaluationResponse;
import com.zinier.entel.mvp.models.coverage.CoverageRequest;
import com.zinier.entel.mvp.models.coverage.CoverageResponse;
import com.zinier.entel.mvp.models.coverage.EvaluationAndCoverageResult;
import com.zinier.entel.mvp.models.sales.CreditEvaluationRequest;
import com.zinier.entel.mvp.view.CreditEvaluationView;

import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by janaperhun on 21.05.17.
 */
@InjectViewState
public class CreditEvaluationPresenter extends MvpPresenter<CreditEvaluationView> {
    @Inject Api<IEntelApi> api;
    @Inject SaleManager saleManager;

    public CreditEvaluationPresenter() {
        ComponentManager.get().getSaleComponent().inject(this);
    }

    public void checkEvaluation(final CreditEvaluationRequest evaluation, final CoverageRequest coverageRequest) {
        api.getProjectApi().checkCreditEvaluation(evaluation)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        getViewState().onDataUploadingStart();
                    }
                })
                .doOnTerminate(new Action0() {
                    @Override
                    public void call() {
                        getViewState().onDataUploadingEnd();
                    }
                })
                .flatMap(new Func1<EvaluationResponse, Observable<CoverageResponse>>() {
                    @Override
                    public Observable<CoverageResponse> call(EvaluationResponse v) {
                        saleManager.getSale().setEvaluated(true);
                        saleManager.getSale().setEvaluationResponse(v);
                        saleManager.getSale().getCreditEvaluationRequest().setWasProccesed(true);

                        return getCoverageObservable(coverageRequest)
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread());
                    }
                })
                .map(new Func1<CoverageResponse, EvaluationAndCoverageResult>() {
                    @Override
                    public EvaluationAndCoverageResult call(CoverageResponse coverageResponse) {
                        saleManager.getSale().setCoverageResponse(coverageResponse);
                        EvaluationAndCoverageResult result = new EvaluationAndCoverageResult();
                        result.setCoverageResponse(coverageResponse);
                        result.setEvaluationResponse(saleManager.getSale().getEvaluationResponse());
                        return result;
                    }
                })
                .subscribe(new Observer<EvaluationAndCoverageResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getViewState().onError(e);
                    }

                    @Override
                    public void onNext(EvaluationAndCoverageResult v) {

                        getViewState().onEvaluationChecked(v);
                    }
                });
    }


    public Observable<CoverageResponse> getCoverageObservable(CoverageRequest coverageRequest) {
        if (coverageRequest.isLocality()) {
            return api.getProjectApi().coverageByLocality(coverageRequest.getRegionId(),
                    coverageRequest.getVillageId(), coverageRequest.getLocalityName());
        } else {
            return api.getProjectApi().coverageByAddress(coverageRequest.getRegionId(),
                    coverageRequest.getVillageId(), coverageRequest.getStreetNumberId());
        }
    }

//    public void coverageByLocation(CoverageRequest coverageRequest) {
//        api.getProjectApi().coverageByLocality(coverageRequest.getRegionId(), coverageRequest.getVillageId(), coverageRequest.getLocalityName())
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .doOnSubscribe(new Action0() {
//                    @Override
//                    public void call() {
//                        getViewState().onDataUploadingStart();
//                    }
//                })
//                .doOnTerminate(new Action0() {
//                    @Override
//                    public void call() {
//                        getViewState().onDataUploadingEnd();
//                    }
//                })
//                .subscribe(new Observer<Object>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        getViewState().onError(e);
//                    }
//
//                    @Override
//                    public void onNext(Object v) {
//
//                    }
//                });
//    }
//
//    public void coverageByAddress(CoverageRequest coverageRequest) {
//        api.getProjectApi().coverageByAddress(coverageRequest.getRegionId(), coverageRequest.getVillageId(), coverageRequest.getStreetNumberId())
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .doOnSubscribe(new Action0() {
//                    @Override
//                    public void call() {
//                        getViewState().onDataUploadingStart();
//                    }
//                })
//                .doOnTerminate(new Action0() {
//                    @Override
//                    public void call() {
//                        getViewState().onDataUploadingEnd();
//                    }
//                })
//                .subscribe(new Observer<Object>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        getViewState().onError(e);
//                    }
//
//                    @Override
//                    public void onNext(Object v) {
//
//                    }
//                });
//    }


}
