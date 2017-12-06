package com.zinier.entel.di;

import com.zinier.entel.di.scopes.PerSale;
import com.zinier.entel.mvp.managers.SaleManager;

import dagger.Module;
import dagger.Provides;

/**
 * Created by janaperhun on 11.10.17.
 */
@Module
public class SaleModule {

    @Provides
    @PerSale
    SaleManager provideSaleManager() {
        return new SaleManager();
    }

}
