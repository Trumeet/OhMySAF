package moe.yuuta.ohmysafdemo;

import android.annotation.NonNull;
import android.annotation.Nullable;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import moe.yuuta.ohmysaf.OhMySAF;
import moe.yuuta.ohmysaf.SafFile;

public class MainActivity extends Activity implements View.OnClickListener {
    private static final int RC_OPEN_DOC = 1;
    private static final int RC_CREATE_DOC = 2;
    private static final int RC_OPEN_TREE = 3;

    private ImageView mImage;
    private volatile Bitmap mBitmap;
    private File mFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(android.R.id.button1).setOnClickListener(this);
        findViewById(android.R.id.button2).setOnClickListener(this);
        mImage = findViewById(R.id.image);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case android.R.id.button1:
                runOpenDoc();
                break;
            case android.R.id.button2:
                runOpenTree();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        log("Activity result.....");
        if (resultCode != RESULT_OK || data == null) return;
        final Uri mUri = data.getData();
        log(mUri.toString());
        mFile = OhMySAF.ohMyUri(this, mUri);
        log("Name: " + mFile.getName() +
                ", isFile: " + mFile.isFile() +
                ", length: " + mFile.length() +
                ", canWrite: " + mFile.canWrite() +
                ", files: " + (mFile.isDirectory() ? Arrays.toString(mFile.list()) : "?"));
        setTitle(mFile.getName());
        switch (requestCode) {
            case RC_OPEN_DOC:
                handleDocSelected();
                break;
            case RC_CREATE_DOC:
                handleDocCreated();
                break;
            case RC_OPEN_TREE:
                mBitmap = null;
                mImage.setImageBitmap(null);
                break;
        }
    }

    private void handleDocCreated() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OutputStream stream = getContentResolver().openOutputStream(((SafFile) mFile).getAndroidUri(), "w");
                    mBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    stream.flush();
                    stream.close();
                    log("Write done");
                    handleDocSelected();
                } catch (IOException e) {
                    log("Write: " + e.getMessage());
                }
            }
        }).start();
    }

    private void handleDocSelected() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ParcelFileDescriptor parcelFileDescriptor =
                            getContentResolver().openFileDescriptor(((SafFile) mFile).getAndroidUri(), "r");
                    FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                    mBitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                    parcelFileDescriptor.close();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mImage.setImageBitmap(mBitmap);
                        }
                    });
                } catch (final IOException e) {
                    log(e.getMessage());
                }
            }
        }).start();
    }

    private void log(@NonNull final String message) {
        Log.i("OhMySAF", message);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "Make a copy");
        menu.add(0, 1, 0, "Delete (without confirmation!!!)");
        menu.add(0, 2, 0, "Take persistable uri permission");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mBitmap == null) {
            log("Select a photo and try again");
            return false;
        }
        switch (item.getItemId()) {
            case 0:
                runCreateDoc();
                return true;
            case 1:
                runDeleteDoc();
                return true;
            case 2:
                runTakePersistablePermission();
                return true;
            default:
                return false;
        }
    }

    private void runOpenDoc() {
        Intent intentOpenDoc = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intentOpenDoc.addCategory(Intent.CATEGORY_OPENABLE);
        intentOpenDoc.setType("image/*");
        startActivityForResult(intentOpenDoc, RC_OPEN_DOC);
    }

    private void runCreateDoc() {
        Intent intentCreateDoc = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intentCreateDoc.addCategory(Intent.CATEGORY_OPENABLE);
        intentCreateDoc.setType("image/*");
        intentCreateDoc.putExtra(Intent.EXTRA_TITLE, "Copy_" + mFile.getName());
        startActivityForResult(intentCreateDoc, RC_CREATE_DOC);
    }

    private void runDeleteDoc() {
        try {
            mFile.delete();
            log("Delete done");
            mBitmap = null;
            mFile = null;
            mImage.setImageBitmap(null);
            setTitle(R.string.app_name);
        } catch (Exception e) {
            log("Delete: " + e.getMessage());
        }
    }

    private void runTakePersistablePermission() {
        getContentResolver().takePersistableUriPermission(((SafFile) mFile).getAndroidUri(),
                Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        log("Done");
    }

    private void runOpenTree() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, RC_OPEN_TREE);
    }
}
