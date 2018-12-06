package com.example.te_test_task;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.te_test_task.pojo.TransactionsResponse;

import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.TransactionViewHolder> {
    MerchView mMerchView;

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {

        TextView merchSku;
        TextView merchAmount;
        TextView merchCurrency;
        ConstraintLayout cardLayout;

        TransactionViewHolder(View itemView) {
            super(itemView);
            itemView.setTag(this);
            cardLayout = (ConstraintLayout) itemView.findViewById(R.id.card_layout);
            merchSku = (TextView) itemView.findViewById(R.id.textViewSku);
            merchAmount = (TextView) itemView.findViewById(R.id.textViewAmount);
            merchCurrency = (TextView) itemView.findViewById(R.id.textViewCurrency);

        }
    }

    List<TransactionsResponse> transactionsResponses;


    RVAdapter(List<TransactionsResponse> transactionsResponses, MerchView merchView) {
        this.transactionsResponses = transactionsResponses;
        this.mMerchView = merchView;
    }


    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.merch_card, viewGroup, false);
        TransactionViewHolder fvh = new TransactionViewHolder(v);

        return fvh;
    }

    @Override
    public void onBindViewHolder(TransactionViewHolder transactionViewHolder, final int position) {
        transactionViewHolder.merchSku.setText("Sku: " + transactionsResponses.get(position).getSku());
        transactionViewHolder.merchAmount.setText("Amount: " + transactionsResponses.get(position).getAmount());
        transactionViewHolder.merchCurrency.setText("Currency: " + transactionsResponses.get(position).getCurrency());


        transactionViewHolder.cardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMerchView.showCurrentProduct(transactionsResponses.get(position).getSku());
            }

        });
    }

    @Override
    public int getItemCount() {
        return transactionsResponses.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {

        super.onAttachedToRecyclerView(recyclerView);
    }
}