package com.botty.gallery.Fragment;

/**
 * Created by ivanbotty on 06/12/14.
 */

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.botty.gallery.R;


public class GalleryLocal extends Fragment {

    //define source of MediaStore.Images.Media, internal or external storage
    final Uri sourceUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    final Uri thumbUri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;
    final String thumb_DATA = MediaStore.Images.Thumbnails.DATA;
    final String thumb_IMAGE_ID = MediaStore.Images.Thumbnails.IMAGE_ID;

    //SimpleCursorAdapter mySimpleCursorAdapter;
    MyAdapter mySimpleCursorAdapter;

    GridView myGridView;

    public GalleryLocal() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.gallery_local, container,
                false);

        myGridView = (GridView)view.findViewById(R.id.results);

        String[] from = {MediaStore.MediaColumns.TITLE};
        int[] to = {android.R.id.text1};

        CursorLoader cursorLoader = new CursorLoader(
                getActivity(),
                sourceUri,
                null,
                null,
                null,
                MediaStore.Audio.Media.TITLE);

        Cursor cursor = cursorLoader.loadInBackground();

        mySimpleCursorAdapter = new MyAdapter(
                getActivity(),
                android.R.layout.simple_list_item_1,
                cursor,
                from,
                to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        myGridView.setAdapter(mySimpleCursorAdapter);
        myGridView.setOnItemClickListener(myOnItemClickListener);
        return view;
    }

    AdapterView.OnItemClickListener myOnItemClickListener
            = new AdapterView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            Cursor cursor = mySimpleCursorAdapter.getCursor();
            cursor.moveToPosition(position);

            int int_ID = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID));
            getThumbnail(int_ID);
        }};

    private Bitmap getThumbnail(int id){

        String[] thumbColumns = {thumb_DATA, thumb_IMAGE_ID};

        CursorLoader thumbCursorLoader = new CursorLoader(
                getActivity(),
                thumbUri,
                thumbColumns,
                thumb_IMAGE_ID + "=" + id,
                null,
                null);

        Cursor thumbCursor = thumbCursorLoader.loadInBackground();

        Bitmap thumbBitmap = null;
        if(thumbCursor.moveToFirst()){
            int thCulumnIndex = thumbCursor.getColumnIndex(thumb_DATA);

            String thumbPath = thumbCursor.getString(thCulumnIndex);

            Toast.makeText(getActivity(),
                    thumbPath,
                    Toast.LENGTH_LONG).show();

            thumbBitmap = BitmapFactory.decodeFile(thumbPath);

            //Create a Dialog to display the thumbnail

            AlertDialog.Builder thumbDialog = new AlertDialog.Builder(getActivity());
            ImageView thumbView = new ImageView(getActivity());
            thumbView.setImageBitmap(thumbBitmap);
            LinearLayout layout = new LinearLayout(getActivity());
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.addView(thumbView);
            thumbDialog.setView(layout);
            thumbDialog.show();

        }else{
            Toast.makeText(getActivity(),
                    "NO Thumbnail!",
                    Toast.LENGTH_LONG).show();
        }

        return thumbBitmap;
    }

    public class MyAdapter extends SimpleCursorAdapter {

        Cursor myCursor;
        Context myContext;

        public MyAdapter(Context context, int layout, Cursor c, String[] from,
                         int[] to, int flags) {
            super(context, layout, c, from, to, flags);

            myCursor = c;
            myContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            if(row==null){
                LayoutInflater inflater=getActivity().getLayoutInflater();
                row=inflater.inflate(R.layout.image, parent, false);
            }

            ImageView thumbV = (ImageView)row.findViewById(R.id.image);

            myCursor.moveToPosition(position);

            int myID = myCursor.getInt(myCursor.getColumnIndex(MediaStore.Images.Media._ID));

            String[] thumbColumns = {thumb_DATA, thumb_IMAGE_ID};
            CursorLoader thumbCursorLoader = new CursorLoader(
                    myContext,
                    thumbUri,
                    thumbColumns,
                    thumb_IMAGE_ID + "=" + myID,
                    null,
                    null);
            Cursor thumbCursor = thumbCursorLoader.loadInBackground();

            Bitmap myBitmap = null;
            if(thumbCursor.moveToFirst()){
                int thCulumnIndex = thumbCursor.getColumnIndex(thumb_DATA);
                String thumbPath = thumbCursor.getString(thCulumnIndex);
                myBitmap = BitmapFactory.decodeFile(thumbPath);
                thumbV.setImageBitmap(myBitmap);
            }

            return row;
        }

    }
}