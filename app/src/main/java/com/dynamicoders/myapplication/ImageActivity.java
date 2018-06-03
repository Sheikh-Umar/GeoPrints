package com.dynamicoders.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.support.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.dynamicoders.myapplication.ui.adapter.FontsAdapter;
import com.dynamicoders.myapplication.utils.FontProvider;
import com.dynamicoders.myapplication.viewmodel.Font;
import com.dynamicoders.myapplication.viewmodel.Layer;
import com.dynamicoders.myapplication.viewmodel.TextLayer;
import com.dynamicoders.myapplication.widget.MotionView;
import com.dynamicoders.myapplication.widget.entity.ImageEntity;
import com.dynamicoders.myapplication.widget.entity.MotionEntity;
import com.dynamicoders.myapplication.widget.entity.TextEntity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ImageActivity extends AppCompatActivity implements TextEditorDialogFragment.OnTextLayerCallback {

    public static final int SELECT_STICKER_REQUEST_CODE = 123;
    public static final int SAVE_REQUEST = 456;
    public static final int SHARE_REQUEST = 789;


    private String m_Text = "";

    private Bitmap bitmap;

    private ArrayList<Integer> usedStickers;

    protected MotionView motionView;
    protected View textEntityEditPanel;
    private final MotionView.MotionViewCallback motionViewCallback = new MotionView.MotionViewCallback() {
        @Override
        public void onEntitySelected(@Nullable MotionEntity entity) {
            if (entity instanceof TextEntity) {
                textEntityEditPanel.setVisibility(View.VISIBLE);
            } else {
                textEntityEditPanel.setVisibility(View.GONE);
            }
        }

        @Override
        public void onEntityDoubleTap(@NonNull MotionEntity entity) {
            startTextEntityEditing();
        }
    };
    private FontProvider fontProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_activity);

        this.fontProvider = new FontProvider(getResources());

        usedStickers = new ArrayList<>();

        motionView = (MotionView) findViewById(R.id.main_motion_view);
        textEntityEditPanel = findViewById(R.id.main_motion_text_entity_edit_panel);
        motionView.setMotionViewCallback(motionViewCallback);

        try {
            bitmap = BitmapFactory.decodeStream(this.openFileInput("myImage"));
            ExifInterface exif = new ExifInterface(this.openFileInput("myImage"));
            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int rotationInDegrees = exifToDegrees(rotation);
            Matrix matrix = new Matrix();
            if (rotation != 0f) {
                matrix.preRotate(rotationInDegrees);
            }
            final Bitmap adjustedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

            motionView.post(new Runnable() {
                @Override
                public void run() {
                    Layer layer = new Layer();

                    ImageEntity entity = new ImageEntity(layer, adjustedBitmap, motionView.getWidth(), motionView.getHeight());


                    motionView.addFirstEntityAndPosition(entity);

                }
            });

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        initTextEntitiesListeners();


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

    private void addSticker(final int stickerResId) {
        motionView.post(new Runnable() {
            @Override
            public void run() {
                usedStickers.add(stickerResId);
                Layer layer = new Layer();
                layer.setScale(1);
                Bitmap pica = BitmapFactory.decodeResource(getResources(), stickerResId);

                ImageEntity entity = new ImageEntity(layer, pica, motionView.getWidth(), motionView.getHeight());

                motionView.addEntityAndPosition(entity);
            }
        });
    }

    private void initTextEntitiesListeners() {
        findViewById(R.id.text_entity_font_size_increase).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                increaseTextEntitySize();
            }
        });
        findViewById(R.id.text_entity_font_size_decrease).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decreaseTextEntitySize();
            }
        });
        findViewById(R.id.text_entity_color_change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeTextEntityColor();
            }
        });
        findViewById(R.id.text_entity_font_change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeTextEntityFont();
            }
        });
        findViewById(R.id.text_entity_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTextEntityEditing();
            }
        });
    }

    private void increaseTextEntitySize() {
        TextEntity textEntity = currentTextEntity();
        if (textEntity != null) {
            textEntity.getLayer().getFont().increaseSize(TextLayer.Limits.FONT_SIZE_STEP);
            textEntity.updateEntity();
            motionView.invalidate();
        }
    }

    private void decreaseTextEntitySize() {
        TextEntity textEntity = currentTextEntity();
        if (textEntity != null) {
            textEntity.getLayer().getFont().decreaseSize(TextLayer.Limits.FONT_SIZE_STEP);
            textEntity.updateEntity();
            motionView.invalidate();
        }
    }

    private void changeTextEntityColor() {
        TextEntity textEntity = currentTextEntity();
        if (textEntity == null) {
            return;
        }

        int initialColor = textEntity.getLayer().getFont().getColor();

        ColorPickerDialogBuilder
                .with(ImageActivity.this)
                .setTitle(R.string.select_color)
                .initialColor(initialColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .density(8) // magic number
                .setPositiveButton(R.string.ok, new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        TextEntity textEntity = currentTextEntity();
                        if (textEntity != null) {
                            textEntity.getLayer().getFont().setColor(selectedColor);
                            textEntity.updateEntity();
                            motionView.invalidate();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }

    private void changeTextEntityFont() {
        final List<String> fonts = fontProvider.getFontNames();
        FontsAdapter fontsAdapter = new FontsAdapter(this, fonts, fontProvider);
        new AlertDialog.Builder(this)
                .setTitle(R.string.select_font)
                .setAdapter(fontsAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        TextEntity textEntity = currentTextEntity();
                        if (textEntity != null) {
                            textEntity.getLayer().getFont().setTypeface(fonts.get(which));
                            textEntity.updateEntity();
                            motionView.invalidate();
                        }
                    }
                })
                .show();
    }

    private void startTextEntityEditing() {
        TextEntity textEntity = currentTextEntity();
        if (textEntity != null) {
            TextEditorDialogFragment fragment = TextEditorDialogFragment.getInstance(textEntity.getLayer().getText());
            fragment.show(getFragmentManager(), TextEditorDialogFragment.class.getName());
        }
    }

    @Nullable
    private TextEntity currentTextEntity() {
        if (motionView != null && motionView.getSelectedEntity() instanceof TextEntity) {
            return ((TextEntity) motionView.getSelectedEntity());
        } else {
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.main_add_sticker) {
            Intent intent = new Intent(this, StickerSelectActivity.class);
            startActivityForResult(intent, SELECT_STICKER_REQUEST_CODE);
            return true;
        } else if (item.getItemId() == R.id.main_add_text) {
            addTextSticker();
        } else if (item.getItemId() == R.id.save) {
            Intent intent = new Intent(this, AgreementActivity.class);
            intent.putIntegerArrayListExtra("usedStickers", usedStickers);
            startActivityForResult(intent, SAVE_REQUEST);

        } else if (item.getItemId() == R.id.share) {
            Intent intent = new Intent(this, AgreementActivity.class);
            intent.putIntegerArrayListExtra("usedStickers", usedStickers);
            startActivityForResult(intent, SHARE_REQUEST);
        } else if (item.getItemId() == R.id.shareGeo) {
            Bitmap bitmap = motionView.getThumbnailImage();
            FirebaseAuth firebaseAuth;
            firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser user = firebaseAuth.getCurrentUser();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            for(int sticker: usedStickers){
                StorageReference mountainsRef = storageRef.child(sticker+"/"+ user.getUid()+".jpg");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                UploadTask uploadTask = mountainsRef.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.

                    }
                });
            }
        }
        return super.onOptionsItemSelected(item);
    }


    private void saveImage(Bitmap finalBitmap, String image_name) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root);
        myDir.mkdirs();
        String fname = image_name + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        Log.i("LOAD", root + fname);
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void shareImage(Bitmap bitmap) {
        // save bitmap to cache directory
        Uri contentUri = getUri(bitmap);

        if (contentUri != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND );
            shareIntent.putExtra(Intent.EXTRA_TEXT, StickerSelectActivity.getCaption(usedStickers));
            shareIntent.setType("text/plain");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
            shareIntent.setDataAndType(contentUri, getContentResolver().getType(contentUri));
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            shareIntent.setType("image/png");
            startActivity(Intent.createChooser(shareIntent, "Choose an app"));
        }
    }

    private Uri getUri(Bitmap bitmap) {
        try {
            File cachePath = new File(this.getCacheDir(), "images");
            cachePath.mkdirs(); // don't forget to make the directory
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        File imagePath = new File(this.getCacheDir(), "images");
        File newFile = new File(imagePath, "image.png");
        return FileProvider.getUriForFile(this, "com.mvc.imagepicker.provider", newFile);
    }

    protected void addTextSticker() {
        TextLayer textLayer = createTextLayer();
        TextEntity textEntity = new TextEntity(textLayer, motionView.getWidth(),
                motionView.getHeight(), fontProvider);
        motionView.addEntityAndPosition(textEntity);

        // move text sticker up so that its not hidden under keyboard
        PointF center = textEntity.absoluteCenter();
        center.y = center.y * 0.5F;
        textEntity.moveCenterTo(center);

        // redraw
        motionView.invalidate();

        startTextEntityEditing();
    }

    private TextLayer createTextLayer() {
        TextLayer textLayer = new TextLayer();
        Font font = new Font();

        font.setColor(TextLayer.Limits.INITIAL_FONT_COLOR);
        font.setSize(TextLayer.Limits.INITIAL_FONT_SIZE);
        font.setTypeface(fontProvider.getDefaultFontName());

        textLayer.setFont(font);

        if (BuildConfig.DEBUG) {
            textLayer.setText("Hello, world :))");
        }

        return textLayer;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_STICKER_REQUEST_CODE) {
                if (data != null) {
                    int stickerId = data.getIntExtra(StickerSelectActivity.EXTRA_STICKER_ID, 0);
                    if (stickerId != 0) {
                        addSticker(stickerId);
                    }
                }
            } else if (requestCode == SAVE_REQUEST) {
                if (data.getBooleanExtra("result", true)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Enter file name");

                    final EditText input = new EditText(this);
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);

                    builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            m_Text = input.getText().toString();
                            Bitmap bitmap = motionView.getThumbnailImage();
                            saveImage(bitmap, m_Text);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                }
            } else if (requestCode == SHARE_REQUEST) {
                if (data.getBooleanExtra("result", true)) {

                    Bitmap bitmap = motionView.getThumbnailImage();
                    shareImage(bitmap);
                }
            }
        }
    }

    @Override
    public void textChanged(@NonNull String text) {
        TextEntity textEntity = currentTextEntity();
        if (textEntity != null) {
            TextLayer textLayer = textEntity.getLayer();
            if (!text.equals(textLayer.getText())) {
                textLayer.setText(text);
                textEntity.updateEntity();
                motionView.invalidate();
            }
        }
    }

}
