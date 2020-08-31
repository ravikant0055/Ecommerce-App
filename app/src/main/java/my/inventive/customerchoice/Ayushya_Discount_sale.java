package my.inventive.customerchoice;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Ayushya_Discount_sale extends AppCompatActivity {
    private static final String apiurl="https://simplyfied.co.in/groceryapp/fetchadditem.php";
    ArrayList<AttaAndOtherModel> attaAndOtherModels;
    RecyclerView recyclerView;
    AttaAndOtherAdapter gridItemAdapter;
    ProgressDialog progressDialog;
    int count1=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayushya__discount_sale);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Ayushya Discount Sale");

        recyclerView=findViewById(R.id.recycle_view_item);
        attaAndOtherModels =new ArrayList<>();

        LinearLayoutManager layoutManager= new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        gridItemAdapter=new AttaAndOtherAdapter(this, attaAndOtherModels);
        recyclerView.setAdapter(gridItemAdapter);
        progressDialog=new ProgressDialog(Ayushya_Discount_sale.this, R.style.MyAlertDialogStyle);
        progressDialog.setTitle("Customer Choice");
        progressDialog.setMessage("Please Wait......");

        progressDialog.show();
        StringRequest request=new StringRequest(Request.Method.POST, apiurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // attaAndOtherModels.clear();
                try {
                    //Toast.makeText(AttaAndOtherActivity.this, "" + response, Toast.LENGTH_SHORT).show();
                    JSONObject jsonObject = new JSONObject(response);
                    String sucess = jsonObject.getString("success");
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    if (sucess.equals("1")) {
                        progressDialog.dismiss();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            String category=object.getString("item_category");
                            String seller_id = object.getString("seller_id");
                            if (category.equals("Ayushya Discount Sale") && seller_id.equals("27")) {
                                String id = object.getString("id");
                                String name = object.getString("item_name");
                                String product_price = object.getString("item_mrp");
                                String selling = object.getString("item_outprice");
                                String weight = object.getString("item_weight");
                                String item_descrip = object.getString("item_description");
                                String item_image = object.getString("item_image");
                                String u = "https://simplyfied.co.in/groceryapp/images/" + item_image;
                                attaAndOtherModels.add(new AttaAndOtherModel(u, name, weight, product_price, selling, item_descrip, id, seller_id));
                                gridItemAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Ayushya_Discount_sale.this, ""+error, Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(request);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);

    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(Ayushya_Discount_sale.this, MainActivity.class));
        finish();
    }
}
