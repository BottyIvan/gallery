package com.botty.gallery.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.botty.gallery.Activity.FullPhotoView;
import com.botty.gallery.R;
import com.koushikdutta.ion.Ion;
import java.io.File;


/**
 * Created by ivanbotty on 09/12/14.
 */
public class GalleryLocalwithIon extends Fragment {

    public String filePath;

    public GalleryLocalwithIon() {

    }

    public MyAdapter mAdapter;
    // Adapter to populate and imageview from an url contained in the array adapter
    private class MyAdapter extends ArrayAdapter<String> {
        public MyAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
        // see if we need to load more to get 40, otherwise populate the adapter
            if (position > getCount() - 4)
                loadMore();
            if (convertView == null)
                convertView = getActivity().getLayoutInflater().inflate(R.layout.image, null);
            // find the image view
            final ImageView iv = (ImageView) convertView.findViewById(R.id.image);
            // select the image view
            Ion.with(iv)
                    .centerCrop()
                    .error(Drawable.createFromPath("Error"))
                    .load(getItem(position));
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(),FullPhotoView.class);
                    filePath = mAdapter.getItem(position);
                    i.putExtra("data",filePath);
                    Log.i("position",filePath);
                    startActivity(i);
                }
            });
            return convertView;
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.gallery_local, container,
                false);


        Ion.getDefault(getActivity()).configure().setLogging("ion-sample", Log.DEBUG);
        int cols = getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().densityDpi * 2;
        GridView gridView = (GridView) view.findViewById(R.id.results);

        mAdapter = new MyAdapter(getActivity());
        gridView.setAdapter(mAdapter);
        loadMore();
        return view;
    }
    Cursor mediaCursor;
    public void loadMore() {
        if (mediaCursor == null) {
            mediaCursor = getActivity().getContentResolver().query(MediaStore.Files.getContentUri("external"), null, null, null, null);
        }
        int loaded = 0;
        while (mediaCursor.moveToNext() && loaded < 10) {
        // get the media type. ion can show images for both regular images AND video.
            int mediaType = mediaCursor.getInt(mediaCursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE));
            if (mediaType != MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
                continue;
            }
            loaded++;
            String uri = mediaCursor.getString(mediaCursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
            File file = new File(uri);
            // turn this into a file uri if necessary/possible
            if (file.exists())
                mAdapter.add(file.toURI().toString());
            else
                mAdapter.add(uri);
        }
    }
}