package com.example.te_test_task;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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

public class MainActivity extends AppCompatActivity implements MerchView {
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private MerchService mMerchService;

    public List<TransactionsResponse> getTransactionsResponses() {
        return transactionsResponses;
    }

    public void setTransactionsResponses(List<TransactionsResponse> transactionsResponses) {
        this.transactionsResponses = transactionsResponses;
    }

    private List<TransactionsResponse> transactionsResponses;

    public List<RatesResponse> getRatesResponses() {
        return ratesResponses;
    }

    public void setRatesResponses(List<RatesResponse> ratesResponses) {
        this.ratesResponses = ratesResponses;
        updateAvailableKoefMatrix();
    }
    Button buttonUpdate;
    private List<RatesResponse> ratesResponses;
RecyclerView rv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rv = (RecyclerView) findViewById(R.id.rv);
        transactionsResponses = new ArrayList<>();
        buttonUpdate = (Button)findViewById(R.id.buttonUpdateTransactions);
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadTransactions();
                buttonUpdate.setText("update transactions");
            }
        });
        mMerchService = new RetrofitHelper().getMerchFromApi();
        loadRates();
        loadTransactions();
    }

    private void displayTransactions(List<TransactionsResponse> transactionsResponses) {
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

        RVAdapter adapter = new RVAdapter(transactionsResponses, this);
        rv.setAdapter(adapter);

        adapter.notifyDataSetChanged();
    }

    public void loadRates(){
        mCompositeDisposable.add(mMerchService.queryRates()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<RatesResponse>>() {

                    @Override
                    public void accept(
                            @io.reactivex.annotations.NonNull final List<RatesResponse> results)
                            throws Exception {

                        showRates(results);
                        setRatesResponses(results);

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        showError();
                    }
                })

        );
    }

    private void showError() {
    }

    private void showRates(List<RatesResponse> results) {
        Log.d("merchLog", "count rates: "+results.size());

    }

    public void loadTransactions(){
        mCompositeDisposable.add(mMerchService.queryTransactions()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(new Consumer<List<TransactionsResponse>>() {

                    @Override
                    public void accept(
                            @io.reactivex.annotations.NonNull final List<TransactionsResponse> results)
                            throws Exception {
                        setTransactionsResponses(results);
                        displayTransactions(results);

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        showError();
                    }
                })

        );
    }

    private void showTransactions(List<TransactionsResponse> results) {

        Log.d("merchLog", "count trans: "+results.size());
    }

    @Override
    public void showCurrentProduct(String currentSku) {
Log.d("currentProd", "show current called");
List<TransactionsResponse> queryResponses = new ArrayList<>();
 float sum = 0;
         for (int i = 0; i< transactionsResponses.size(); i++){
             if(transactionsResponses.get(i).getSku().equals(currentSku)){
                 queryResponses.add(transactionsResponses.get(i));
                 sum=sum+transactionsResponses.get(i).getAmount()/calculateRateGoldKoef(transactionsResponses.get(i));
                 float forlog = transactionsResponses.get(i).getAmount()/calculateRateGoldKoef(transactionsResponses.get(i));
                 Log.d("totalRateToGold",String.valueOf(forlog));
             }
         }
         buttonUpdate.setText("ALL SUM IN GOLD CURRENCY = "+sum);
         displayTransactions(queryResponses);
    }
    float[][] koefMatrix = new float[4][4];
void updateAvailableKoefMatrix(){
int row =0, column = 0;
        for (int i=0;i<ratesResponses.size();i++){
            if(ratesResponses.get(i).getFrom().equals("Copper")){
                row = 0;
            }
            if(ratesResponses.get(i).getFrom().equals("Bronze")){
                row = 1;
            }
            if(ratesResponses.get(i).getFrom().equals("Silver")){
                row = 2;
            }
            if(ratesResponses.get(i).getFrom().equals("Gold")){
                row = 3;
            }
            if(ratesResponses.get(i).getTo().equals("Copper")){
                column = 0;
            }
            if(ratesResponses.get(i).getTo().equals("Bronze")){
                column = 1;
            }
            if(ratesResponses.get(i).getTo().equals("Silver")){
                column = 2;
            }
            if(ratesResponses.get(i).getTo().equals("Gold")){
                column = 3;
            }
            koefMatrix[row][column]=ratesResponses.get(i).getRate();
            for(int j=0;j<4;j++){
                for (int g = 0; g<4;g++){
                    Log.d("matrix",String.valueOf(koefMatrix[j][g]));
                }
                koefMatrix[j][j]=1f;
            }
        }
}
    private float calculateRateGoldKoef(TransactionsResponse transactionsResponse) {



      if(transactionsResponse.getCurrency().equals("Copper")){
          return getKoefToGold(0);
      }
        if(transactionsResponse.getCurrency().equals("Bronze")){
            return getKoefToGold(1);
        }
        if(transactionsResponse.getCurrency().equals("Silver")){
            return getKoefToGold(2);
        }
return 1f;
    }
    float totalKoef = 1f;
float getKoefToGold(int v) {
    // количество вершин
    // количество ребер
    boolean[] used = new boolean[16]; // массив пометок
    totalKoef = 1f;
     path = new ArrayList<>();
    justDFS(v, used);
    Log.d("KOEFS","totalKoef for "+String.valueOf(v)+" is "+totalKoef);
    return totalKoef;
}
    int vNum = 4;
    List<Integer> path = new ArrayList<>();
    void justDFS ( int v, boolean[] used){
        used[v] = true; // помечаем вершину
        path.add(v);
        for (int nv = 0; nv < vNum; nv++){
            int countVisited = 0;// перебираем вершины
            if (!used[nv] && (koefMatrix[v][nv] != 0)) { // если вершина не помечена, и смежна с текущей

                Log.d("addPath", "for "+v+ " add to path "+nv);

                Log.d("totalKoef", "multiply to "+ koefMatrix[v][nv]);
                if (nv == 3) {
                    Log.d("addPath", "call calculate with path size = "+path.size());
                 savePathAndCalculateKoef(path);
                }else {

                    justDFS(nv, used); // рекурсивно запускаем от нее DFS

                }
                if(path.size()>0) {
                    Log.d("addPath", "for "+v+ " remove from path "+path.get(path.size()-1));
                    path.remove(path.size() - 1);
                }
            }

        }

}

    private void savePathAndCalculateKoef(List<Integer> path) {
        for (int i = 0;i<path.size();i++){
            Log.d("pathTOCALC", path.get(i).toString());
        }
        Log.d("pathTOCALC", "------");
        for (int i = 0;i<path.size()-1;i++){
            Log.d("pathKOEF", path.get(i).toString());
            totalKoef = totalKoef*koefMatrix[path.get(i)][path.get(i+1)];

        }
        totalKoef = totalKoef*koefMatrix[path.get(path.size()-1)][3];
        Log.d("pathKOEF", "total koef = "+ totalKoef);
    }
}
