package com.example.mahitabstockkeeping;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.example.mahitabstockkeeping.adapter.ProductAdapter;
import com.example.mahitabstockkeeping.managers.DataManager;
import com.example.mahitabstockkeeping.managers.interfaces.BaseCallback;
import com.example.mahitabstockkeeping.model.CollectionModel;
import com.example.mahitabstockkeeping.model.ProductModel;
import com.example.mahitabstockkeeping.model.SelectedOptions;
import com.example.mahitabstockkeeping.search.ColorSuggestion;
import com.example.mahitabstockkeeping.search.DataHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "SearchResultActivity";
    SelectedOptions selectedOptions;
    private MenuItem searchMenuItem = null;
    private ImageView searchCloseIcon, imgCart;

    private ArrayList<ProductModel> searchResultList = null;
    private ArrayList<ProductModel> productList = null;
    private ProductAdapter productAdapter;

    private Toolbar toolbar;
    RecyclerView rvProducts;
    public static int x = 0;
    ArrayList<CollectionModel> allCollectionList = null;
    public static ArrayList<ColorSuggestion> recentlySearchedList;
    private SharedPreferences defaultPreferences;
    FloatingSearchView mSearchView;
    public static final long FIND_SUGGESTION_SIMULATED_DELAY = 250;
    private String mLastQuery = "";
    SharedPreferences.Editor shEditor;



@SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    initView();
    //setArDefaultLocale(this);
    mSearchView.setSearchFocused(true);
    mSearchView.setSearchFocusable(true);


    DataManager.getInstance().setClientManager(this);

    shEditor = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE).edit();
    defaultPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
    if (defaultPreferences.getString("recentlySearchedList", null) == null)
        recentlySearchedList = new ArrayList<>();
    else
        recentlySearchedList = new Gson().fromJson(defaultPreferences.getString("recentlySearchedList", null), new TypeToken<List<ColorSuggestion>>() {
        }.getType());

    Log.d(TAG, "onCreate: " + recentlySearchedList.toString());


    selectedOptions = new SelectedOptions();
    setupFloatingSearch();


}


    private void setupFloatingSearch() {
        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {

                //get suggestions based on newQuery

                //pass them on to the search view
                // mSearchView.swapSuggestions(newQuery);

                /*   */
                x = 1;
                if (newQuery != null) {
                    getSearchResult();
                    selectedOptions.setSearchCriteria(newQuery);


                }
                Log.d(TAG, "list: " + recentlySearchedList.toString());


                if (!oldQuery.equals("") && newQuery.equals("")) {
                    mSearchView.clearSuggestions();
                } else {

                    //this shows the top left circular progress
                    //you can call it where ever you want, but
                    //it makes sense to do it when loading something in
                    //the background.
                    mSearchView.showProgress();

                    //simulates a query call to a data source
                    //with a new query.
                    DataHelper.findSuggestions(MainActivity.this, newQuery, 5,
                            FIND_SUGGESTION_SIMULATED_DELAY, new DataHelper.OnFindSuggestionsListener() {

                                @Override
                                public void onResults(List<ColorSuggestion> results) {

                                    //this will swap the data and
                                    //render the collapse/expand animations as necessary

                                    mSearchView.swapSuggestions(results);

                                    //let the users know that the background
                                    //process has completed
                                    mSearchView.hideProgress();
                                }
                            });
                }

                Log.d(TAG, "onSearchTextChanged()");
            }
        });

        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(final SearchSuggestion searchSuggestion) {

                ColorSuggestion colorSuggestion = (ColorSuggestion) searchSuggestion;
                Log.d(TAG, "onSuggestionClicked: " + colorSuggestion.getBody().toString());
                DataHelper.findColors(MainActivity.this, colorSuggestion.getBody(),
                        new DataHelper.OnFindColorsListener() {

                            @Override
                            public void onResults(ArrayList<ProductModel> searchResultList) {
                                //show search results
                                getSearchResult();
                                selectedOptions.setSearchCriteria(colorSuggestion.getBody());
                                mSearchView.clearSuggestions();
                                mSearchView.setSearchFocusable(false);

                            }

                        });
                Log.d(TAG, "onSuggestionClicked()");

                mLastQuery = searchSuggestion.getBody();
            }

            @Override
            public void onSearchAction(String query) {
                mLastQuery = query;

                DataHelper.findColors(MainActivity.this, query,
                        new DataHelper.OnFindColorsListener() {

                            @Override
                            public void onResults(ArrayList<ProductModel> searchResultList) {
                                //show search results
                                x = 1;
                                getSearchResult();
                                selectedOptions.setSearchCriteria(mLastQuery.toString());


                                recentlySearchedList.add(new ColorSuggestion(mLastQuery));

                                if (!recentlySearchedList.isEmpty()) {
                                    Gson gson = new Gson();
                                    String json = gson.toJson(recentlySearchedList);
                                    shEditor.remove("recentlySearchedList").apply();
                                    shEditor.putString("recentlySearchedList", json);
                                    shEditor.apply();
                                }
                                Log.d(TAG, "list: " + recentlySearchedList.toString());
                            }

                        });
                Log.d(TAG, "onSearchAction()");
            }
        });


        mSearchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {

                //show suggestions when search bar gains focus (typically history suggestions)
                mSearchView.swapSuggestions(DataHelper.getHistory(defaultPreferences, MainActivity.this, 6));

                Log.d(TAG, "onFocus()");

            }

            @Override
            public void onFocusCleared() {

                //set the title of the bar so that when focus is returned a new query begins
                mSearchView.setSearchBarTitle(mLastQuery);

                //you can also set setSearchText(...) to make keep the query there when not focused and when focus returns
                //mSearchView.setSearchText(searchSuggestion.getBody());

                Log.d(TAG, "onFocusCleared()");
            }
        });


        //handle menu clicks the same way as you would
        //in a regular activity


        mSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                //just print action
                x = 1;
                if (item.getItemId() == R.id.action_search) {


                    Log.d(TAG, "query: " + mSearchView.getQuery());
                    getSearchResult();
                    selectedOptions.setSearchCriteria(mSearchView.getQuery());
                    mSearchView.clearSuggestions();
                    mSearchView.setSearchFocusable(false);
                }


            }
        });

        //use this listener to listen to menu clicks when app:floatingSearch_leftAction="showHome"
        mSearchView.setOnHomeActionClickListener(new FloatingSearchView.OnHomeActionClickListener() {
            @Override
            public void onHomeClicked() {

                Log.d(TAG, "onHomeClicked()");
            }
        });

        /*
         * Here you have access to the left icon and the text of a given suggestion
         * item after as it is bound to the suggestion list. You can utilize this
         * callback to change some properties of the left icon and the text. For example, you
         * can load the left icon images using your favorite image loading library, or change text color.
         *
         *
         * Important:
         * Keep in mind that the suggestion list is a RecyclerView, so views are reused for different
         * items in the list.
         */


    }


    private void initView() {
        rvProducts = findViewById(R.id.rvProducts);

        mSearchView = findViewById(R.id.floating_search_view);
    }



    private void getSearchResult() {
        DataManager.getInstance().setClientManager(this);
        DataManager.getInstance().products("Z2lkOi8vc2hvcGlmeS9Db2xsZWN0aW9uLzIzMDU5MTA3MDM3NQ==",
                new BaseCallback() {
                    @Override
                    public void onResponse(int status) {
                        if (status == RESULT_OK) {
                            productList = DataManager.getInstance().getCollectionByID("Z2lkOi8vc2hvcGlmeS9Db2xsZWN0aW9uLzIzMDU5MTA3MDM3NQ==").getPreviewProducts();
                            Log.d(TAG, "productList: "+productList.size());
                        }
                    }

                    @Override
                    public void onFailure(String message) {
                        Log.d(TAG, "onFailure: " + message.toString());
                    }
                });


        searchResultList=DataManager.getInstance().getAllProducts();
        productAdapter = new ProductAdapter(MainActivity.this, searchResultList);
        selectedOptions.addObserver(productAdapter);
        rvProducts.setLayoutManager(new LinearLayoutManager(MainActivity.this,LinearLayoutManager.VERTICAL,false));
        rvProducts.setHasFixedSize(true);
        rvProducts.setAdapter(productAdapter);


    }



}