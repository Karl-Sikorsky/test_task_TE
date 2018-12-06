package com.example.te_test_task;

import android.util.Log;

import com.example.te_test_task.network.MerchService;
import com.example.te_test_task.network.RetrofitHelper;
import com.example.te_test_task.pojo.RatesResponse;
import com.example.te_test_task.pojo.TransactionsResponse;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainPresenter implements MerchPresenter {
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private MerchService mMerchService;

    public List<TransactionsResponse> getTransactionsResponses() {
        return transactionsResponses;
    }

    private void setTransactionsResponses(List<TransactionsResponse> transactionsResponses) {
        this.transactionsResponses = transactionsResponses;
    }

    private List<TransactionsResponse> transactionsResponses;
    private MerchView mMerchView;

    MainPresenter(MerchView merchView) {
        this.mMerchView = merchView;
        mMerchService = new RetrofitHelper().getMerchFromApi();
        transactionsResponses = new ArrayList<>();
    }

    private List<RatesResponse> ratesResponses;

    private void setRatesResponses(List<RatesResponse> ratesResponses) {
        this.ratesResponses = ratesResponses;
        updateAvailableKoefMatrix();
    }

    private float[][] koefMatrix = new float[4][4];

    private void updateAvailableKoefMatrix() {
        int row = 0, column = 0;
        for (int i = 0; i < ratesResponses.size(); i++) {
            if (ratesResponses.get(i).getFrom().equals("Copper")) {
                row = 0;
            }
            if (ratesResponses.get(i).getFrom().equals("Bronze")) {
                row = 1;
            }
            if (ratesResponses.get(i).getFrom().equals("Silver")) {
                row = 2;
            }
            if (ratesResponses.get(i).getFrom().equals("Gold")) {
                row = 3;
            }
            if (ratesResponses.get(i).getTo().equals("Copper")) {
                column = 0;
            }
            if (ratesResponses.get(i).getTo().equals("Bronze")) {
                column = 1;
            }
            if (ratesResponses.get(i).getTo().equals("Silver")) {
                column = 2;
            }
            if (ratesResponses.get(i).getTo().equals("Gold")) {
                column = 3;
            }
            koefMatrix[row][column] = ratesResponses.get(i).getRate();
            for (int j = 0; j < 4; j++) {
                for (int g = 0; g < 4; g++) {
                    Log.d("matrix", String.valueOf(koefMatrix[j][g]));
                }
                koefMatrix[j][j] = 1f;
            }
        }
    }

    public void loadRates() {
        mCompositeDisposable.add(mMerchService.queryRates()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<RatesResponse>>() {

                    @Override
                    public void accept(
                            @io.reactivex.annotations.NonNull final List<RatesResponse> results)
                            throws Exception {

                        mMerchView.showRates(results);
                        setRatesResponses(results);

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        mMerchView.showError();
                    }
                })

        );
    }

    public void loadTransactions() {
        mCompositeDisposable.add(mMerchService.queryTransactions()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(new Consumer<List<TransactionsResponse>>() {

                    @Override
                    public void accept(
                            @io.reactivex.annotations.NonNull final List<TransactionsResponse> results)
                            throws Exception {
                        setTransactionsResponses(results);
                        mMerchView.displayTransactions(results);

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        mMerchView.showError();
                    }
                })

        );
    }

//WE NEED TO USE DFS SEARCH FOR RECREATE ALL CURRENCY CHANGES GRAPH.
    public float calculateRateGoldKoef(TransactionsResponse transactionsResponse) {


        if (transactionsResponse.getCurrency().equals("Copper")) {
            return getKoefToGold(0);
        }
        if (transactionsResponse.getCurrency().equals("Bronze")) {
            return getKoefToGold(1);
        }
        if (transactionsResponse.getCurrency().equals("Silver")) {
            return getKoefToGold(2);
        }
        return 1f;
    }

    private float getKoefToGold(int v) {

        boolean[] used = new boolean[16];
        totalKoef = 1f;
        path = new ArrayList<>();
        justDFS(v, used);

        return totalKoef;
    }

    private float totalKoef = 1f;


    private int vNum = 4;
    private List<Integer> path = new ArrayList<>();

    private void justDFS(int v, boolean[] used) {
        used[v] = true;
        path.add(v);
        for (int nv = 0; nv < vNum; nv++) {

            if (!used[nv] && (koefMatrix[v][nv] != 0)) {


                if (nv == 3) {

                    savePathAndCalculateKoef(path);
                } else {

                    justDFS(nv, used);

                }
                if (path.size() > 0) {

                    path.remove(path.size() - 1);
                }
            }

        }

    }

    private void savePathAndCalculateKoef(List<Integer> path) {


        for (int i = 0; i < path.size() - 1; i++) {

            totalKoef = totalKoef * koefMatrix[path.get(i)][path.get(i + 1)];

        }
        totalKoef = totalKoef * koefMatrix[path.get(path.size() - 1)][3];

    }
}
