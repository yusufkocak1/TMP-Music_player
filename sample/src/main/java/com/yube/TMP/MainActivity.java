package com.yube.TMP;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.widget.Toast;

import com.example.jean.jcplayer.JcAudio;
import com.example.jean.jcplayer.JcPlayerView;

import com.example.jean.jcplayer.JcStatus;
import com.yube.TMP.process.getplaylist;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends Activity
    implements JcPlayerView.OnInvalidPathListener, JcPlayerView.JcPlayerViewStatusListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private JcPlayerView player;
    private RecyclerView recyclerView;
    private AudioAdapter audioAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        player = (JcPlayerView) findViewById(R.id.jcplayer);
        ArrayList<HashMap<String, String>> ist=new ArrayList<>(new getplaylist().getPlayList("/storage/"));
        ArrayList<JcAudio> jcAudios = new ArrayList<>();

        for (HashMap<String, String> item:ist
             ) {
            jcAudios.add(JcAudio.createFromFilePath(item.get("file_name").toString(),item.get("file_path").toString()));

        }
        //jcAudios.add(JcAudio.createFromURL("url audio","http://www.villopim.com.br/android/Music_01.mp3"));
       /* jcAudios.add(JcAudio.createFromAssets("Asset audio 1", "49.v4.mid"));
        jcAudios.add(JcAudio.createFromAssets("Asset audio 2", "56.mid"));
        jcAudios.add(JcAudio.createFromAssets("Asset audio 3", "a_34.mp3"));
        jcAudios.add(JcAudio.createFromRaw("Raw audio 1", R.raw.a_34));
        jcAudios.add(JcAudio.createFromRaw("Raw audio 2", R.raw.a_203));*/
        //jcAudios.add(JcAudio.createFromFilePath("File directory audio", this.getFilesDir() + "/" + "CANTO DA GRAÚNA.mp3"));
        //jcAudios.add(JcAudio.createFromAssets("I am invalid audio", "aaa.mid")); // invalid assets file


        player.initPlaylist(jcAudios);


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

        player.registerInvalidPathListener(this);
        player.registerStatusListener(this);
        adapterSetup();
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
    public void onPause(){
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

    @Override public void onPausedStatus(JcStatus jcStatus) {

    }

    @Override public void onContinueAudioStatus(JcStatus jcStatus) {

    }

    @Override public void onPlayingStatus(JcStatus jcStatus) {

    }

    @Override public void onTimeChangedStatus(JcStatus jcStatus) {
        updateProgress(jcStatus);
    }

    @Override public void onCompletedAudioStatus(JcStatus jcStatus) {
        updateProgress(jcStatus);
    }

    @Override public void onPreparedAudioStatus(JcStatus jcStatus) {

    }

    private void updateProgress(final JcStatus jcStatus) {
        Log.d(TAG, "Song id = " + jcStatus.getJcAudio().getId() + ", song duration = " + jcStatus.getDuration()
            + "\n song position = " + jcStatus.getCurrentPosition());

        runOnUiThread(new Runnable() {
            @Override public void run() {
                // calculate progress
                float progress = (float) (jcStatus.getDuration() - jcStatus.getCurrentPosition())
                    / (float) jcStatus.getDuration();
                progress = 1.0f - progress;
                audioAdapter.updateProgress(jcStatus.getJcAudio(), progress);
            }
        });
    }
}