package com.intrepid.cryptox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.CheckBoxPreference;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.ArrayMap;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.intrepid.cryptox.model.Currency;
import com.intrepid.cryptox.util.AppController;
import com.intrepid.cryptox.util.RecyclerItemTouchHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener,
        RecyclerItemTouchHelper.RecyclerItemTouchHelperListener, CurrencyAdapter.currencyCardClickListener,SwipeRefreshLayout.OnRefreshListener {

    public static final String BASE_API_URL = "https://min-api.cryptocompare.com/data/pricemulti?fsyms=BTC,ETH&tsyms=";

    private List<String> shownCurrencies;

   // private android.support.v4.util.ArrayMap<String,Boolean> mapOfPreferences;

    private Map<String,?> mapOfPreferences;

    private List<Currency> currenciesList;
    private RecyclerView recyclerView;
    private CurrencyAdapter currencyAdapter;
    private TextView errorMessageTextView;
    //private ProgressBar progressBar;
    private  SharedPreferences sharedPreferences;
    private SwipeRefreshLayout swipeRefreshLayout;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);





        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.activity_main_recyclerView);
        currencyAdapter = new CurrencyAdapter(this);
        errorMessageTextView = (TextView) findViewById(R.id.activity_main_error_message);
        //progressBar = (ProgressBar) findViewById(R.id.activity_main_progressBar);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        currenciesList = new ArrayList<>();
        shownCurrencies = new ArrayList<>();

        mapOfPreferences = new android.support.v4.util.ArrayMap<>();

        swipeRefreshLayout.setRefreshing(true);
        getCurrenciesToBeShown();

        sendJsonRequest(buildApiUrl());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(currencyAdapter);
        recyclerView.setHasFixedSize(true);


        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);





    }

    private String buildApiUrl() {
        StringBuilder API_URLBuilder = new StringBuilder(BASE_API_URL);
        for (String currency: shownCurrencies) {
           API_URLBuilder.append(currency).append(",");

        }
        String API_URL = API_URLBuilder.toString();
        Log.v("--API_URL",API_URL);
        return API_URL;
    }

    private void getCurrenciesToBeShown() {
        mapOfPreferences.clear();
        shownCurrencies.clear();
        Log.v("--getCurrencies..()","getCurrenciesToBeShown() called");



        mapOfPreferences = sharedPreferences.getAll();
        if (mapOfPreferences==null){
            Log.v("mapOfPreference","mapOfPreferences is null");
        }else {
            Log.v("mapOfPreference","mapOfPreferences is not null");

            Log.v("--loop","about to enter loop");
            for (ArrayMap.Entry<String, ?> entry : mapOfPreferences.entrySet()) {

                Log.v("List of preferecences", entry.getKey() + " :" + entry.getValue().toString());

                if (entry.getValue().toString().equals("true")) {
                    shownCurrencies.add(entry.getKey());
                }

            }
        }

        for (String cur: shownCurrencies) {
            Log.v("shownCurrencies:",cur);

        }



    }


    public void sendJsonRequest(String url){
        swipeRefreshLayout.setRefreshing(true);

        showCurrenciesList();
        //Log.d(this.getClass().getSimpleName(),"sendJsonMethod called ...");
        JsonObjectRequest request = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {


            @Override
            public void onResponse(JSONObject response) {
                //Log.d(this.getClass().getSimpleName(),response.toString());
                //progressBar.setVisibility(View.INVISIBLE);
                parseJson(response);

                currencyAdapter.setCurrenciesList(currenciesList);

                    swipeRefreshLayout.setRefreshing(false);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(MainActivity.class.getSimpleName(),error.toString());
                //progressBar.setVisibility(View.INVISIBLE);

                    swipeRefreshLayout.setRefreshing(false);

                showErrorMessage();
            }
        }) ;

        AppController.getInstance().addToRequestQueue(request);


    }

    private void parseJson(JSONObject response) {
        currenciesList.clear();

        if (response != null){

            try {
                JSONObject bitcoin = response.getJSONObject("BTC");
                JSONObject ethereum = response.getJSONObject("ETH");
                for (int i = 0; i<=bitcoin.names().length();i++){
                    String currencyName = bitcoin.names().getString(i);
                    double currencyBitcoinValue = Double.parseDouble(bitcoin.get(currencyName).toString());
                    double currencyEthereumValue = Double.parseDouble(ethereum.get(currencyName).toString());



                    currenciesList.add(new Currency(currencyName,currencyBitcoinValue,currencyEthereumValue));
                }




            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }


    private void showCurrenciesList() {
        errorMessageTextView.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        recyclerView.setVisibility(View.INVISIBLE);
        errorMessageTextView.setVisibility(View.VISIBLE);
    }






    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        getCurrenciesToBeShown();

        sendJsonRequest(buildApiUrl());



    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        final SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();


            if (viewHolder instanceof CurrencyAdapter.ViewHolder) {

            // get the removed item name to display it in snack bar
            final String name = currenciesList.get(viewHolder.getAdapterPosition()).getName();
                // backup of removed item for undo purpose
            final Currency deletedItem = currenciesList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

                // remove the item from recycler view
                currencyAdapter.removeItem(viewHolder.getAdapterPosition());
            if(name.equals("USD") || name.equals("NGR") || name.equals("EUR")){
                currencyAdapter.restoreItem(deletedItem, deletedIndex);

            }else {


                sharedPreferencesEditor.putBoolean(name, false).apply();

                // showing snack bar with Undo option

                Snackbar snackbar = Snackbar
                        .make(findViewById(android.R.id.content), name + " removed from currencies list!", Snackbar.LENGTH_LONG);

                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // undo is selected, restore the deleted item
                        currencyAdapter.restoreItem(deletedItem, deletedIndex);
                        sharedPreferencesEditor.putBoolean(name, true).apply();
                    }
                });
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();

            }

        }
    }

    @Override
    public void onCurrencyCardClick(int clickedCardPosition) {

        // TODO: this get is currently returning -1 //DONE
        Currency currency = currenciesList.get(clickedCardPosition);
        Intent intent = new Intent(this,Converter.class);
        intent.putExtra(Converter.CURRENCY,currency);
        startActivity(intent);



    }


    public void onClickFloatingActionButton(View view) {
        startActivity(new Intent(this,AddCurrencies.class));

    }

    @Override
    public void onRefresh() {
        Log.v("--OnRefresh","Onrefresh() called");
        showCurrenciesList();
        getCurrenciesToBeShown();

        sendJsonRequest(buildApiUrl());
    }
}
