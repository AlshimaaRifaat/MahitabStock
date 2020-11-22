package com.example.mahitabstockkeeping.managers;

import android.util.Log;

import com.example.mahitabstockkeeping.model.CollectionModel;
import com.example.mahitabstockkeeping.model.ProductModel;
import com.shopify.buy3.Storefront;
import com.shopify.graphql.support.ID;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DataManagerHelper {

    private static final String TAG ="DataManagerHelper" ;

    private static class DataManagerWrapper {
        private static final DataManagerHelper INSTANCE = new DataManagerHelper();
    }

    private DataManagerHelper() {
        mCollections = new HashMap<String, CollectionModel>();

        mProductsByCollection = new HashMap<String, ArrayList<ProductModel>>();

    }

    public static DataManagerHelper getInstance() {
        return DataManagerWrapper.INSTANCE;
    }

    private static final Object mLock = new Object();

    private HashMap<String, CollectionModel> mCollections;
    private HashMap<String, ArrayList<ProductModel>> mProductsByCollection;




    public HashMap<String, CollectionModel> getCollections() {
        Log.d(TAG, "getCollections: "+mCollections);
        return mCollections;
    }

    public ArrayList<ProductModel> getProductsByCollectionID(String collectionID) {
        return mProductsByCollection.get(collectionID);
    }


    public void createProductsListForCollectionId(String collectionID) {
        mProductsByCollection.put(collectionID, new ArrayList<ProductModel>());
    }

    public ProductModel getProductByID(String productID) {
        ProductModel product = null;
        for (Map.Entry<String, ArrayList<ProductModel>> entry : mProductsByCollection.entrySet()) {
            for (ProductModel p : entry.getValue()) {
                if (ID.equals(p.getID(), new ID(productID))) {
                    product = p;
                    break;
                }
            }
        }

        return product;
    }

    public ArrayList<ProductModel> getAllProducts() {
        ArrayList<ProductModel> products = new ArrayList<ProductModel>();
        Log.d(TAG, "getAllProducts: "+mProductsByCollection.size()+"");
        for (Map.Entry<String, ArrayList<ProductModel>> entry : mProductsByCollection.entrySet()) {
            for (ProductModel p : entry.getValue()) {
                products.add(p);
            }
        }
        return products;
    }

    public ProductModel.ProductVariantModel getVariantByID(String variantID) {
        for (Map.Entry<String, ArrayList<ProductModel>> entry : mProductsByCollection.entrySet()) {
            for (ProductModel product : entry.getValue()) {
                for (ProductModel.ProductVariantModel variant : product.getVariants()) {
                    if (!ID.equals(variant.getID(), new ID(variantID))) {
                        continue;
                    }

                    return variant;
                }
            }
        }
        return null;
    }








}
