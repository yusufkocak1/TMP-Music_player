package com.yube.TMP;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.os.EnvironmentCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.jean.jcplayer.JcAudio;
import com.example.jean.jcplayer.JcPlayerView;

import com.example.jean.jcplayer.JcStatus;
import com.yube.TMP.Contact.PlayListContact;
import com.yube.TMP.process.Database;
import com.yube.TMP.process.getplaylist;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends Activity
        implements JcPlayerView.OnInvalidPathListener, JcPlayerView.JcPlayerViewStatusListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private JcPlayerView player;
    private RecyclerView recyclerView;
    private AudioAdapter audioAdapter;
    Database db = new Database(this);
    ImageButton scanbtn;
    ArrayList<JcAudio> jcAudios;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        player = (JcPlayerView) findViewById(R.id.jcplayer);

        jcAudios = new ArrayList<>();
// ist veritabanına kaydedilecek.
        //veritabanından cekilen veri bir hashmape eklenecek.
// name path category
        final alert scanalert = new alert();

        db.open();

        dbread();

        scanbtn = (ImageButton) findViewById(R.id.scanbtn);


        scanbtn.setOnClickListener(new View.OnClickListener() {


            public void onClick(View v) {

                        scanalert.showalert(MainActivity.this);


                Thread threadscan = new Thread(new Runnable() {
                    @Override
                    public void run() {


                        ArrayList<HashMap<String, String>> ist = new ArrayList<>(new getplaylist().getPlayList(getExternalStorageDirectories().get(0).toString()));

                        db.delete();
                        for (HashMap<String, String> item : ist
                                ) {

                            db.create(item.get("file_name").toString(), item.get("file_path").toString(), "Default");

                        }
                        scanalert.dialog.dismiss();
                        jcAudios=dbread();
                        finish();
                        startActivity(getIntent());
                    }
                });
                threadscan.start();


            }
        });


        //jcAudios.add(JcAudio.createFromURL("url audio","http://www.villopim.com.br/android/Music_01.mp3"));
       /* jcAudios.add(JcAudio.createFromAssets("Asset audio 1", "49.v4.mid"));
        jcAudios.add(JcAudio.createFromAssets("Asset audio 2", "56.mid"));
        jcAudios.add(JcAudio.createFromAssets("Asset audio 3", "a_34.mp3"));
        jcAudios.add(JcAudio.createFromRaw("Raw audio 1", R.raw.a_34));
        jcAudios.add(JcAudio.createFromRaw("Raw audio 2", R.raw.a_203));*/
        //jcAudios.add(JcAudio.createFromFilePath("File directory audio", this.getFilesDir() + "/" + "CANTO DA GRAÚNA.mp3"));
        //jcAudios.add(JcAudio.createFromAssets("I am invalid audio", "aaa.mid")); // invalid assets file

        if (jcAudios.size() > 0) {
            player.initPlaylist(jcAudios);
            player.registerInvalidPathListener(this);
            player.registerStatusListener(this);
            adapterSetup();
        }

//        jcAudios.add(JcAudio.createFromFilePath("test", this.getFilesDir() + "/" + "13.mid"));
//        jcAudios.add(JcAudio.createFromFilePath("test", this.getFilesDir() + "/" + "123123.mid")); // invalid file path
//        jcAudios.add(JcAudio.createFromAssets("49.v4.mid"));
//        jcAudios.add(JcAudio.createFromRaw(R.raw.a_203));
//        jcAudios.add(JcAudio.createFromRaw("a_34", R.raw.a_34));
//        player.initWithTitlePlaylist(jcAudios, "Awesome music");


//        jcAudios.add(JcAudio.createFromFilePath("test", this.getFilesDir() + "/" + "13.mid"));
//        jcAudios.add(JcAudio.createFromFilePath("test", this.getFilesDir() + "/" + "123123.mid")); // invalid file path
//        jcAudios.add(JcAudio.createFromAssets("49.v4.mid"));
//        jcAudios.add(JcAudio.createFromRaw(R.raw.a_203));
//        jcAudios.add(JcAudio.createFromRaw("a_34", R.raw.a_34));
//        player.initAnonPlaylist(jcAudios);

//        Adding new audios to playlist
//        player.addAudio(JcAudio.createFromURL("url audio","http://www.villopim.com.br/android/Music_01.mp3"));
//        player.addAudio(JcAudio.createFromAssets("49.v4.mid"));
//        player.addAudio(JcAudio.createFromRaw(R.raw.a_34));
//        player.addAudio(JcAudio.createFromFilePath(this.getFilesDir() + "/" + "121212.mmid"));


    }

    protected void adapterSetup() {
        audioAdapter = new AudioAdapter(player.getMyPlaylist());
        audioAdapter.setOnItemClickListener(new AudioAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                player.playAudio(player.getMyPlaylist().get(position));
            }

            @Override
            public void onSongItemDeleteClicked(int position) {
                Toast.makeText(MainActivity.this, "Delete song at position " + position,
                        Toast.LENGTH_SHORT).show();
//                if(player.getCurrentPlayedAudio() != null) {
//                    Toast.makeText(MainActivity.this, "Current audio = " + player.getCurrentPlayedAudio().getPath(),
//                            Toast.LENGTH_SHORT).show();
//                }
                removeItem(position);
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(audioAdapter);

        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

    }

    @Override
    public void onPause() {
        super.onPause();
        player.createNotification();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.kill();
    }

    @Override
    public void onPathError(JcAudio jcAudio) {
        Toast.makeText(this, jcAudio.getPath() + " with problems", Toast.LENGTH_LONG).show();
//        player.removeAudio(jcAudio);
//        player.next();
    }

    private void removeItem(int position) {
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(true);

        //        jcAudios.remove(position);
        player.removeAudio(player.getMyPlaylist().get(position));
        audioAdapter.notifyItemRemoved(position);

        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    @Override
    public void onPausedStatus(JcStatus jcStatus) {

    }

    @Override
    public void onContinueAudioStatus(JcStatus jcStatus) {

    }

    @Override
    public void onPlayingStatus(JcStatus jcStatus) {

    }

    @Override
    public void onTimeChangedStatus(JcStatus jcStatus) {
        updateProgress(jcStatus);
    }

    @Override
    public void onCompletedAudioStatus(JcStatus jcStatus) {
        updateProgress(jcStatus);
    }

    @Override
    public void onPreparedAudioStatus(JcStatus jcStatus) {

    }

    private void updateProgress(final JcStatus jcStatus) {
        Log.d(TAG, "Song id = " + jcStatus.getJcAudio().getId() + ", song duration = " + jcStatus.getDuration()
                + "\n song position = " + jcStatus.getCurrentPosition());

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // calculate progress
                float progress = (float) (jcStatus.getDuration() - jcStatus.getCurrentPosition())
                        / (float) jcStatus.getDuration();
                progress = 1.0f - progress;
                audioAdapter.updateProgress(jcStatus.getJcAudio(), progress);
            }
        });
    }

    public List getExternalStorageDirectories() {

        List<String> results = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //Method 1 for KitKat & above
            File[] externalDirs = getExternalFilesDirs(null);

            for (File file : externalDirs) {
                String path = file.getPath().split("/Android")[0];

                boolean addPath = false;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    addPath = Environment.isExternalStorageRemovable(file);
                } else {
                    addPath = Environment.MEDIA_MOUNTED.equals(EnvironmentCompat.getStorageState(file));
                }

                if (addPath) {
                    results.add(path);
                }
            }
        }

        if (results.isEmpty()) { //Method 2 for all versions
            // better variation of: http://stackoverflow.com/a/40123073/5002496
            String output = "";
            try {
                final Process process = new ProcessBuilder().command("mount | grep /dev/block/vold").redirectErrorStream(true).start();
                process.waitFor();
                final InputStream is = process.getInputStream();
                final byte[] buffer = new byte[1024];
                while (is.read(buffer) != -1) {
                    output = output + new String(buffer);
                }
                is.close();
            } catch (final Exception e) {
                e.printStackTrace();
            }
            if (!output.trim().isEmpty()) {
                String devicePoints[] = output.split("\n");
                for (String voldPoint : devicePoints) {
                    results.add(voldPoint.split(" ")[2]);
                }
            }
        }

        return results;
      /*  String[] storageDirectories = new String[results.size()];
        for(int i=0; i<results.size(); ++i) storageDirectories[i] = results.get(i);

        return storageDirectories;*/
    }


    public ArrayList dbread(){
        ArrayList<PlayListContact> playList = new ArrayList<>();
        try {
            playList = db.vericek();
        } catch (Exception e) {
            ArrayList<HashMap<String, String>> ist = new ArrayList<>(new getplaylist().getPlayList(getExternalStorageDirectories().get(0).toString()));

            db.delete();
            for (HashMap<String, String> item : ist
                    ) {

                db.create(item.get("file_name").toString(), item.get("file_path").toString(), "Default");

            }
        }


        for (int i = 0; i < playList.size(); i++) {
            jcAudios.add(JcAudio.createFromFilePath(playList.get(i).getName(), playList.get(i).getPath()));


        }
        db.close();
        return  jcAudios;
    }

public void initplayer(ArrayList jcAudios){
    if (jcAudios.size() > 0) {
        player.initPlaylist(jcAudios);
        player.registerInvalidPathListener(this);
        player.registerStatusListener(this);
        adapterSetup();
    }
}

    class alert {
        Dialog dialog;

        public void showalert(Activity activity) {
            dialog = new Dialog(activity);

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.scan_alert);
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
            //   progressBar.setVisibility(View.VISIBLE);
            //  progressBar.setMax(150);
            dialog.show();

        }

    }

}