package com.intrepid.cryptox.model;

/*
  Created by Intrepid on 29/10/2017.
 */


import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 *
 * USD - US Dollar
 EUR - Euro
 GBP - British Pound
 INR - Indian Rupee
 AUD - Australian Dollar
 CAD - Canadian Dollar
 SGD - Singapore Dollar
 CHF - Swiss Franc
 MYR - Malaysian Ringgit
 JPY - Japanese Yen
 CNY - Chinese Yuan Renminbi

 *
 * */

public class Currency implements Parcelable{
    private String name;
    private double bitcoinValue, ethereumValue;

    public Currency(String name, double bitcoinValue, double ethereumValue) {
        this.name = name;
        this.bitcoinValue = bitcoinValue;
        this.ethereumValue = ethereumValue;
    }

    private  Currency(Parcel in) {
        name = in.readString();
        bitcoinValue = in.readDouble();
        ethereumValue = in.readDouble();
    }

    public static final Creator<Currency> CREATOR = new Creator<Currency>() {
        @Override
        public Currency createFromParcel(Parcel in) {
            return new Currency(in);
        }

        @Override
        public Currency[] newArray(int size) {
            return new Currency[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBitcoinValue() {
        return bitcoinValue;
    }

    public void setBitcoinValue(int bitcoinValue) {
        this.bitcoinValue = bitcoinValue;
    }

    public double getEthereumValue() {
        return ethereumValue;
    }

    public void setEthereumValue(int ethereumValue) {
        this.ethereumValue = ethereumValue;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeDouble(bitcoinValue);
        parcel.writeDouble(ethereumValue);
    }
}
