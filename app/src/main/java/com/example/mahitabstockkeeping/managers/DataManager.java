package com.example.mahitabstockkeeping.managers;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.mahitabstockkeeping.managers.interfaces.BaseCallback;
import com.example.mahitabstockkeeping.model.CollectionModel;
import com.example.mahitabstockkeeping.model.ProductModel;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.shopify.buy3.GraphCall;
import com.shopify.buy3.GraphError;
import com.shopify.buy3.GraphResponse;
import com.shopify.buy3.Storefront;
import com.shopify.graphql.support.ID;

import java.util.ArrayList;
import java.util.Observable;

public class DataManager  extends Observable {

    private static final String TAG = DataManager.class.getSimpleName();

    //region Instance
    private Storefront.Checkout mCheckout = null;
    private Storefront.MailingAddress mBillingAddress = null;

    private final Object mLock = new Object();

    private static class InstanceHelper {

        private static final DataManager INSTANCE = new DataManager();
    }


    public synchronized static DataManager getInstance() {
        return InstanceHelper.INSTANCE;
    }

    private DataManager() {
    }
    //endregion

    private GraphClientManager mClientManager;

    public void setClientManager(Context context) {
        mClientManager = new GraphClientManager(context);
    }


    public void products(String collectionID, BaseCallback callback) {
        if(DataManagerHelper.getInstance().getCollections().containsKey(collectionID)) {
            callback.onResponse(BaseCallback.RESULT_OK);
        } else {
            mClientManager.getProducts(new ID(collectionID), new GraphCall.Callback<Storefront.QueryRoot>() {
                @Override
                public void onResponse(@NonNull GraphResponse<Storefront.QueryRoot> response) {
                    if (!response.hasErrors()) {
                        Storefront.Collection collection = (Storefront.Collection) response.data().getNode();
                        Storefront.ProductConnection productConnection = collection.getProducts();

                        if (null == DataManagerHelper.getInstance().getProductsByCollectionID(collectionID) ||
                                DataManagerHelper.getInstance().getProductsByCollectionID(collectionID).size() != productConnection.getEdges().size()) {
                            if (null != DataManagerHelper.getInstance().getProductsByCollectionID(collectionID)) {
                                DataManagerHelper.getInstance().getProductsByCollectionID(collectionID).clear();
                            } else {
                                DataManagerHelper.getInstance().createProductsListForCollectionId(collectionID);
                            }
                        }

                        for (Storefront.ProductEdge edge : productConnection.getEdges()) {
                            DataManagerHelper.getInstance().getProductsByCollectionID(collectionID).add(new ProductModel(edge, collectionID));
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull GraphError error) {
                    callback.onFailure(error.getLocalizedMessage());
                }
            });
        }
    }



    public ArrayList<ProductModel> getAllProducts() {
        return DataManagerHelper.getInstance().getAllProducts();
    }
    public CollectionModel getCollectionByID(String collectionId) {
        return DataManagerHelper.getInstance().getCollections().get(collectionId);
    }

}
