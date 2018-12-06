package com.example.te_test_task.network;

import com.example.te_test_task.pojo.RatesResponse;
import com.example.te_test_task.pojo.TransactionsResponse;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;


public interface MerchService {
    @GET("rates.json")
    Single<List<RatesResponse>> queryRates();

    @GET("transactions.json")
    Single<List<TransactionsResponse>> queryTransactions();

}
