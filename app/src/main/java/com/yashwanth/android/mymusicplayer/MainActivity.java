package com.yashwanth.android.mymusicplayer;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaDrm;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private String[] song_names;
    private ArrayList<File> mySongs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.songs_list);
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @RequiresApi(api = Build.VERSION_CODES.R)
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Toast.makeText(MainActivity.this,"Permission Granted",Toast.LENGTH_SHORT).show();

                        String sd_card = Environment.getExternalStorageDirectory().toString();
                        File file = new File(sd_card);
                        mySongs = fetchSongs(file);

                        song_names = new String[mySongs.size()];
                        for(int i=0;i<mySongs.size();i++){
                            song_names[i] = mySongs.get(i).getName().replace(".mp3","");
                        }
                        customAdapter adapter = new customAdapter();
                        listView.setAdapter(adapter);

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Log.d("clicked", "Item Clicked");
                                String songName = song_names[position];
                                Log.d("songName", "Main Activity : "+songName);
                                Intent intent = new Intent(getApplicationContext(),song_activity.class);
                                intent.putExtra("song_file",mySongs);
                                intent.putExtra("position",position);
                                intent.putExtra("song_name",songName);
                                startActivity(intent);
                            }
                        });

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                })
                .check();
    }
    private ArrayList<File> fetchSongs(File file){
        ArrayList<File> arrayList = new ArrayList<>();
        File[] songs = file.listFiles();
        if(songs!=null){
            for(File myFile:songs){
                if(!myFile.isHidden() && myFile.isDirectory()){
                    arrayList.addAll(fetchSongs(myFile));
                }
                else if(myFile.getName().endsWith(".mp3") && !myFile.getName().startsWith(".")){
                    arrayList.add(myFile);
                }
            }
        }

        return arrayList;
    }
    private class customAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return song_names.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            @SuppressLint("ViewHolder") View view = getLayoutInflater().inflate(R.layout.list_item,null);
            TextView song_name = view.findViewById(R.id.song_name);
            song_name.setSelected(true);
            song_name.setText(song_names[position]);
            return view;
        }

    }
}
