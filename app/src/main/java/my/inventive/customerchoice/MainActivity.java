package my.inventive.customerchoice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.GridView;
import android.widget.ImageView;
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
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import my.inventive.customerchoice.Adapters.PackagesAdapter;

public class MainActivity extends AppCompatActivity  implements  NavigationView
        .OnNavigationItemSelectedListener{
    GridView gridView;
    ViewPager viewPager;
NotificationBadge badge;
    Toolbar toolbar;
    DrawerLayout mDrawerLayout;
    List<Model> list;
    CustomAdapter customAdapter;
    SharedPreferences shared;
    String[] fruitNames = {"Atta & Other Flours","Rice & Other Grains","Edible Oils & Ghee ","Tea & Coffee","Salt & Sugar","Hair Care","Laundry & Detergent","Masalas","Mouth & Man's Care" ,"Bath & Wash Care","Pooja Needs","Dishwashers","Skin & Face Care","Mosquito Respellents","Pasta And Noodles","ketchups and Sauces","Jams Honey and Spreads","NamKeens and Biscuits","Ready Made Meals","Milk and Dairy products","Baking","Breakfast and Cereals","Beverage and Health","Chips and Chocolates","Bread and Butter","Ayushya Discount Sale","Bedsheets And curtain","Food Supplements","Fruits And Vegetable","Covid Protection","Festivals"};
    int[] fruitImages = {R.drawable.geo11,R.drawable.rice_grid,R.drawable.edilble,R.drawable.tea,R.drawable.sugar_grid,R.drawable.shampoo_grid,R.drawable.detergent,R.drawable.masala,R.drawable.colgate,R.drawable.shabun,R.drawable.pooja,R.drawable.vim,R.drawable.garnier,R.drawable.mortine,R.drawable.pasta1,R.drawable.ketchup,R.drawable.jam,R.drawable.namkeen,R.drawable.readymade,R.drawable.amul,R.drawable.baking,R.drawable.breakfast,R.drawable.bornvita,R.drawable.chocaltechis,R.drawable.breadbutter,R.drawable.ayusha,R.drawable.bedseet,R.drawable.food,R.drawable.fruits,R.drawable.ccccccc,R.drawable.festivals};
   // int[] fruitImages = {R.drawable.geo11,R.drawable.geo9,R.drawable.geo9,R.drawable.geo9,R.drawable.geo9,R.drawable.geo9,R.drawable.geo9,R.drawable.geo9,R.drawable.colgate,R.drawable.shabun,R.drawable.pooja,R.drawable.vim,R.drawable.garnier,R.drawable.mortine};
int count1=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_drawer_layout);
        shared = this.getSharedPreferences("User_Info", Context.MODE_PRIVATE);
        String user_name=shared.getString("name","");
        String user_mobile=shared.getString("mobile","");
        doInBackground();
        if(Build.VERSION.SDK_INT>=21){
            Window window=this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        }

        Log.d("Token",""+ FirebaseInstanceId.getInstance().getToken());
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Home");

        mDrawerLayout=findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(
                this,mDrawerLayout,toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        NavigationView navigationView = findViewById(R.id.navigation_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername =  headerView.findViewById(R.id.headername);
        TextView navUsermobile = headerView.findViewById(R.id.headernumber);
        ImageView userImage=headerView.findViewById(R.id.UserImageProfile);
        userImage.setImageResource(R.drawable.icon1);
        navUsername.setText(user_name);
        navUsermobile.setText(user_mobile);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        viewPager=findViewById(R.id.view_pager);
        PackagesAdapter packagesAdapter=new PackagesAdapter(this);
        viewPager.setAdapter(packagesAdapter);


       Myhelper myhelper=new Myhelper(this);
        SQLiteDatabase database = myhelper.getReadableDatabase();
        String sql1 = "select * from CUSTOMER";
        Cursor c1 = database.rawQuery(sql1,null);
        while(c1.moveToNext()){
            count1++;
        }

        Timer timer=new Timer();
        timer.scheduleAtFixedRate(new MyTimerTask(),5000,5000);

        list=new ArrayList<>();
        for(int i=0;i<fruitNames.length;i++){
            list.add(new Model(fruitNames[i],fruitImages[i]));

        }
        gridView = findViewById(R.id.gridview);

        customAdapter = new CustomAdapter(this,list);
        gridView.setAdapter(customAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0: {
                        Intent intent = new Intent(getApplicationContext(), AttaAndOtherActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    }
                    case 1: {
                        Intent intent1 = new Intent(getApplicationContext(), RiceAndOtherGains.class);
                        startActivity(intent1);
                        finish();
                        break;
                    }
                    case 2: {
                        Intent intent2 = new Intent(getApplicationContext(), OilsAndGhee.class);
                        startActivity(intent2);
                        finish();
                        break;
                    }
                    case 3: {
                        Intent intent3 = new Intent(getApplicationContext(), TeaAndCoffee.class);
                        startActivity(intent3);
                        finish();
                        break;
                    }
                    case 4: {
                        Intent intent4 = new Intent(getApplicationContext(), SaltAndSugarActivity.class);
                        startActivity(intent4);
                        finish();
                        break;
                    }
                    case 5: {
                        Intent intent5 = new Intent(getApplicationContext(), HairCareActivity.class);
                        startActivity(intent5);
                        finish();
                        break;
                    }
                    case 7: {
                        Intent intent7 = new Intent(getApplicationContext(), MasalaActivity.class);
                        startActivity(intent7);
                        finish();
                        break;
                    }
                    case 6: {
                        Intent intent6 = new Intent(getApplicationContext(), LaundryAndDetergentActivity.class);
                        startActivity(intent6);
                        finish();
                        break;
                    }
                    case 8:
                        Intent intent8 = new Intent(getApplicationContext(), MouthAndMansCare.class);
                        startActivity(intent8);
                        finish();
                        break;

                    case 9:
                        Intent intent9 = new Intent(getApplicationContext(), BathAndWashCare.class);
                        startActivity(intent9);
                        finish();
                        break;
                    case 10:
                        Intent intent10 = new Intent(getApplicationContext(), PoojaNeeds.class);
                        startActivity(intent10);
                        finish();
                        break;
                    case 11:
                        Intent intent11 = new Intent(getApplicationContext(), DisWashers.class);
                        startActivity(intent11);
                        finish();
                        break;
                    case 12:
                        Intent intent12 = new Intent(getApplicationContext(), SkinAndFaceCare.class);
                        startActivity(intent12);
                        finish();
                        break;
                    case 13:
                        Intent intent13 = new Intent(getApplicationContext(), MosqitoAndRespellents.class);
                        startActivity(intent13);
                        finish();
                        break;
                    case 14:
                        Intent intent14 = new Intent(getApplicationContext(), PastaAndNoodles.class);
                        startActivity(intent14);
                        finish();
                        break;
                    case 15:
                        Intent intent15 = new Intent(getApplicationContext(), KetchupsAndSoups.class);
                        startActivity(intent15);
                        finish();
                        break;
                    case 16:
                        Intent intent16 = new Intent(getApplicationContext(), JamesHoneyAndSpreads.class);
                        startActivity(intent16);
                        finish();
                        break;
                    case 17:
                        Intent intent17 = new Intent(getApplicationContext(),NamkeensAndBiscut.class);
                        startActivity(intent17);
                        finish();
                        break;
                    case 18:
                        Intent intent18 = new Intent(getApplicationContext(),ReadyMadeMeals.class);
                        startActivity(intent18);
                        finish();
                        break;
                    case 19:
                        Intent intent19 = new Intent(getApplicationContext(),MilkAndDairyProducts.class);
                        startActivity(intent19);
                        finish();
                        break;
                    case 20:
                        Intent intent20 = new Intent(getApplicationContext(),Baking.class);
                        startActivity(intent20);
                        finish();
                        break;
                    case 21:
                        Intent intent21 = new Intent(getApplicationContext(),BreakFastAndCreals.class);
                        startActivity(intent21);
                        break;
                    case 22:
                        Intent intent22 = new Intent(getApplicationContext(),BeveragesandHealthDrink.class);
                        startActivity(intent22);
                        finish();
                        break;
                    case 23:
                        Intent intent23 = new Intent(getApplicationContext(),ChipsAndChocolate.class);
                        startActivity(intent23);
                        finish();
                        break;
                    case 24:
                        Intent intent24 = new Intent(getApplicationContext(),BreadandButter.class);
                        startActivity(intent24);
                        finish();
                        break;
                    case 25:
                        Intent intent25 = new Intent(getApplicationContext(),Ayushya_Discount_sale.class);
                        startActivity(intent25);
                        finish();
                        break;
                    case 26:
                        Intent intent26 = new Intent(getApplicationContext(),Bedsheets_and_Curtain.class);
                        startActivity(intent26);
                        finish();
                        break;
                    case 27:
                        Intent intent27 = new Intent(getApplicationContext(),Food_Supplements.class);
                        startActivity(intent27);
                        finish();
                        break;
                    case 28:
                        Intent intent28 = new Intent(getApplicationContext(),Fruits_And_Vegetable.class);
                        startActivity(intent28);
                        finish();
                        break;
                    case 29:
                        Intent intent29 = new Intent(getApplicationContext(), CoviItem.class);
                        startActivity(intent29);
                        finish();
                        break;
                    case 30:
                        Intent intent30 = new Intent(getApplicationContext(),Festivals.class);
                        startActivity(intent30);
                        finish();
                        break;

                }
            }
        });
    }
    private void doInBackground()
    {
        String url = "https://simplyfied.co.in/groceryapp/isValidToken.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                if(response.trim().equals("true"))
                {
                    updateToken();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("id",shared.getString("id",""));
                map.put("token",FirebaseInstanceId.getInstance().getToken());
                map.put("table","UserRegistration");
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this, new HurlStack());
        requestQueue.add(stringRequest);
    }

    private synchronized void updateToken()
    {
        final String seller_id=shared.getString("id","");
        final ProgressDialog pd = new ProgressDialog(MainActivity.this);
        pd.setTitle("Customer Choice");
        pd.setMessage("Please Wait...");
        pd.show();
        String referral;
        String url = "https://simplyfied.co.in/groceryapp/updateToken.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                pd.dismiss();
                if(response.trim().equals("token updated"))
                {
                    SharedPreferences.Editor editor = shared.edit();
                    editor.putString("token",FirebaseInstanceId.getInstance().getToken());
                    editor.commit();
                    Toast.makeText(MainActivity.this, "Token Updated", Toast.LENGTH_SHORT).show();
                }
                else Toast.makeText(MainActivity.this, ""+response, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                Toast.makeText(MainActivity.this, "" + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("id",seller_id);
                map.put("token",FirebaseInstanceId.getInstance().getToken());
                map.put("table","UserRegistration");
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this, new HurlStack());
        requestQueue.add(stringRequest);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_for_cart,menu);

        MenuItem menuItem1=menu.findItem(R.id.cart1);
        menuItem1.setIcon(Converter.convertLayoutToImage(MainActivity.this,count1,R.drawable.ic_shopping_cart));
        menuItem1.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                startActivity(new Intent(MainActivity.this,CartActivity.class));
                finish();
                return true;
            }
        });

        MenuItem menuItem=menu.findItem(R.id.action_search);
        SearchView searchView=(SearchView) menuItem.getActionView();
        searchView.setQueryHint("Type here to search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                customAdapter.getFilter().filter(newText);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    public long getProfilesCount() {
        Myhelper myhelper=new Myhelper(this);
        SQLiteDatabase db = myhelper.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, "CUSTOMER");
        db.close();
        return count;
    }
    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cart:
                startActivity(new Intent(MainActivity.this, CartActivity.class));
                finish();
                break;
            case R.id.home:
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                break;
            case R.id.contact:
                startActivity(new Intent(MainActivity.this, ContactUs.class));
                break;

            case R.id.nav_website:
            {
                Uri webpage=Uri.parse("http://www.ayushyavatika.com");
                Intent intent=new Intent(Intent.ACTION_VIEW,webpage);
                startActivity(intent);
                break;
            }
            case R.id.myorder: {
                SharedPreferences prefs = this.getSharedPreferences("User_Info", Context.MODE_PRIVATE);
                String loginID = prefs.getString("email", "");
                String loginPWD = prefs.getString("password", "");

                if (loginID.length() > 0 && loginPWD.length() > 0) {
                    Intent intent = new Intent(MainActivity.this, MyorderActivity.class);
                    startActivity(intent);
                }
                else {
                    //SHOW PROMPT FOR LOGIN DETAILS
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                    alertDialogBuilder.setMessage("Please Login To Continue");
                    alertDialogBuilder.setPositiveButton("Login",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                                    startActivity(intent);
                                }
                            });

                    alertDialogBuilder.setNegativeButton("Sign-Up",new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            Intent intent=new Intent(MainActivity.this,RegisterActivity.class);
                            startActivity(intent);
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
                break;
            }
            case R.id.nav_logout:
            {
                SharedPreferences prefs = this.getSharedPreferences("User_Info", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("email", "");     //RESET TO DEFAULT VALUE
                editor.putString("password", "");     //RESET TO DEFAULT VALUE
                editor.commit();
                Toast.makeText(this, "Succefully Logout", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private class CustomAdapter extends BaseAdapter implements Filterable {

        Context context;
        List<Model> modelList;
        List<Model> modelListFilter;

        public CustomAdapter(Context context, List<Model> modelList) {
            this.context = context;
            this.modelList = modelList;
            this.modelListFilter = modelList;
        }

        @Override
        public int getCount() {
            return modelListFilter.size();
        }

        @Override
        public Object getItem(int i) {
            return modelListFilter.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            View view1 = getLayoutInflater().inflate(R.layout.custome_item,null);
            TextView name = view1.findViewById(R.id.fruits);
            ImageView image = view1.findViewById(R.id.images);

            name.setText(modelListFilter.get(i).getName());
            image.setImageResource(modelListFilter.get(i).getImage());
            return view1;
        }

        @Override
        public Filter getFilter() {
            Filter filter=new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    FilterResults filterResults=new FilterResults();
                    if(charSequence==null || charSequence.length()==0){
                        filterResults.count=modelList.size();
                        filterResults.values=modelList;
                    }
                    else{
                        String serachStr=charSequence.toString().toUpperCase();
                        List<Model> resultData=new ArrayList<>();
                        for(Model model:modelList){
                            if(model.getName().toUpperCase().contains(serachStr)){
                                resultData.add(model);
                            }
                            filterResults.count=resultData.size();
                            filterResults.values=resultData;
                        }
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults results) {
                    modelListFilter=(List<Model>) results.values;
                    notifyDataSetChanged();
                }
            };
            return filter;
        }


    }
    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(viewPager.getCurrentItem()==0){
                        viewPager.setCurrentItem(1);
                    }
                    else if(viewPager.getCurrentItem()==1){
                        viewPager.setCurrentItem(2);
                    }
                    else {
                        viewPager.setCurrentItem(0);
                    }
                }
            });
        }

    }
}