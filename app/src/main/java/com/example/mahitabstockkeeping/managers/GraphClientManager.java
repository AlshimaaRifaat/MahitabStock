package com.example.mahitabstockkeeping.managers;

import android.content.Context;

import com.shopify.buy3.GraphCall;
import com.shopify.buy3.GraphClient;
import com.shopify.buy3.HttpCachePolicy;
import com.shopify.buy3.MutationGraphCall;
import com.shopify.buy3.QueryGraphCall;
import com.shopify.buy3.Storefront;
import com.shopify.graphql.support.ID;
import com.shopify.graphql.support.Nullable;

import java.util.UUID;

public final class GraphClientManager {
    private static final String SHOP_DOMAIN = "mahitab.com";
    private static final String API_KEY = "407b2b4dbc9db87e69b1a0116b1fbc87";
    public static final String MERCHANT_ID = "merchant.estore.your.id";
    public static final String PUBLIC_KEY = "BOSJcwUSqSSNm8EnmlKYBRRjeCtPvCJpSkjUxWUjOCl1G5FoI6uIzVi77dA+Rola/LphZDaLVCn7Ttd8OO3Offs=";
    public static final String IDEMPOTENCY_KEY = UUID.randomUUID().toString();

    public static GraphClient mClient;


    GraphClientManager(Context context) {
        mClient = GraphClient.builder(context)
                .shopDomain(SHOP_DOMAIN)
                .accessToken(API_KEY)
                .defaultHttpCachePolicy(HttpCachePolicy.CACHE_FIRST)
                .build();
    }

    void getProducts(ID collectionID, GraphCall.Callback<Storefront.QueryRoot> callback) {
        Storefront.QueryRootQuery query = ClientQuery.queryProducts(collectionID);
        QueryGraphCall call = mClient.queryGraph(query);

        call.enqueue(callback);
    }

}
