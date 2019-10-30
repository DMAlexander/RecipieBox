package com.example.devin.recipiebox.view.RecipieFolders;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.devin.recipiebox.R;
import com.example.devin.recipiebox.database.DatabaseHelper;
import com.example.devin.recipiebox.view.NewIngredient.IngredientLayoutScreen;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class RecipeAdd extends AppCompatActivity {

    private Button btnRecipieFolderAdd;
    private EditText editable_recipie_folder_item;
    private DatabaseHelper mDatabaseHelper;
    private ImageButton imageButton;
    private String recipieFolderName;
    private String recipieFolderId;
    private static final String IMAGE_DIRECTORY = "/demonuts";
    private int GALLERY = 1, CAMERA = 2;
    ImageButton mImageBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_add);
        btnRecipieFolderAdd = (Button) findViewById(R.id.btnRecipieFolderAdd);
        editable_recipie_folder_item = (EditText) findViewById(R.id.editable_recipie_folder_item);
        mDatabaseHelper = new DatabaseHelper(this);

        setButtons();

        imageButton = (ImageButton) findViewById(R.id.iv);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPictureDialog();
            }
        });
    }


    public void setButtons() {
        btnRecipieFolderAdd = findViewById(R.id.btnRecipieFolderAdd);
        btnRecipieFolderAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recipieFolderName = editable_recipie_folder_item.getText().toString();
                if (editable_recipie_folder_item.length() !=0) {
                    insertItem(recipieFolderName);
          //          showPictureDialog();
                    Intent intent = new Intent(RecipeAdd.this, RecipieFolder.class);
                    startActivity(intent);
                    editable_recipie_folder_item.setText("");
                } else {
                    toastMessage("Please put something in the textbox!");
                }
            }
        });
    }

    public void insertItem(String recipieFolderName) {
        if(editable_recipie_folder_item != null) {
            boolean insertData = mDatabaseHelper.addRecipieFolderData(recipieFolderName);
            if (insertData) {
                toastMessage("Data successfully inserted!");
            } else {
                toastMessage("Something went wrong!");
            }
        } else {
            toastMessage("Put something in the text field!");
        }
    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        switch (which) {
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
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if(requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    String path = saveImage(bitmap);
                    Toast.makeText(RecipeAdd.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    //   imageView.setImageBitmap(bitmap);
                    imageButton.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();;
                    Toast.makeText(RecipeAdd.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            //   imageView.setImageBitmap(thumbnail);
            imageButton.setImageBitmap(thumbnail);
            saveImage(thumbnail);
            Toast.makeText(RecipeAdd.this, "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure if needed
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdir();
        }

        try {
            //   File f = new File(wallpaperDirectory, Calendar.getInstance().getTimeInMillis() + ".jpg");
      //      int fss = mDatabaseHelper.getRecipeFolderTableCount();
            Cursor data = mDatabaseHelper.getRecipieFolderData();
            while (data.moveToNext()) {
                recipieFolderId = data.getString(0);
            }
            int result = 0;
            if (recipieFolderId != null){
                result = Integer.parseInt(recipieFolderId);
            } else {
                result = 0;
            }
            result = result+1;
            recipieFolderId = String.valueOf(result);
            String fileName = "myImage" + recipieFolderId;
      //      final int recipieFolderPicID = recipieFolderID
            File f = new File(wallpaperDirectory, fileName + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this, new String[]{f.getPath()},new String[]{"image/jpeg"},null);
            fo.close();
            Log.d("TAG", "File Saved::-->" + f.getAbsolutePath()); //ex) should be 'myImagePizza.jpg'

            return f.getAbsolutePath();
        } catch (IOException el) {
            el.printStackTrace();
        }
        return "";
    }

    private void toastMessage(String message) {
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}
