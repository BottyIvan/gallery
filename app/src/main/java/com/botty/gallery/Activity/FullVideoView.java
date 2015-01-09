package com.botty.gallery.Activity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.VideoView;

import com.botty.gallery.R;
import com.koushikdutta.ion.Ion;

import org.w3c.dom.Text;

import java.io.File;

import uk.co.senab.photoview.PhotoViewAttacher;

public class FullVideoView extends ActionBarActivity {

    public String filepath;
    public String file_name;
    VideoView videoView;
    ToggleButton mPlayVideo;
    TextView mVideoTime;
    ProgressBar mVideoProgress;
    FrameLayout mVideoControl;
    View decorView;
    boolean on;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_video_view);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        decorView = getWindow().getDecorView();
        hideSystemUI();
        toolbar.setVisibility(View.GONE);

        filepath = this.getIntent().getStringExtra("data");
        videoView  = (VideoView)findViewById(R.id.video_full);
        mPlayVideo = (ToggleButton)findViewById(R.id.play_pause);
        mVideoTime = (TextView)findViewById(R.id.text_time_video);
        mVideoProgress = (ProgressBar) findViewById(R.id.video_progress);
        mVideoControl = (FrameLayout) findViewById(R.id.video_control);

        toolbar.setTitle(filepath);


        mVideoControl.setVisibility(View.GONE);
        mVideoProgress.setProgress(0);
        mVideoProgress.setMax(100);


        if ( videoView != null)
        {   videoView.setVideoURI(Uri.parse(filepath));
            videoView.requestFocus();
            new myAsync().execute();
            videoView.start();
        } else
        { //toast or print "mVideoView is null"
        }

        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVideoControl.setVisibility(View.VISIBLE);
            }
        });

        mPlayVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                on = ((ToggleButton) v).isChecked();
                if(on) {
                    videoView.pause();
                }else {
                    videoView.start();
                }
            }
        });

        decorView.setOnSystemUiVisibilityChangeListener
                (new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        // Note that system bars will only be "visible" if none of the
                        // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
                        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                            mVideoControl.setVisibility(View.VISIBLE);
                            toolbar.setVisibility(View.VISIBLE);
                        }else {
                            mVideoControl.setVisibility(View.GONE);
                        }
                    }
                });
    }

    // This snippet hides the system bars.
    private void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    // This snippet shows the system bars. It does this by removing all the flags
    // except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_VISIBLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    private class myAsync extends AsyncTask<Void, Integer, Void>
    {
        int duration = 0;
        int current = 0;
        @Override
        protected Void doInBackground(Void... params) {

            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                public void onPrepared(MediaPlayer mp) {
                    duration = videoView.getDuration();
                }
            });

            do {
                current = videoView.getCurrentPosition();
               /* System.out.println("duration - " + duration + " current- "
                        + current);*/
                try {
                    publishProgress((int) (current * 100 / duration));
                    if(mVideoProgress.getProgress() >= 100){
                        videoView.stopPlayback();
                        break;
                    }
                } catch (Exception e) {
                }
            } while (mVideoProgress.getProgress() <= 100);

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            //System.out.println(values[0]);
            mVideoProgress.setProgress(values[0]);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
        }
    }

    @Override
    public void onResume(){
        super.onResume();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            return true;
        }
        if (id == R.id.action_info) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
