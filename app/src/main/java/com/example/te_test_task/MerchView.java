package com.example.te_test_task;

import com.example.te_test_task.pojo.RatesResponse;
import com.example.te_test_task.pojo.TransactionsResponse;

import java.util.List;

public interface MerchView {
    void showCurrentProduct(String currentSku);

    void showError();

    void displayTransactions(List<TransactionsResponse> results);

    void showRates(List<RatesResponse> results);
}
