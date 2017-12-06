package com.zinier.entel.di;

import android.content.Context;

/**
 * Created by janaperhun on 11.10.17.
 */

public class ComponentManager {
    private static ComponentManager ourInstance;
    private static AppComponentHelper appComponentHelper = new AppComponentHelper();

    public static ComponentManager get() {
        if (ourInstance == null) {
            throw new IllegalStateException("Not initialized");
        }
        return ourInstance;
    }

    private ComponentManager() {
    }

    public static void init(Context context) {
        if (ourInstance != null) {
            throw new IllegalStateException("Already initialized");
        }
        ourInstance = new ComponentManager();
        appComponentHelper.build(context);

        ourInstance.appComponent = appComponentHelper.getAppComponent();
    }

    private SaleComponent saleComponent;

    private AppComponent appComponent;

    public SaleComponent getSaleComponent() {
        if (saleComponent == null) {
            saleComponent = appComponent.plusSaleComponent();
        }
        return saleComponent;
    }

    public void clearSaleComponent() {
        saleComponent = null;
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
