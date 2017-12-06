package com.zinier.entel.di;

import com.zinier.entel.di.scopes.PerSale;
import com.zinier.entel.mvp.presenter.CreditEvaluationPresenter;
import com.zinier.entel.mvp.presenter.SaleProcessPresenter;
import com.zinier.entel.mvp.presenter.SearchableLocationPresenter;

import dagger.Subcomponent;

/**
 * Created by janaperhun on 11.10.17.
 */
@Subcomponent(modules = {SaleModule.class})
@PerSale
public interface SaleComponent {

    void inject(SaleProcessPresenter presenter);
    void inject(CreditEvaluationPresenter presenter);
    void inject(SearchableLocationPresenter presenter);

}
