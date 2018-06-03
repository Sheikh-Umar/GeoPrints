package com.dynamicoders.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.mvc.imagepicker.ImagePicker;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInLeftAnimationAdapter;
import jp.wasabeef.recyclerview.animators.FadeInAnimator;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.Query;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StickersRVAdapter myAdapter;
    private final int[] data = {
            R.drawable.picture1,
            R.drawable.picture2,
            R.drawable.picture3,
            R.drawable.picture4,
            R.drawable.picture5,
            R.drawable.picture6,
            R.drawable.picture7,
            R.drawable.picture8,
            R.drawable.picture9,
            R.drawable.picture10,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FirebaseAuth firebaseAuth;
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user == null) {
                Intent s = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(s);
            }

            else {
                firebaseAuth.getCurrentUser();
            }

        recyclerView = findViewById(R.id.stickers_recycler_view);
        Paint paint = new Paint();
        paint.setStrokeWidth(5);
        paint.setColor(Color.BLUE);
        paint.setAntiAlias(true);
        paint.setPathEffect(new DashPathEffect(new float[]{50, 50}, 0));
        recyclerView.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(this).paint(paint).build());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new StickersRVAdapter(this,data);
        recyclerView.setAdapter(new ScaleInAnimationAdapter(myAdapter));

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Nammu.askForPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, new PermissionCallback() {
                @Override
                public void permissionGranted() {
                    //Nothing, this sample saves to Public gallery so it needs permission
                }

                @Override
                public void permissionRefused() {
                    finish();
                }
            });
        }

        permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Nammu.askForPermission(this, Manifest.permission.CAMERA, new PermissionCallback() {
                @Override
                public void permissionGranted() {
                    //Nothing, this sample saves to Public gallery so it needs permission
                }

                @Override
                public void permissionRefused() {
                    finish();
                }
            });
        }

        EasyImage.configuration(this)
                .setImagesFolderName("EasyImage sample")
                .setCopyTakenPhotosToPublicGalleryAppFolder(true)
                .setCopyPickedImagesToPublicGalleryAppFolder(true)
                .setAllowMultiplePickInGallery(false);

        ImagePicker.setMinQuality(600, 600);
    }

    public void onPickImage(View view) {
//        ImagePicker.pickImage(this, "Select your image:");
        EasyImage.openChooserWithGallery(MainActivity.this, "Choose image", 0);
    }



//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        InputStream is = ImagePicker.getInputStreamFromResult(this, requestCode, resultCode, data);
//
//        try {
//            ExifInterface exif = new ExifInterface(is);
//            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//            int rotationInDegrees = exifToDegrees(rotation);
//            Matrix matrix = new Matrix();
//            if (rotation != 0f) {matrix.preRotate(rotationInDegrees);}
//            Bitmap bitmap = ImagePicker.getImageFromResult(this, requestCode, resultCode, data);
//            Bitmap adjustedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//            createImageFromBitmap(adjustedBitmap);
//            Intent intent = new Intent(this, ImageActivity.class);
////            startActivity(intent);
//            imageView.setImageBitmap(adjustedBitmap);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
                e.printStackTrace();
            }

            @Override
            public void onImagesPicked(List<File> imageFiles, EasyImage.ImageSource source, int type) {

                try {
                    InputStream is = new FileInputStream(imageFiles.get(0));
                    ExifInterface exif = new ExifInterface(is);
                    int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    int rotationInDegrees = exifToDegrees(rotation);
                    Matrix matrix = new Matrix();
                    if (rotation != 0f) {
                        matrix.preRotate(rotationInDegrees);
                    }
                    Bitmap bitmap = BitmapFactory.decodeFile(imageFiles.get(0).getAbsolutePath());
                    Bitmap adjustedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    createImageFromBitmap(adjustedBitmap);
                    openImageActivity();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                //Cancel handling, you might wanna remove taken photo if it was canceled
                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(MainActivity.this);
                    if (photoFile != null) photoFile.delete();
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        // Clear any configuration that was done!
        EasyImage.clearConfiguration(this);
        super.onDestroy();
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    public void openImageActivity(){
        Intent intent = new Intent(this, ImageActivity.class);
        startActivity(intent);
    }

    public String createImageFromBitmap(Bitmap bitmap) {
        String fileName = "myImage";//no .png or .jpg needed
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            FileOutputStream fo = openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            // remember close file output
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
            fileName = null;
        }
        return fileName;
    }
}
