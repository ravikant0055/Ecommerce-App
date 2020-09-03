package my.inventive.RozanaFresh;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import my.inventive.RozanaFresh.Adapters.OrderAdapter;
import my.inventive.RozanaFresh.fcm.ServerKey;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class OrderActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<Order> orders,order_for_data;
    String orderId,time;
    String useraddress;
    final int UPI_PAYMENT=0;
    int count=0,totalPrice=0;
    Button placeorder,btnok;
    TextView subtotal,totals,or,select_address,cash_on,self,homed,address_shipping,shippingaddress,shipping_charge;
    RadioGroup radioGroup;
    RadioButton radioButton,ddtt;
    EditText selected_address;
    RelativeLayout addres,deliveryttimees;
    boolean flag=true;
    String new_Address;
    Myhelper myhelper;
    String body;
    SharedPreferences sp=null;
    String formattedDate;
    RadioButton cashOnDelivery,onlinePayment;
    String formattime;
    String notification="https://simplyfied.co.in/groceryapp/sendMail.php";
    private String transactionId="";

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_activity);
        address_shipping=findViewById(R.id.address_shiping);
        shipping_charge=findViewById(R.id.shippingcharge);
        shippingaddress=findViewById(R.id.shippingaddress);
        self=findViewById(R.id.self);
        cashOnDelivery=findViewById(R.id.homebtn);
        onlinePayment=findViewById(R.id.officebtn);
        homed=findViewById(R.id.homed);
        placeorder=findViewById(R.id.placeorder);
        radioGroup=findViewById(R.id.radioGroup);
        myhelper=new Myhelper(this);
        //deliverytype=findViewById(R.id.selectt);
        or=findViewById(R.id.or);
        btnok=findViewById(R.id.btnok);
        selected_address=findViewById(R.id.selected_address);
        select_address=findViewById(R.id.select_your_address);
        cash_on=findViewById(R.id.cashon);
        addres=findViewById(R.id.addresss);
        deliveryttimees=findViewById(R.id.delivery_time);
        orders=new ArrayList<>();
        order_for_data=new ArrayList<>();
        orderId=""+System.currentTimeMillis();
        subtotal=findViewById(R.id.subtotals);
        totals=findViewById(R.id.total);

        Date cc = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        SimpleDateFormat tf=new SimpleDateFormat("HH:mm:ss");
        formattedDate = df.format(cc);
        formattime=tf.format(cc);
        // Toast.makeText(this, ""+formattime, Toast.LENGTH_SHORT).show();

        recyclerView=findViewById(R.id.rerere);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(OrderActivity.this));

        sp=getSharedPreferences("User_Info", MODE_PRIVATE);
        final SharedPreferences shared = getSharedPreferences("User_Info", MODE_PRIVATE);
        final String address=shared.getString("address","");
        useraddress=address;
        SQLiteDatabase database = myhelper.getReadableDatabase();
        String sql = "select * from CUSTOMER";
        Cursor c = database.rawQuery(sql,null);
        while(c.moveToNext()){
            String name =c.getString(1);
            String product_price =c.getString(2);
            String selling_price =c.getString(3);
            int qty = c.getInt(4);
            String seller_id=c.getString(5);
            String item_image=c.getString(6);
            int id = Integer.parseInt(c.getString(7));
            String weight=c.getString(8);
            if(!selling_price.equals(""))
                totalPrice+=(Integer.parseInt(selling_price) * qty);
            Log.d("selling price",selling_price);
            Order item = new Order(item_image,name,"","\u20B9"+product_price,"\u20B9"+selling_price,qty);
            String username=shared.getString("name","");
            String usermobile=shared.getString("mobile","");
            String z=item_image.replace("https://simplyfied.co.in/groceryapp/images/","");
            Order itemss=new Order(id,Integer.parseInt(seller_id),name,selling_price,qty,username,usermobile,useraddress,z,"Recieved",weight);
            order_for_data.add(itemss);
            orders.add(item);
            count++;
        }
        recyclerView.setAdapter(new OrderAdapter(OrderActivity.this,orders));
        subtotal.setText("\u20B9"+totalPrice);
        totals.setText("\u20B9"+(totalPrice+30));

        self.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                totals.setText("\u20B9"+(totalPrice));
                useraddress="Self Pickup";
                flag=false;
                shipping_charge.setText("Rs 0");
                btnok.setVisibility(View.GONE);
                select_address.setVisibility(View.GONE);
                placeorder.setVisibility(View.VISIBLE);
                select_address.setVisibility(View.GONE);
                shippingaddress.setVisibility(View.GONE);
                or.setVisibility(View.GONE);
                select_address.setVisibility(View.GONE);
                cash_on.setVisibility(View.GONE);
                self.setBackgroundColor(Color.parseColor("#800000"));
                self.setTextColor(Color.parseColor("#ffffff"));
                addres.setVisibility(View.GONE);
                deliveryttimees.setVisibility(View.GONE);
                homed.setBackgroundColor(Color.parseColor("#ffffff"));
                homed.setTextColor(Color.parseColor("#000000"));
                deliveryttimees.setVisibility(View.VISIBLE);
                int radioId=radioGroup.getCheckedRadioButtonId();
                radioButton=findViewById(radioId);

            }
        });
        homed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag=true;
                totals.setText("\u20B9"+(totalPrice+30));
                placeorder.setVisibility(View.VISIBLE);
                or.setVisibility(View.VISIBLE);
                shipping_charge.setText("Rs 30");
                select_address.setVisibility(View.VISIBLE);
                shippingaddress.setVisibility(View.VISIBLE);
                useraddress=address;
                shippingaddress.setText(useraddress);
                select_address.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btnok.setVisibility(View.VISIBLE);
                        selected_address.setVisibility(View.VISIBLE);
                        //Toast.makeText(OrderActivity.this, ""+useraddress, Toast.LENGTH_SHORT).show();
                    }
                });
                btnok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        useraddress=selected_address.getText().toString();
                        shippingaddress.setText(useraddress);
                        selected_address.setVisibility(View.GONE);
                        btnok.setVisibility(View.GONE);
                    }
                });
                // cash_on.setVisibility(View.VISIBLE);
                homed.setBackgroundColor(Color.parseColor("#800000"));
                homed.setTextColor(Color.parseColor("#ffffff"));
                self.setBackgroundColor(Color.parseColor("#ffffff"));
                self.setTextColor(Color.parseColor("#000000"));
                addres.setVisibility(View.VISIBLE);
                deliveryttimees.setVisibility(View.VISIBLE);
                int radioId=radioGroup.getCheckedRadioButtonId();
                radioButton=findViewById(radioId);
                //Log.d("Payment type : ",time);
            }
        });
        placeorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(cashOnDelivery.isChecked())
                    time="Cash On Delivery";
                else if(onlinePayment.isChecked())
                    time="Online Payment";
                Log.d("Payment Type : ",time);
                if(time.trim().equals("Online Payment"))
                {
                    String am="";
                    if(flag)
                        am=""+(totalPrice+30);
                    else am=""+totalPrice;
                    String note="Grocery Payment";
                    String name=shared.getString("name","");
                    String upid="7210069362@upi";
                    payUsingUpi(am, upid, name, note);
                }
                else
                {
                    final ProgressDialog pd = new ProgressDialog(OrderActivity.this);
                    pd.setTitle("Uploading Item....");
                    pd.show();
                    HashMap<String,ArrayList<Order>> hm = new HashMap<>();
                    hm.put("order",order_for_data);
                    Gson gson = new Gson();
                    final String jsonData = gson.toJson(hm);

                    // Log.v("JsonData", "onClick: "+jsonData );
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OrderActivity.this);
                    alertDialogBuilder.setMessage("Are you sure,You wanted to Place Order");

                    alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            //Toast.makeText(OrderActivity.this,"You clicked yesbutton",Toast.LENGTH_LONG).show();
                            String url = "https://simplyfied.co.in/groceryapp/proceed_order.php";
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response)
                                {
                                    final ProgressDialog pd = new ProgressDialog(OrderActivity.this);
                                    pd.setTitle("Customer Choice");
                                    pd.setMessage("Please Wait...");
                                    pd.show();
                                    String url = "https://simplyfied.co.in/groceryapp/sendNotification.php";
                                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response)
                                        {
                                            pd.dismiss();
                                            if(response.equals("something wrong"))
                                            {
                                                new androidx.appcompat.app.AlertDialog.Builder(OrderActivity.this).setTitle("Order Placed").setMessage("Order Successfully Placed But Notification Not Sent").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                }).show();
                                            } else
                                            {
                                                JSONObject jsonObject = null;
                                                try {
                                                    jsonObject = new JSONObject(response);
                                                    String sucess = jsonObject.getString("success");
                                                    if(sucess.equals("1"))
                                                    {
                                                        new androidx.appcompat.app.AlertDialog.Builder(OrderActivity.this).setTitle("Order Placed").setMessage("Order Successfully Placed And Notification Has Been Sent").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {

                                                            }
                                                        }).show();
                                                    } else
                                                    {
                                                        new androidx.appcompat.app.AlertDialog.Builder(OrderActivity.this).setTitle("Order Placed").setMessage("Order Successfully Placed But Notification Not Sent").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {

                                                            }
                                                        }).show();
                                                    }    } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            SQLiteDatabase database=myhelper.getWritableDatabase();
                                            database.execSQL("delete from CUSTOMER");
                                            database.close();
                                            Intent intent=new Intent(OrderActivity.this,MainActivity.class);
                                            startActivity(intent);
                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error)
                                        {
                                            pd.dismiss();
                                            Toast.makeText(OrderActivity.this, "" + error, Toast.LENGTH_SHORT).show();
                                            SQLiteDatabase database=myhelper.getWritableDatabase();
                                            database.execSQL("delete from CUSTOMER");
                                            database.close();
                                            Intent intent=new Intent(OrderActivity.this,MainActivity.class);
                                            startActivity(intent);
                                        }
                                    }) {
                                        @Override
                                        protected Map<String, String> getParams() throws AuthFailureError {
                                            Map<String, String> map = new HashMap<>();
                                            map.put("serverKey", ServerKey.SELLER_KEY);
                                            map.put("table","seller");
                                            map.put("userId","27");
                                            map.put("title","Congratulation You Received New Order");
                                            map.put("body","Order Id : "+orderId+"\nOrder Date : "+formattedDate+
                                                    "\nOrder Time : "+formattime+"\nPayment Mode : "+time+"\nMobile Number : "+sp.getString("mobile","")+""+"\nOrder By : "+sp.getString("name","")+""+"\nNo Of Items : "+count
                                                    +"\nTotal Amount : "+totalPrice);
                                            return map;
                                        }
                                    };
                                    RequestQueue requestQueue = Volley.newRequestQueue(OrderActivity.this, new HurlStack());
                                    requestQueue.add(stringRequest);
                                    pd.dismiss();

                                }
                            }   , new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    pd.dismiss();
                                    Toast.makeText(OrderActivity.this, "" + error, Toast.LENGTH_SHORT).show();
                                }
                            }) {
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String, String> map = new HashMap<>();
                                    map.put("allItems",jsonData);
                                    map.put("orderId",orderId);
                                    map.put("userId",shared.getString("id",""));
                                    map.put("name",shared.getString("name",""));
                                    map.put("mobile",shared.getString("mobile",""));
                                    map.put("address",useraddress);
                                    map.put("deliveryTime",time);
                                    map.put("itemCount",""+count);
                                    if (flag)
                                        map.put("totalPrice",""+(totalPrice+30));
                                    else
                                        map.put("totalPrice",""+(totalPrice));
                                    map.put("orderStatus","Received");
                                    map.put("timeOfOrder",""+formattime);
                                    map.put("dateOfOrder",""+formattedDate);
                                    map.put("sellerId","27");
                                    return map;
                                }
                            };
                            RequestQueue requestQueue = Volley.newRequestQueue(OrderActivity.this);
                            requestQueue.add(stringRequest);

                            StringRequest stringRequest1 = new StringRequest(Request.Method.POST, notification, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response)
                                {
                                    pd.dismiss();
                                    Toast.makeText(OrderActivity.this, "Send Mail you will get within a minute", Toast.LENGTH_SHORT).show();
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    pd.dismiss();
                                    Toast.makeText(OrderActivity.this, "" + error, Toast.LENGTH_SHORT).show();
                                }
                            }) {
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String, String> map = new HashMap<>();
                                    map.put("to","tribhuvan98@gmail.com");
                                    map.put("subject","Congratulation You Reiceved New Order");
                                    body="Order Id : "+orderId+"\nOrder Date : "+formattedDate+
                                            "\nOrder Time : "+formattime+"\nPayment Mode : "+time+"\nMobile Number : "+sp.getString("mobile","")+""+"\nOrder By : "+sp.getString("name","")+""+"\nNo Of Items : "+count
                                            +"\nTotal Amount : "+totalPrice;
                                    map.put("message",body);
                                    return map;
                                }
                            };
                            RequestQueue requestQueue1 = Volley.newRequestQueue(OrderActivity.this, new HurlStack());
                            requestQueue1.add(stringRequest1);
                        }
                    });

                    alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {


                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }
        });
    }
    private void payUsingUpi(String am, String upid, String name, String note)
    {
        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upid)
                .appendQueryParameter("pn", name)
                .appendQueryParameter("tn", note)
                .appendQueryParameter("am", am)
                .appendQueryParameter("cu", "INR")
                .build();
        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);

        // will always show a dialog to user to choose an app
        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");

        // check if intent resolves
        if(null != chooser.resolveActivity(getPackageManager())) {
            startActivityForResult(chooser, UPI_PAYMENT);
        } else {
            Toast.makeText(this,"No UPI app found, please install one to continue",Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case UPI_PAYMENT:
                if ((RESULT_OK == resultCode) || (resultCode == 11))
                {
                    Log.e("main",data.toString());
                    Log.e("trxt",data.getStringExtra("response"));

                    if (data != null)
                    {
                        String trxt = data.getStringExtra("response");
                        Log.d("UPI", "onActivityResult: " + trxt);
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add(trxt);
                        upiPaymentDataOperation(dataList);
                    } else {
                        Log.d("UPI", "onActivityResult: " + "Return data is null");
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add("nothing");
                        upiPaymentDataOperation(dataList);
                    }
                } else {
                    Log.d("UPI", "onActivityResult: " + "Return data is null"); //when user simply back without payment
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add("nothing");
                    upiPaymentDataOperation(dataList);
                }
                break;
        }
    }
    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable();
        }
        return false;
    }
    private void upiPaymentDataOperation(ArrayList<String> data)
    {
        if (isConnectionAvailable(this))
        {
            Log.e("Data",data.toString());
            String str = data.get(0);
            Log.d("UPIPAY", "upiPaymentDataOperation: "+str);
            String paymentCancel = "";
            if(str == null) str = "discard";
            String status = "";
            String approvalRefNo = "";
            String[] response = str.split("&");
            for (int i = 0; i < response.length; i++) {
                String[] equalStr = response[i].split("=");
                if(equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                        status = equalStr[1].toLowerCase();
                    }
                    else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                        approvalRefNo = equalStr[1];
                    }
                }
                else {
                    paymentCancel = "Payment cancelled by user.";
                }
            }

            if (status.equals("success"))
            {
                transactionId=approvalRefNo;
                final ProgressDialog pd = new ProgressDialog(OrderActivity.this);
                pd.setTitle("Uploading Item....");
                pd.show();
                HashMap<String,ArrayList<Order>> hm = new HashMap<>();
                hm.put("order",order_for_data);
                Gson gson = new Gson();
                final String jsonData = gson.toJson(hm);
                // Log.v("JsonData", "onClick: "+jsonData );
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OrderActivity.this);
                alertDialogBuilder.setMessage("Are you sure,You wanted to Place Order");

                alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        //Toast.makeText(OrderActivity.this,"You clicked yesbutton",Toast.LENGTH_LONG).show();
                        String url = "https://simplyfied.co.in/groceryapp/proceed_order.php";
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response)
                            {
                                final ProgressDialog pd = new ProgressDialog(OrderActivity.this);
                                pd.setTitle("Customer Choice");
                                pd.setMessage("Please Wait...");
                                pd.show();
                                String url = "https://simplyfied.co.in/groceryapp/sendNotification.php";
                                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response)
                                    {
                                        pd.dismiss();
                                        if(response.equals("something wrong"))
                                        {
                                            new androidx.appcompat.app.AlertDialog.Builder(OrderActivity.this).setTitle("Order Placed").setMessage("Order Successfully Placed But Notification Not Sent").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            }).show();
                                        } else
                                        {
                                            JSONObject jsonObject = null;
                                            try {
                                                jsonObject = new JSONObject(response);
                                                String sucess = jsonObject.getString("success");
                                                if(sucess.equals("1"))
                                                {
                                                    new androidx.appcompat.app.AlertDialog.Builder(OrderActivity.this).setTitle("Order Placed").setMessage("Order Successfully Placed And Notification Has Been Sent").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                        }
                                                    }).show();
                                                } else
                                                {
                                                    new androidx.appcompat.app.AlertDialog.Builder(OrderActivity.this).setTitle("Order Placed").setMessage("Order Successfully Placed But Notification Not Sent").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                        }
                                                    }).show();
                                                }    } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        SQLiteDatabase database=myhelper.getWritableDatabase();
                                        database.execSQL("delete from CUSTOMER");
                                        database.close();
                                        Intent intent=new Intent(OrderActivity.this,MainActivity.class);
                                        startActivity(intent);
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error)
                                    {
                                        pd.dismiss();
                                        Toast.makeText(OrderActivity.this, "" + error, Toast.LENGTH_SHORT).show();
                                        SQLiteDatabase database=myhelper.getWritableDatabase();
                                        database.execSQL("delete from CUSTOMER");
                                        database.close();
                                        Intent intent=new Intent(OrderActivity.this,MainActivity.class);
                                        startActivity(intent);
                                    }
                                }) {
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        Map<String, String> map = new HashMap<>();
                                        map.put("serverKey", ServerKey.SELLER_KEY);
                                        map.put("table","seller");
                                        map.put("userId","27");
                                        map.put("title","Congratulation You Received New Order");
                                        map.put("body","Order Id : "+orderId+"\nOrder Date : "+formattedDate+
                                                "\nOrder Time : "+formattime+"\nPayment Mode : "+time+"\nMobile Number : "+sp.getString("mobile","")+""+"\nOrder By : "+sp.getString("name","")+""+"\nNo Of Items : "+count
                                                +"\nTotal Amount : "+totalPrice);
                                        return map;
                                    }
                                };
                                RequestQueue requestQueue = Volley.newRequestQueue(OrderActivity.this, new HurlStack());
                                requestQueue.add(stringRequest);
                                pd.dismiss();

                            }
                        }   , new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                pd.dismiss();
                                Toast.makeText(OrderActivity.this, "" + error, Toast.LENGTH_SHORT).show();
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> map = new HashMap<>();
                                map.put("allItems",jsonData);
                                map.put("orderId",orderId);
                                map.put("userId",sp.getString("id",""));
                                map.put("name",sp.getString("name",""));
                                map.put("mobile",sp.getString("mobile",""));
                                map.put("address",useraddress);
                                map.put("deliveryTime",time);
                                map.put("itemCount",""+count);
                                if (flag)
                                    map.put("totalPrice",""+(totalPrice+30));
                                else
                                    map.put("totalPrice",""+(totalPrice));
                                map.put("orderStatus","Received");
                                map.put("timeOfOrder",""+formattime);
                                map.put("dateOfOrder",""+formattedDate);
                                map.put("sellerId","27");
                                return map;
                            }
                        };
                        RequestQueue requestQueue = Volley.newRequestQueue(OrderActivity.this);
                        requestQueue.add(stringRequest);

                        StringRequest stringRequest1 = new StringRequest(Request.Method.POST, notification, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response)
                            {
                                pd.dismiss();
                                Toast.makeText(OrderActivity.this, "Send Mail you will get within a minute", Toast.LENGTH_SHORT).show();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                pd.dismiss();
                                Toast.makeText(OrderActivity.this, "" + error, Toast.LENGTH_SHORT).show();
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> map = new HashMap<>();
                                map.put("to","tribhuvan98@gmail.com");
                                map.put("subject","Congratulation You Reiceved New Order");
                                body="Order Id : "+orderId+"\nOrder Date : "+formattedDate+
                                        "\nOrder Time : "+formattime+"\nPayment Mode : "+time+"\nMobile Number : "+sp.getString("mobile","")+""+"\nOrder By : "+sp.getString("name","")+""+"\nNo Of Items : "+count
                                        +"\nTotal Amount : "+totalPrice;
                                map.put("message",body);
                                return map;
                            }
                        };
                        RequestQueue requestQueue1 = Volley.newRequestQueue(OrderActivity.this, new HurlStack());
                        requestQueue1.add(stringRequest1);
                    }
                });

                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {


                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
            else if("Payment cancelled by user.".equals(paymentCancel)) {
                Toast.makeText(this, "Payment cancelled by You", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
        }
    }
    public void checkButton(){
        int radioId=radioGroup.getCheckedRadioButtonId();
        radioButton=findViewById(radioId);
    }
}