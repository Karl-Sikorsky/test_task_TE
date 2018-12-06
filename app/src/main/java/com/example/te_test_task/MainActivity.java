package com.example.te_test_task;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.te_test_task.pojo.RatesResponse;
import com.example.te_test_task.pojo.TransactionsResponse;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MerchView {

    MerchPresenter mMerchPresenter;




    Button buttonUpdate;

    RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMerchPresenter = new MainPresenter(this);
        rv = (RecyclerView) findViewById(R.id.rv);

        buttonUpdate = (Button) findViewById(R.id.buttonUpdateTransactions);
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMerchPresenter.loadTransactions();
                buttonUpdate.setText(R.string.update_transactions);
            }
        });


        mMerchPresenter.loadRates();
        mMerchPresenter.loadTransactions();

    }

    public void displayTransactions(List<TransactionsResponse> transactionsResponses) {
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

        RVAdapter adapter = new RVAdapter(transactionsResponses, this);
        rv.setAdapter(adapter);

        adapter.notifyDataSetChanged();
    }


    public void showError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Something went wrong");
        builder.setMessage("Check your internet connection and retry");
        builder.setPositiveButton("Retry",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mMerchPresenter.loadTransactions();
                        mMerchPresenter.loadRates();
                    }
                });
        builder.setCancelable(false);
        builder.show();
    }

    public void showRates(List<RatesResponse> results) {
        Log.d("merchLog", "count rates: " + results.size());

    }


    private void showTransactions(List<TransactionsResponse> results) {

        Log.d("merchLog", "count trans: " + results.size());
    }

    @Override
    public void showCurrentProduct(String currentSku) {
        Log.d("currentProd", "show current called");
        List<TransactionsResponse> queryResponses = new ArrayList<>();

        float sum = 0;
        for (int i = 0; i < mMerchPresenter.getTransactionsResponses().size(); i++) {
            if (mMerchPresenter.getTransactionsResponses().get(i).getSku().equals(currentSku)) {
                queryResponses.add(mMerchPresenter.getTransactionsResponses().get(i));
                sum = sum + mMerchPresenter.getTransactionsResponses().get(i).getAmount() / mMerchPresenter.calculateRateGoldKoef(mMerchPresenter.getTransactionsResponses().get(i));

            }
        }
        buttonUpdate.setText(getString(R.string.sum_in_gold) + sum);
        displayTransactions(queryResponses);
    }


}
