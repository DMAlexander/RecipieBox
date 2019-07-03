package com.example.devin.recipiebox.view.NewIngredient;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.devin.recipiebox.R;
import com.example.devin.recipiebox.database.DatabaseHelper;
import com.example.devin.recipiebox.view.Ingredient.IngredientEditAdapter;
import com.example.devin.recipiebox.view.Ingredient.IngredientScreen;
import com.example.devin.recipiebox.view.MainMenu;
import com.example.devin.recipiebox.view.PublishedIngredient.IngredientInfo;
import com.example.devin.recipiebox.view.Recipie.MainActivity;
import com.example.devin.recipiebox.view.Recipie.RecipieInsert;
import com.example.devin.recipiebox.view.ShoppingCart.ShoppingCartList;

import java.util.ArrayList;

public class IngredientLayoutScreen extends AppCompatActivity {

    private static final String TAG = "IngredientLayoutScreen";
    private LinearLayout parentLinearLayout;
    int sizeOfList = 1; //tracks how large the list is...
    private Button btnIngredientAdd, btnIngredientInfo, btnSave;
    //    private EditText editable_recipie_item, editable_ingredient_item;
//    private ListView mListView;
    //   private ImageView imageView;

    DatabaseHelper mDatabaseHelper;
    private IngredientEditAdapter mAdapter;
    private EditText number_edit_text;
    private EditText price_edit_text;
    private EditText recipieDescription;
    private String selectedRecipieName;
    private int selectedRecipieID;
    private int selectedIngredientID;
    private static final int PICK_IMAGE = 100;
    private Spinner type_spinner, type_spinner2;
    Uri imageUri;
    RecyclerView recyclerView;
    ImageButton mImageBtn;
    Toolbar mMyToolbar;
    TextView mCountTv;
    MenuItem mCartIconMenuItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_layout_screen);
        parentLinearLayout = (LinearLayout) findViewById(R.id.parent_linear_layout);
        number_edit_text = (EditText) findViewById(R.id.number_edit_text);
        price_edit_text = (EditText) findViewById(R.id.price_edit_text);
        recipieDescription = (EditText) findViewById(R.id.recipieDescription);
        type_spinner = (Spinner) findViewById(R.id.type_spinner);
        type_spinner2 = (Spinner) findViewById(R.id.type_spinner2);
        mDatabaseHelper = new DatabaseHelper(this);
        mMyToolbar = findViewById(R.id.myToolBar);
        setSupportActionBar(mMyToolbar);
        mMyToolbar.setTitleTextColor(0xFFFFFFFF);

        Intent receivedIntent = getIntent();
        selectedRecipieID = receivedIntent.getIntExtra("RecipieId", -1);
        selectedRecipieName = receivedIntent.getStringExtra("RecipieName");
  //      getSupportActionBar().setTitle(selectedRecipieName + selectedRecipieID);
        final int childCount = parentLinearLayout.getChildCount();
        btnSave = (Button) findViewById(R.id.btnSave);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int childCount = parentLinearLayout.getChildCount();
                Intent receivedIntent = getIntent();
                selectedRecipieID = receivedIntent.getIntExtra("RecipieId", -1);
                for(int i=1; i<childCount-3; i++) {
                    View v = parentLinearLayout.getChildAt(i);
                    number_edit_text = (EditText) v.findViewById(R.id.number_edit_text);

                    String ingredientName = number_edit_text.getText().toString();

         //           View rv = v.getRootView()
                    Cursor data = mDatabaseHelper.getRecipieItemID(selectedRecipieName);


                    //       for(int i=0; i<sizeOfList; i++) {
//                    View v = parentLinearLayout.getChildAt(i);

                    price_edit_text = (EditText) v.findViewById(R.id.price_edit_text);
                    String price = price_edit_text.getText().toString();
                    double convertedPrice = Double.parseDouble(price);



                    if (number_edit_text.length() != 0) {
                        insertItem(ingredientName, convertedPrice, selectedRecipieID);
                        //               number_edit_text.setText("");
                    } else {
                        toastMessage("Please put something in the textbox!");
                    }
                }

                if (recipieDescription.length() != 0) {
                    String recipeDescription = recipieDescription.getText().toString();
                    mDatabaseHelper.updateRecipieDescription(recipeDescription, selectedRecipieID);
                } else {
                    toastMessage("Put something in the description text field!");
                }

/*
                Cursor data = mDatabaseHelper.getRecipieItemID(selectedRecipieName);
                int itemID = -1;
                while (data.moveToNext()) {
                    itemID = data.getInt(0);
                }
                if(itemID > 1) {
                    Log.d(TAG, "The RecipieID is: " + itemID);
                }
                */
                //         return mDatabaseHelper.getIngredientsBasedOnRecipieData(itemID);

                Intent intent = new Intent(IngredientLayoutScreen.this, IngredientInfo.class);
        //       intent.putExtra("RecipieId", itemID);
                intent.putExtra("RecipieName", selectedRecipieName);
         //       Log.d(TAG, "The RecipieId is: " + itemID);
                startActivity(intent);

       //         Intent editScreenIntent = new Intent(IngredientLayoutScreen.this, IngredientInfo.class);
                //         editScreenIntent.putExtra("FolderID", selectedRecipieFolderID);
      //          startActivity(editScreenIntent);

            }
        });


    }
    public void onAddField(View v) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.activity_ingredient_layout_field, null);
        //Add the new row before the add field button
        sizeOfList++;
        parentLinearLayout.addView(rowView, parentLinearLayout.getChildCount() - 3);
    }
    public void onDelete(View v) {
        parentLinearLayout.removeView((View) v.getParent());
        sizeOfList--;
    }
    public void insertItem(String ingredientName, double convertedPrice, int selectedRecipieID) {
    /*
        Cursor data = mDatabaseHelper.getRecipieItemID(selectedRecipieName);
        int itemID = -1;
        while (data.moveToNext()) {
            itemID = data.getInt(0);
        } */
        double convertedSpinner = 0;
        String newEntry2 = type_spinner.getSelectedItem().toString();
        String newEntry3 = type_spinner2.getSelectedItem().toString();

        if (newEntry2.equalsIgnoreCase("1/8")) {
      //      convertedSpinner = Double.parseDouble(newEntry2);
            convertedSpinner = 0.125;
            System.out.print(convertedSpinner);
        } else if (newEntry2.equalsIgnoreCase("1/4")) {
            convertedSpinner = 0.25;
            System.out.print(convertedSpinner);
        } else if (newEntry2.equalsIgnoreCase("1/2")) {
            convertedSpinner = 0.5;
            System.out.print(convertedSpinner);
        } else if (newEntry2.equalsIgnoreCase("1")) {
            convertedSpinner = 1;
            System.out.print(convertedSpinner);
        } else if (newEntry2.equalsIgnoreCase("2")) {
            convertedSpinner = 2;
            System.out.print(convertedSpinner);
        } else if (newEntry2.equalsIgnoreCase("3")) {
            convertedSpinner = 3;
            System.out.print(convertedSpinner);
        } else {
            convertedSpinner = 0;
            System.out.print(convertedSpinner);
        }

        /*
        View v = parentLinearLayout.getChildAt(i);
        price_edit_text = (EditText) v.findViewById(R.id.price_edit_text);
        String price = price_edit_text.getText().toString();
        double convertedPrice = Double.parseDouble(price);
        */

        //      String newEntry = editable_ingredient_item.getText().toString();
        if (number_edit_text.length() != 0) {
            Log.d(TAG, "ingredientName: " + ingredientName + " num: " + convertedSpinner + " newEntry3: " + newEntry3 + "recipieId :" + selectedRecipieID);
            boolean insertData = mDatabaseHelper.addIngredientData(ingredientName, convertedSpinner, newEntry3, convertedPrice, selectedRecipieID); //we need all 4 parameters here...
            if (insertData) {
                toastMessage("Data successfully inserted!");
            } else {
                toastMessage("Something went wrong!");
            }
        } else {
            toastMessage("Put something in the text field!");
        }

        String currentPrice2 = "";

        Cursor data = mDatabaseHelper.getRecipePrice(selectedRecipieName);
        ArrayList<String> listData = new ArrayList<>();
        while (data.moveToNext()) {
            currentPrice2 = data.getString(0);
            listData.add(currentPrice2);
        }
        currentPrice2 = listData.get(0);
        Double convertedPrice2 = Double.parseDouble(currentPrice2);
        Double convertedPrice3 = convertedPrice + convertedPrice2;

        mDatabaseHelper.updateRecipePrice(convertedPrice3, selectedRecipieName);
    }

    //Need this method for shopping cart icon
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        mCartIconMenuItem = menu.findItem(R.id.cart_count_menu_item);
        View actionView = mCartIconMenuItem.getActionView();

        if(actionView != null) {
            mCountTv = actionView.findViewById(R.id.count_tv_layout);
            mImageBtn = actionView.findViewById(R.id.image_btn_layout);
        }
        mImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IngredientLayoutScreen.this, ShoppingCartList.class);
                startActivity(intent);
            }
        });
        int shoppingCartCount = mDatabaseHelper.getShoppingCartCount();
        String shoppingCartString = String.valueOf(shoppingCartCount);
        mCountTv.setText(shoppingCartString);

        return super.onCreateOptionsMenu(menu);
    }

    public void removeItem(int position, String ingredientName) {

        Cursor data = mDatabaseHelper.getRecipieItemID(selectedRecipieName);
        int recipieID = -1;
        while (data.moveToNext()) {
            recipieID = data.getInt(0);
        }
        data = mDatabaseHelper.getIngredientItemID(ingredientName, recipieID);
        int itemID = -1;
        while (data.moveToNext()) {
            itemID = data.getInt(0);
        }
        Log.d(TAG, "ingredientId: " + itemID + " and ingredient name is: " + ingredientName);
        mDatabaseHelper.deleteIngredientName(itemID, ingredientName);

        mAdapter.notifyItemRemoved(position);
        mAdapter.notifyDataSetChanged();
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
        toastMessage("Removed from database");
    }
    private void toastMessage(String message) {
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}
