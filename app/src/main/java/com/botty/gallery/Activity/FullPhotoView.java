package com.botty.gallery.Activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import com.botty.gallery.R;
import com.koushikdutta.ion.Ion;

import java.io.File;

import uk.co.senab.photoview.PhotoViewAttacher;

public class FullPhotoView extends ActionBarActivity {

    public String filepath;
    ImageView imageView;
    PhotoViewAttacher mAttacher;
    View decorView;
    Toolbar toolbar;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        decorView = getWindow().getDecorView();

        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        setContentView(R.layout.full_image);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setVisibility(View.GONE);

        filepath = this.getIntent().getStringExtra("data");
        imageView= (ImageView)findViewById(R.id.imgDisplay);
        Ion.with(imageView).load(filepath);

        // Attach a PhotoViewAttacher, which takes care of all of the zooming functionality.
        mAttacher = new PhotoViewAttacher(imageView);
        mAttacher.getScaleType();
        mAttacher.update();


        decorView.setOnSystemUiVisibilityChangeListener
                (new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        // Note that system bars will only be "visible" if none of the
                        // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
                        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                            toolbar.setVisibility(View.VISIBLE);
                        }else {
                        }
                    }
                });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_delete) {
            return true;
        }
        if (id == R.id.action_info) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
