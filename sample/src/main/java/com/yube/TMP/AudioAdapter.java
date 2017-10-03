package com.yube.TMP;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jean.jcplayer.JcAudio;

import java.util.List;

/**
 * Created by jean on 27/06/16.
 */

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.AudioAdapterViewHolder> {
    public static final String TAG = AudioAdapter.class.getSimpleName();

    private List<JcAudio> jcAudioList;
    private SparseArray<Float> progressMap = new SparseArray<>();

    private static OnItemClickListener mListener;

    // Define the mListener interface
    public interface OnItemClickListener {
        void onItemClick(int position);
        void onSongItemDeleteClicked(int position);
    }

    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public AudioAdapter(List<JcAudio> jcAudioList) {
        this.jcAudioList = jcAudioList;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return jcAudioList.get(position).getId();
    }

    @Override
    public AudioAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.audio_item, parent, false);
        return new AudioAdapterViewHolder(view);
//        audiosViewHolder.itemView.setOnClickListener(this);
//        return audiosViewHolder;
    }

    @Override
    public void onBindViewHolder(AudioAdapterViewHolder holder, int position) {
        String title = position+1 + "    " + jcAudioList.get(position).getTitle();
        holder.audioTitle.setText(title);
        holder.itemView.setTag(jcAudioList.get(position));

        applyProgressPercentage(holder, progressMap.get(position, 0.0f));
    }

  /**
   * Applying percentage to progress.
   * @param holder ViewHolder
   * @param percentage in float value. where 1 is equals as 100%
   */
    private void applyProgressPercentage(AudioAdapterViewHolder holder, float percentage) {
        Log.d(TAG, "applyProgressPercentage() with percentage = " + percentage);
        LinearLayout.LayoutParams progress = (LinearLayout.LayoutParams) holder.viewProgress.getLayoutParams();
        LinearLayout.LayoutParams antiProgress = (LinearLayout.LayoutParams) holder.viewAntiProgress.getLayoutParams();

        progress.weight = percentage;
        holder.viewProgress.setLayoutParams(progress);

        antiProgress.weight = 1.0f - percentage;
        holder.viewAntiProgress.setLayoutParams(antiProgress);
    }

    @Override
    public int getItemCount() {
        return jcAudioList == null ? 0 : jcAudioList.size();
    }

    public void updateProgress(JcAudio jcAudio, float progress) {
        int position = jcAudioList.indexOf(jcAudio);
        Log.d(TAG, "Progress = " + progress);


        progressMap.put(position, progress);
        if(progressMap.size() > 1) {
            for(int i = 0; i < progressMap.size(); i++) {
                if(progressMap.keyAt(i) != position) {
                    Log.d(TAG, "KeyAt(" + i + ") = " + progressMap.keyAt(i));
                    notifyItemChanged(progressMap.keyAt(i));
                    progressMap.delete(progressMap.keyAt(i));
                }
            }
        }
        notifyItemChanged(position);
    }

    static class AudioAdapterViewHolder extends RecyclerView.ViewHolder{
        private TextView audioTitle;
        private Button btnDelete;
        private View viewProgress;
        private View viewAntiProgress;

        public AudioAdapterViewHolder(View view){
            super(view);
            this.audioTitle = (TextView) view.findViewById(R.id.audio_title);
            this.btnDelete = (Button) view.findViewById(R.id.btn_delete);
            viewProgress = view.findViewById(R.id.song_progress_view);
            viewAntiProgress = view.findViewById(R.id.song_anti_progress_view);

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mListener != null) {
                        mListener.onSongItemDeleteClicked(getAdapterPosition());
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    // Triggers click upwards to the adapter on click
                    if (mListener != null) mListener.onItemClick(getAdapterPosition());
                }
            });
        }
    }
}
