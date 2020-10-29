package com.example.devin.recipebox.view.NewIngredient;

        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.res.Resources;
        import android.database.Cursor;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.media.MediaScannerConnection;
        import android.net.Uri;
        import android.os.Environment;
        import android.provider.MediaStore;
        import android.support.v7.app.AlertDialog;
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

        import com.example.devin.recipebox.R;
        import com.example.devin.recipebox.database.DatabaseHelper;
        import com.example.devin.recipebox.view.PublishedIngredient.IngredientInfo;
        import com.example.devin.recipebox.view.ShoppingCart.ShoppingCartList;

        import java.io.ByteArrayOutputStream;
        import java.io.File;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.util.ArrayList;

public class IngredientLayoutScreen extends AppCompatActivity {

    private static final String TAG = "IngredientLayoutScreen";
    private LinearLayout parentLinearLayout;
    int sizeOfList = 1;
    private Button btnSave;

    DatabaseHelper mDatabaseHelper;
    private EditText number_edit_text;
    private EditText recipieDescription;
    private String selectedRecipieName;
    private int selectedRecipieID;
    private Spinner type_spinner, type_spinner2;
    ImageButton mImageBtn;
    TextView mCountTv;
    TextView descriptionLabel;
    MenuItem mCartIconMenuItem;
    private ImageButton imageButton;
    private static final String IMAGE_DIRECTORY = "/demonuts";
    private int GALLERY = 1, CAMERA = 2;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_ingredient_layout_screen );
        parentLinearLayout = (LinearLayout) findViewById( R.id.parent_linear_layout );
        number_edit_text = (EditText) findViewById( R.id.number_edit_text );
        recipieDescription = (EditText) findViewById( R.id.recipeDescription );
        type_spinner = (Spinner) findViewById( R.id.type_spinner );
        type_spinner2 = (Spinner) findViewById( R.id.type_spinner2 );
        descriptionLabel = (TextView) findViewById( R.id.descriptionLabel );
        descriptionLabel.setText( "Recipe Description:" );
        mDatabaseHelper = new DatabaseHelper(this );
        Intent recievedIntent = getIntent();
        selectedRecipieName = recievedIntent.getStringExtra("RecipieName" );

        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY );
        String fileName = "/myImage" + selectedRecipieName;
        File imgFile = new File(wallpaperDirectory + fileName + ".jpg" );
        if( imgFile.exists() ) {
            Bitmap bitmap = BitmapFactory.decodeFile( imgFile.getAbsolutePath() );
            mImageBtn = (ImageButton) findViewById( R.id.iv );
            mImageBtn.setImageBitmap( bitmap );
        }

        imageButton = (ImageButton) findViewById( R.id.iv );

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                showPictureDialog();
            }
        });

        Intent receivedIntent = getIntent();
        selectedRecipieID = receivedIntent.getIntExtra("RecipieId", -1 );
        selectedRecipieName = receivedIntent.getStringExtra("RecipieName" );

        Cursor data = mDatabaseHelper.getRecipeDescription( selectedRecipieName );
        String recipeDescription = "";
        while ( data.moveToNext() ) {
            recipeDescription = data.getString(0);
        }
        Log.d( TAG, "Description is: " + recipeDescription );

        recipieDescription.setText( recipeDescription );

        int count = mDatabaseHelper.getIngredientCount( selectedRecipieID );

        for( int i=0; i<count; i++ ) {
            String ingredientName = "";
            String measurementQuantity = "";
            String measurementType = "";

            Cursor data2 = mDatabaseHelper.getIngredientsBasedOnRecipeData( selectedRecipieID );
            ArrayList<String> listData = new ArrayList<>();
            while ( data2.moveToNext() ) {

                ingredientName = data2.getString(1);
                measurementQuantity = data2.getString(2);
                measurementType = data2.getString(3);
                listData.add( ingredientName );
                listData.add( measurementQuantity );
                listData.add( measurementType );
            }
            ingredientName = listData.get(0);
            measurementQuantity = listData.get(1);
            measurementType = listData.get(2);

            View v = parentLinearLayout.getChildAt(i);
            number_edit_text.setText( ingredientName );

            Resources res = getResources();
            String[] items = res.getStringArray( R.array.measurements_array );
            int index = 0;
            for (int j=0; j < items.length; j++) {
                if(items[j].equalsIgnoreCase( measurementQuantity ) ) {
                    index=j;
                }
            }

            type_spinner.setSelection(index);

            String[] items2 = res.getStringArray( R.array.measurement_type_array );
            int index2 = 0;
            for (int k=0; k < items2.length; k++) {
                if(items2[k].equalsIgnoreCase( measurementType ) ) {
                    index2=k;
                }
            }
            type_spinner2.setSelection( index2 );
        }

        final int childCount = parentLinearLayout.getChildCount();
        btnSave = (Button) findViewById( R.id.btnSave );

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent recievedIntent = getIntent();
                selectedRecipieID = recievedIntent.getIntExtra("RecipieId", -1 );
                mDatabaseHelper.deleteRecipeIngredients( selectedRecipieID );

                final int childCount = parentLinearLayout.getChildCount();
                Intent receivedIntent = getIntent();
                selectedRecipieID = receivedIntent.getIntExtra("RecipieId", -1 );
                for( int i=1; i<childCount-4; i++ ) {
                    View v = parentLinearLayout.getChildAt(i);
                    number_edit_text = (EditText) v.findViewById( R.id.number_edit_text );

                    String ingredientName = number_edit_text.getText().toString();
                    if( number_edit_text.getText().toString().length() == 0 ) {
                        number_edit_text.setError( "Ingredient Name is required!" );
                    }

                    Cursor data = mDatabaseHelper.getRecipeItemID( selectedRecipieName );
                    type_spinner = (Spinner) v.findViewById( R.id.type_spinner );
                    String newEntry2 = type_spinner.getSelectedItem().toString();

                    Resources res = getResources();
                    String[] items = res.getStringArray( R.array.measurements_array );
                    int index = 0;
                    for ( int j=0; j < items.length; j++ ) {
                        if( items[j]==newEntry2 ) {
                            index=j;
                        }
                    }
                    System.out.print( index );

                    type_spinner2 = (Spinner) v.findViewById( R.id.type_spinner2 );
                    String newEntry3 = type_spinner2.getSelectedItem().toString();

                    String[] items2 = res.getStringArray( R.array.measurement_type_array );
                    int index2 = 0;
                    for ( int k=0; k < items2.length; k++ ) {
                        if( items2[k]==newEntry3 ) {
                            index2=k;
                        }
                    }

                    System.out.print( index2 );

                    if(newEntry2.equalsIgnoreCase("" ) ) {
                        newEntry2 = null;
                    }

                    if(newEntry3.equalsIgnoreCase("" ) ) {
                        newEntry3 = null;
                    }

                    double convertedSpinner = 0;
                    //     String newEntry2 = type_spinner.getSelectedItem().toString();
                    //     String newEntry3 = type_spinner2.getSelectedItem().toString();

                    if (newEntry2.equalsIgnoreCase("1/8" ) ) {
                        //      convertedSpinner = Double.parseDouble(newEntry2);
                        convertedSpinner = 0.125;
                        System.out.print( convertedSpinner );
                    } else if (newEntry2.equalsIgnoreCase("1/4" ) ) {
                        convertedSpinner = 0.25;
                        System.out.print( convertedSpinner );
                    } else if (newEntry2.equalsIgnoreCase("1/2" ) ) {
                        convertedSpinner = 0.5;
                        System.out.print( convertedSpinner );
                    } else if (newEntry2.equalsIgnoreCase("1" ) ) {
                        convertedSpinner = 1;
                        System.out.print( convertedSpinner );
                    } else if (newEntry2.equalsIgnoreCase("2" ) ) {
                        convertedSpinner = 2;
                        System.out.print( convertedSpinner );
                    } else if (newEntry2.equalsIgnoreCase("3" ) ) {
                        convertedSpinner = 3;
                        System.out.print( convertedSpinner );
                    } else {
                        //     convertedSpinner = 0;
                        convertedSpinner = Double.valueOf( newEntry2 );
                        System.out.print( convertedSpinner );
                    }

                    //   Double convertedSpinner = Double.valueOf(newEntry2);

                    if ( number_edit_text.length() != 0 ) {
                        insertItem( ingredientName, newEntry2, newEntry3, selectedRecipieID );
                    } else {
                        mDatabaseHelper.addIngredientData( ingredientName, convertedSpinner, newEntry3, "N", selectedRecipieID );
                        toastMessage( "No Ingredients Added!" );
                    }
                }

                if ( recipieDescription.length() != 0 ) {
                    String recipeDescription = recipieDescription.getText().toString();
                    mDatabaseHelper.updateRecipeDescription( recipeDescription, selectedRecipieID );
                } else {
                    toastMessage("Put something in the description text field!");
                }

                Intent intent = new Intent(IngredientLayoutScreen.this, IngredientInfo.class );
                intent.putExtra("RecipieName", selectedRecipieName );
                startActivity( intent );
            }
        });
    }

    /**
     * Add the new row before the add field button
     * @param v
     */
    public void onAddField( View v ) {
        LayoutInflater inflater = (LayoutInflater) getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        final View rowView = inflater.inflate( R.layout.activity_ingredient_layout_field, null );
        sizeOfList++;
        parentLinearLayout.addView( rowView, parentLinearLayout.getChildCount() - 4 );
    }

    public void onDelete( View v ) {
        parentLinearLayout.removeView( (View) v.getParent() );
        sizeOfList--;
    }

    public void insertItem( String ingredientName, String newEntry2, String newEntry3, int selectedRecipieID ) {

        double convertedSpinner = 0;
        //     String newEntry2 = type_spinner.getSelectedItem().toString();
        //     String newEntry3 = type_spinner2.getSelectedItem().toString();

        if ( newEntry2.equalsIgnoreCase("1/8" ) ) {
            //      convertedSpinner = Double.parseDouble(newEntry2);
            convertedSpinner = 0.125;
            System.out.print( convertedSpinner );
        } else if ( newEntry2.equalsIgnoreCase("1/4" ) ) {
            convertedSpinner = 0.25;
            System.out.print( convertedSpinner );
        } else if ( newEntry2.equalsIgnoreCase("1/2" ) ) {
            convertedSpinner = 0.5;
            System.out.print( convertedSpinner );
        } else if ( newEntry2.equalsIgnoreCase("1" ) ) {
            convertedSpinner = 1;
            System.out.print( convertedSpinner );
        } else if ( newEntry2.equalsIgnoreCase("2" ) ) {
            convertedSpinner = 2;
            System.out.print( convertedSpinner );
        } else if ( newEntry2.equalsIgnoreCase("3" ) ) {
            convertedSpinner = 3;
            System.out.print( convertedSpinner );
        } else {
            //     convertedSpinner = 0;
            convertedSpinner = Double.valueOf( newEntry2 );
            System.out.print( convertedSpinner );
        }

        if ( number_edit_text.length() != 0 ) {
            Log.d( TAG, "ingredientName: " + ingredientName + " num: " + convertedSpinner + " newEntry3: " + newEntry3 + "recipieId :" + selectedRecipieID );
            boolean insertData = mDatabaseHelper.addIngredientData( ingredientName, convertedSpinner, newEntry3, "Y", /* convertedPrice, */ selectedRecipieID ); //we need all 4 parameters here...
            if ( insertData ) {
                toastMessage( "Data successfully inserted!" );
            } else {
                toastMessage( "Something went wrong!" );
            }
        } else {
            toastMessage( "Put something in the text field!" );
        }
    }

    //Need this method for shopping cart icon
    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        getMenuInflater().inflate( R.menu.menu, menu );
        mCartIconMenuItem = menu.findItem( R.id.cart_count_menu_item );
        View actionView = mCartIconMenuItem.getActionView();

        if( actionView != null ) {
            mCountTv = actionView.findViewById( R.id.count_tv_layout );
            mImageBtn = actionView.findViewById( R.id.image_btn_layout );
        }
        mImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IngredientLayoutScreen.this, ShoppingCartList.class );
                startActivity( intent );
            }
        });
        int shoppingCartCount = mDatabaseHelper.getShoppingCartCount();
        String shoppingCartString = String.valueOf( shoppingCartCount );
        mCountTv.setText( shoppingCartString );

        return super.onCreateOptionsMenu( menu );
    }

    public void removeItem( int position, String ingredientName ) {

        Cursor data = mDatabaseHelper.getRecipeItemID( selectedRecipieName );
        int recipieID = -1;
        while ( data.moveToNext() ) {
            recipieID = data.getInt(0);
        }
        data = mDatabaseHelper.getIngredientItemID( ingredientName, recipieID );
        int itemID = -1;
        while ( data.moveToNext() ) {
            itemID = data.getInt(0);
        }
        Log.d( TAG, "ingredientId: " + itemID + " and ingredient name is: " + ingredientName );
        mDatabaseHelper.deleteIngredientName( itemID, ingredientName );

        //   mAdapter.notifyItemRemoved(position);
        //   mAdapter.notifyDataSetChanged();
        finish();
        overridePendingTransition(0, 0);
        startActivity( getIntent() );
        overridePendingTransition(0, 0);
        toastMessage( "Removed from database" );
    }

    private void toastMessage( String message ) {
        Toast.makeText(this,message, Toast.LENGTH_SHORT ).show();
    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this );
        pictureDialog.setTitle( "Select Action" );
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        switch ( which ) {
                            case 0:
                                choosePhotoFromGallery();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallery() {
        Intent galleryIntent = new Intent( Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI );
        startActivityForResult( galleryIntent, GALLERY );
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );
        startActivityForResult( intent, CAMERA );
    }

    @Override
    public void onActivityResult( int requestCode, int resultCode, Intent data ) {
        super.onActivityResult( requestCode, resultCode, data );
        if ( resultCode == this.RESULT_CANCELED ) {
            return;
        }
        if( requestCode == GALLERY ) {
            if ( data != null ) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap( this.getContentResolver(), contentURI );
                    String path = saveImage( bitmap );
                    Toast.makeText(IngredientLayoutScreen.this, "Image Saved!", Toast.LENGTH_SHORT ).show();
                    //   imageView.setImageBitmap(bitmap);
                    imageButton.setImageBitmap( bitmap );
                } catch ( IOException e ) {
                    e.printStackTrace();
                    Toast.makeText(IngredientLayoutScreen.this, "Failed!", Toast.LENGTH_SHORT ).show();
                }
            }
        } else if ( requestCode == CAMERA ) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get( "data" );
            //   imageView.setImageBitmap(thumbnail);
            imageButton.setImageBitmap( thumbnail );
            saveImage( thumbnail );
            Toast.makeText(IngredientLayoutScreen.this, "Image Saved!", Toast.LENGTH_SHORT ).show();
        }
    }

    public String saveImage( Bitmap myBitmap ) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress( Bitmap.CompressFormat.JPEG, 90, bytes );
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY );
        // have the object build the directory structure if needed
        if ( !wallpaperDirectory.exists() ) {
            wallpaperDirectory.mkdir();
        }

        try {
            //   File f = new File(wallpaperDirectory, Calendar.getInstance().getTimeInMillis() + ".jpg");
            String fileName = "myImage" + selectedRecipieName;
            File f = new File( wallpaperDirectory, fileName + ".jpg" );
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream( f );
            fo.write( bytes.toByteArray() );
            MediaScannerConnection.scanFile(this, new String[]{f.getPath()},new String[]{"image/jpeg"},null );
            fo.close();
            Log.d( TAG, "File Saved::-->" + f.getAbsolutePath() ); //ex) should be 'myImagePizza.jpg'
            return f.getAbsolutePath();

        } catch ( IOException el ) {

            el.printStackTrace();

        }
        return "";
    }
        /*
    private void requestMultiplePermissions(){
        Dexter.withActivity(this).withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    Toast.makeText(getApplicationContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
                }
                //check for permanent denial of any permission
                if (report.isAnyPermissionPermanentlyDenied()) {
                    //show alert dialog navigating to Settings
                    //openSettingsDialog();
                }
            }
            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).withErrorListener(new PermissionRequestErrorListener() {
            @Override
            public void onError(DexterError error) {
                Toast.makeText(getApplicationContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
            }
        }).onSameThread().check();
    }
    */
}