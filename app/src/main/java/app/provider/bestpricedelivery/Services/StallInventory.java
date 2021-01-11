package app.provider.bestpricedelivery.Services;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import app.provider.bestpricedelivery.Constants.Constants;
import app.provider.bestpricedelivery.GlobalAppApis;
import app.provider.bestpricedelivery.R;
import app.provider.bestpricedelivery.retrofit.ApiService;
import app.provider.bestpricedelivery.retrofit.ConnectToRetrofit;
import app.provider.bestpricedelivery.retrofit.RetrofitCallBackListenar;
import retrofit2.Call;

import static app.provider.bestpricedelivery.retrofit.API_Config.getApiClient_ByPost;

public class StallInventory extends AppCompatActivity implements View.OnClickListener {
    Activity mActivity;
    LinearLayout inventoryRowLL;
    ImageView backImg;
    TextView headerTxt, updateInventoryTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stall_inventory);
        mActivity = this;
        initialize();
        getInventoryDetails();
    }

    String stall_id = "";

    void initialize() {
        backImg = findViewById(R.id.backImg);
        headerTxt = findViewById(R.id.headerTxt);
        inventoryRowLL = findViewById(R.id.inventoryRowLL);
        updateInventoryTv = findViewById(R.id.updateInventoryTv);
        headerTxt.setText("My Inventory");
        updateInventoryTv.setOnClickListener(this);
        backImg.setOnClickListener(this);
    }

    void addRows(ArrayList<JSONObject> resultList) {
        inventoryRowLL.removeAllViews();
        for (int i = 0; i < resultList.size(); i++) {
            try {
                View mView = LayoutInflater.from(mActivity).inflate(R.layout.inventory_row, null);
                TextView productName = mView.findViewById(R.id.productName);
                TextView productPrice = mView.findViewById(R.id.productPrice);
                EditText quantity = mView.findViewById(R.id.quantity);
                productName.setText(resultList.get(i).getString("dish_name"));
                productPrice.setText(getResources().getString(R.string.rupee) + resultList.get(i).getString("restaurant_price"));
//                quantity.setText(resultList.get(i).getString("quantity"));
//                quantity.setTag(resultList.get(i).getJSONObject("product").getString("id"));
                inventoryRowLL.addView(mView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setData(JSONObject resultJSON) {
        try {
            ArrayList<JSONObject> resultList = new ArrayList<>();
            JSONArray json = resultJSON.getJSONArray("data");
            for (int i = 0; i < json.length(); i++) {
                resultList.add(json.getJSONObject(i));
            }
            if (resultList.size() > 0) {
                addRows(resultList);
//                createContent(resultList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getInventoryDetails() {
        String data = new GlobalAppApis().getInventory( Constants.getTenderId(mActivity));
        ApiService client = getApiClient_ByPost();
        Call<String> call = client.getInventoryList(Constants.getAPIToken(mActivity), data);
        new ConnectToRetrofit(new RetrofitCallBackListenar() {
            @Override
            public void RetrofitCallBackListenar(String result, String action) throws JSONException {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.getBoolean("status")) {
                    setData(jsonObject);
                } else {
                    Toast.makeText(mActivity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                }

            }
        }, mActivity, call, "", true);


    }

    ArrayList<JSONObject> currentProductList = new ArrayList<>();
    ArrayList<JSONObject> updatedProductList = new ArrayList<>();

    void createContent(ArrayList<JSONObject> resultList) {
        try {
            for (int i = 0; i < resultList.size(); i++) {
                JSONObject resultJSON = new JSONObject();
                resultJSON.put("stall_id", stall_id);
                resultJSON.put("product_id", resultList.get(i).getJSONObject("product").getString("id"));
                resultJSON.put("quantity", resultList.get(i).getString("quantity"));
                currentProductList.add(resultJSON);
            }
            System.out.println("==CURRENT PRODUCT LIST" + currentProductList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    int totalCount = 0;

    void getUpdatedContent() {
        try {
            int totalChildCount = inventoryRowLL.getChildCount();
            for (int i = 0; i < totalChildCount; i++) {
                View mView = inventoryRowLL.getChildAt(i);
                JSONObject resultJSON = new JSONObject();
                TextView quantity = mView.findViewById(R.id.quantity);
                resultJSON.put("stall_id", stall_id);
                resultJSON.put("product_id", quantity.getTag().toString());
                resultJSON.put("quantity", quantity.getText().toString());
                updatedProductList.add(resultJSON);
            }
            System.out.println("==UPDATED PRODUCT LIST" + updatedProductList);
            if (updatedProductList.size() > 0) {
                sendUpdatedData();
            } else {
                Toast.makeText(mActivity, "No product(s) to update.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ArrayList<JSONObject> productList = new ArrayList<>();

    void sendUpdatedData() {
        try {

            for (int j = 0; j < updatedProductList.size(); j++) {
                System.out.println("==CURRENT LIST" + currentProductList.get(j));
                System.out.println("==UPDATED LIST" + updatedProductList.get(j));
                if (!currentProductList.get(j).getString("quantity")
                        .equalsIgnoreCase(updatedProductList.get(j).getString("quantity"))) {
                    productList.add(updatedProductList.get(j));
//                            updateProductDetails(updatedProductList.get(i).toString());

                }
            }
            totalCount = productList.size();
            System.out.println("==PRODUCT LIST " + productList);
            startUpload(uploadCount);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void startUpload(int pos) {
        try {
            if (pos < totalCount) {
                updateProductDetails(productList.get(pos).toString());

            } else {
                Toast.makeText(mActivity, "Product(s) updated successfully.", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    int uploadCount = 0;

    public void updateProductDetails(String productList) {
        String data = productList;
        ApiService client = getApiClient_ByPost();
        Call<String> call = client.putUpdatedProducts(Constants.getAPIToken(mActivity), data);
        new ConnectToRetrofit(new RetrofitCallBackListenar() {
            @Override
            public void RetrofitCallBackListenar(String result, String action) throws JSONException {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.getBoolean("status")) {

                    startUpload(++uploadCount);
                } else {
                    Toast.makeText(mActivity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                }

            }
        }, mActivity, call, "", true);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.updateInventoryTv:
                getUpdatedContent();
                break;
            case R.id.backImg:
                finish();
                break;
        }
    }
}
