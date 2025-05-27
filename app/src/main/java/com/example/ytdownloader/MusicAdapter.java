package com.example.ytdownloader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {

    private final Context context;
    private final List<File> musicFiles;
    private final OnPlayClickListener listener;

    public interface OnPlayClickListener {
        void onPlayClicked(File musicFile);
    }

    public MusicAdapter(Context context, List<File> musicFiles, OnPlayClickListener listener) {
        this.context = context;
        this.musicFiles = musicFiles;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_music, parent, false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        File musicFile = musicFiles.get(position);
        holder.txtTitle.setText(musicFile.getName());

        holder.btnPlay.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPlayClicked(musicFile);
            }
        });
    }

    @Override
    public int getItemCount() {
        return musicFiles.size();
    }

    static class MusicViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle;
        Button btnPlay;

        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtMusicTitle);
            btnPlay = itemView.findViewById(R.id.btnPlayMusic);
        }
    }


}

