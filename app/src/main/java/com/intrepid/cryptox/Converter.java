package com.intrepid.cryptox;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.intrepid.cryptox.model.Currency;

public class Converter extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    public static final String CURRENCY = "currency" ;

    private Spinner spinnerConvertFrom;
    private Spinner spinnerConvertTo;

    private TextView valueTextView;
    private TextView titleTextView;
    private EditText valueEditText;
    private double bitcoinValue;
    private double ethereumValue;
    private String currencyName;
    String title;
     private ArrayAdapter<String> spinnerCustomAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converter);

        spinnerConvertFrom = (Spinner) findViewById(R.id.activity_converter_spinner_convert_from);
        spinnerConvertTo = (Spinner) findViewById(R.id.activity_converter_spinner_convert_to);

        spinnerCustomAdapter = new ArrayAdapter<>(this,R.layout.simple_spinner_item);
        spinnerCustomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerConvertFrom.setAdapter(spinnerCustomAdapter);
        spinnerConvertTo.setAdapter(spinnerCustomAdapter);
        spinnerConvertTo.setOnItemSelectedListener(this);
        spinnerConvertFrom.setOnItemSelectedListener(this);
        valueTextView =(TextView) findViewById(R.id.activity_converter_textview_value);
        valueTextView.setSelected(true);
        valueEditText = (EditText) findViewById(R.id.activity_converter_edittext_value);
        titleTextView = (TextView) findViewById(R.id.activity_converter_textview_title);

        Intent intent = getIntent();

        Currency currency = intent.getParcelableExtra(CURRENCY);
        currencyName = currency.getName();
        bitcoinValue = currency.getBitcoinValue();
        ethereumValue = currency.getEthereumValue();

        //getSupportActionBar().setTitle(currencyName + " - " + getSupportActionBar().getTitle());
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spinnerCustomAdapter.add("Bitcoin");
        spinnerCustomAdapter.add("Ethereum");

        spinnerCustomAdapter.add(currencyName);

        spinnerConvertFrom.setSelection(2);
        spinnerConvertTo.setSelection(0);

        title = currencyName +" - Bitcoin";
        titleTextView.setText(title);

        valueEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                beforeConvert(editable);
            }
        });

    }

    private void beforeConvert(Editable editable) {
        if(editable.length()==0 || editable==null){

                valueTextView.setText("Result");

        }
        else if (editable !=null && editable.length() >0){
            String convertFrom = spinnerCustomAdapter.getItem(spinnerConvertFrom.getSelectedItemPosition()).toString();
            String convertTo = spinnerCustomAdapter.getItem(spinnerConvertTo.getSelectedItemPosition()).toString();

            double valueToConvert = Double.parseDouble(valueEditText.getText().toString());
            valueTextView.setText(convert(convertFrom, convertTo, valueToConvert));
        }
    }

    private String convert(String convertFrom,String convertTo, double valueToConvert ){
        String value =null;
        if (convertFrom.equals(convertTo)){
            value=String.valueOf(valueToConvert);
        }


        else if (convertFrom.equals("Ethereum")&& convertTo.equals("Bitcoin")){
            value=String.valueOf(valueToConvert *(ethereumValue/bitcoinValue));
        }
        else if (convertFrom.equals("Bitcoin")&& convertTo.equals("Ethereum")){
            value=String.valueOf(valueToConvert *(bitcoinValue/ethereumValue));
        }
        else if (convertFrom.equals("Bitcoin") && convertTo.equals(currencyName)){
            value=String.valueOf(valueToConvert * bitcoinValue);
        }
        else if (convertFrom.equals("Ethereum") && convertTo.equals(currencyName)){
            value=String.valueOf(valueToConvert * ethereumValue);
        }
        else if (convertFrom.equals(currencyName) && convertTo.equals("Bitcoin")){
            value=String.valueOf(valueToConvert * (1/bitcoinValue));
        }
        else if (convertFrom.equals(currencyName) && convertTo.equals("Ethereum")){
            value=String.valueOf(valueToConvert *(1/ ethereumValue));
        }

        return value;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String convertFrom = spinnerCustomAdapter.getItem(spinnerConvertFrom.getSelectedItemPosition()).toString();
        String convertTo = spinnerCustomAdapter.getItem(spinnerConvertTo.getSelectedItemPosition()).toString();

        title = convertFrom + " - " + convertTo;
        titleTextView.setText(title);
        beforeConvert(valueEditText.getText());
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
