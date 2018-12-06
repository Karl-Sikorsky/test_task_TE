package com.example.te_test_task;

import com.example.te_test_task.pojo.TransactionsResponse;

import java.util.ArrayList;
import java.util.List;

public interface MerchPresenter {
    void loadRates();

    void loadTransactions();

    float calculateRateGoldKoef(TransactionsResponse transactionsResponse);

    List<TransactionsResponse> getTransactionsResponses();


}
