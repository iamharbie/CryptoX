package com.intrepid.cryptox;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.intrepid.cryptox.model.Currency;

import java.util.List;

/**
 * Created by Intrepid on 29/10/2017.
 */

public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.ViewHolder> {
    private List<Currency> currenciesList;
    private currencyCardClickListener currencyCardClickListener;

    public CurrencyAdapter(currencyCardClickListener currencyCardClickListener) {
        this.currencyCardClickListener = currencyCardClickListener;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View itemView = layoutInflater.inflate(R.layout.crypto_card_view, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Currency currency = currenciesList.get(position);

        holder.textViewCurrencyName.setText(currency.getName());

        holder.textViewBitcoinValue.setText(String.valueOf(currency.getBitcoinValue()));
        holder.textViewBitcoinValue.setSelected(true);
        holder.textViewEthereumValue.setText(String.valueOf(currency.getEthereumValue()));
    }

    @Override
    public int getItemCount() {
        if( currenciesList == null){
            return 0;
        }else{
        return currenciesList.size();
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView textViewCurrencyName, textViewBitcoinValue, textViewEthereumValue;
        public RelativeLayout viewBackGround;
        public CardView viewForeGround;


        public ViewHolder(View itemView) {
            super(itemView);
            textViewCurrencyName = (TextView) itemView.findViewById(R.id.crypto_cardView_currency);
            textViewBitcoinValue = (TextView) itemView.findViewById(R.id.crypto_card_bitcoin);

            textViewEthereumValue = (TextView) itemView.findViewById(R.id.crypto_card_ethereum);
            viewBackGround = (RelativeLayout) itemView.findViewById(R.id.view_background);
            viewForeGround = (CardView)itemView.findViewById(R.id.crypto_card_foreground);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            int clickedCardPosition = getAdapterPosition();
            currencyCardClickListener.onCurrencyCardClick(clickedCardPosition);
        }
    }

    interface currencyCardClickListener{
        void onCurrencyCardClick(int clickedCardPosition);
    }


    public void removeItem(int position){
        currenciesList.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Currency currency, int position){
        currenciesList.add(position,currency);
        notifyItemInserted(position);
    }

    /**
     *Method to set githubUserlist.
     * Not using a constructor to set it cos we may need to change it wiothout creating a new
     * GitHubUserAdapter
     *
     */
    public void setCurrenciesList(List<Currency> currenciesList){
        this.currenciesList = currenciesList;
        notifyDataSetChanged();
    }
}
