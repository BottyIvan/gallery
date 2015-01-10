package com.botty.gallery.Activity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.VideoView;

import com.botty.gallery.R;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class FullVideoView extends ActionBarActivity {

    private final String TAG = "main";
    public String filepath;
    public String file_name;
    private int position = 0;
    boolean on;
    private boolean isPlaying;
    int msec;

    VideoView videoView;
    ToggleButton mPlayVideo;
    MediaController.MediaPlayerControl videoControl;
    TextView mVideoTime,mCurrentVideoTime;
    SeekBar mVideoSeek;
    FrameLayout mVideoControl;
    View decorView;

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
        videoView = (VideoView) findViewById(R.id.video_full);
        mPlayVideo = (ToggleButton) findViewById(R.id.play_pause);
        mVideoTime = (TextView) findViewById(R.id.text_time_video);
        mVideoSeek = (SeekBar) findViewById(R.id.seek_video_bar);
        mCurrentVideoTime = (TextView) findViewById(R.id.time_progress);
        mVideoControl = (FrameLayout) findViewById(R.id.video_control);

        mVideoControl.setVisibility(View.GONE);


        if (videoView != null) {
            videoView.setVideoURI(Uri.parse(filepath));
            Log.i(TAG, "Start playing");
            videoView.start();
            // Play according to the initial position
            videoView.seekTo(msec);
            // Maximum play time length setting maximum progress bar for streaming video
            mVideoSeek.setMax(videoView.getDuration());
            // To update the progress bar thread, scale
            new Thread() {
                    @Override
                    public void run() {
                        try {
                            isPlaying = true;
                            int position = videoView.getCurrentPosition();
                            int duration = videoView.getDuration();
                            if (isPlaying != false) {
                                if (duration > 0) {
                                    // use long to avoid overflow
                                    long pos = 1000L * position / duration;
                                    mVideoSeek.setProgress((int) pos);
                                }
                                int percent = videoControl.getBufferPercentage();
                                mVideoSeek.setSecondaryProgress(percent * 10);
                            }

                            if (mVideoTime != null)
                                mVideoTime.setText(stringForTime(duration));

                            if (mCurrentVideoTime != null)
                                mCurrentVideoTime.setText(stringForTime(position));
                            } catch (Exception e) {
                                      e.printStackTrace();
                        }
                    }
            }.start();

            videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                // Error resume play
                isPlaying = false;
                return false;
                   }
            });
        }



        mPlayVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                on = ((ToggleButton) v).isChecked();
                if (on) {
                    videoView.pause();
                } else {
                    videoView.start();
                }
            }
        });


        mVideoSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                if (videoView != null && videoView.isPlaying()) {
                    videoView.seekTo(progress);
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
                            YoYo.with(Techniques.FadeInUp).playOn(mVideoControl);
                            toolbar.setVisibility(View.VISIBLE);
                        }
                        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 1) {
                            mVideoControl.setVisibility(View.GONE);
                            YoYo.with(Techniques.Wobble).playOn(mVideoControl);
                            toolbar.setVisibility(View.INVISIBLE);
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

    private String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours   = totalSeconds / 3600;

        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        //we use onSaveInstanceState in order to store the video playback position for orientation change
        savedInstanceState.putInt("Position", videoView.getCurrentPosition());
        videoView.pause();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //we use onRestoreInstanceState in order to play the video playback from the stored position
        position = savedInstanceState.getInt("Position");
        videoView.seekTo(position);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_full_video_view, menu);
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
