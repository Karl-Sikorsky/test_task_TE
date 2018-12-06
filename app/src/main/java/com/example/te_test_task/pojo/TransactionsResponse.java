
package com.example.te_test_task.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TransactionsResponse {

    @SerializedName("sku")
    @Expose
    private String sku;
    @SerializedName("amount")
    @Expose
    private float amount;
    @SerializedName("currency")
    @Expose
    private String currency;

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

}
