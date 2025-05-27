package com.example.ytdownloader;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private List<File> videoList;
    private Activity activity;

    public VideoAdapter(Activity activity, List<File> videoList) {
        this.activity = activity;
        this.videoList = videoList;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        File videoFile = videoList.get(position);
        holder.textView.setText(videoFile.getName());

        if (videoFile.exists()) {
            holder.videoView.setVideoPath(videoFile.getAbsolutePath());
            holder.videoView.requestFocus();

            holder.videoView.setOnPreparedListener(mp -> {
                mp.setLooping(true);
                holder.videoView.seekTo(100);
                holder.videoView.pause();
            });
            holder.videoView.setOnErrorListener((mp, what, extra) -> {
                Log.e("VideoAdapter", "VideoView hata: what=" + what + " extra=" + extra);
                return true;
            });

        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(activity, FullscreenVideoActivity.class);
            intent.putExtra("videoPath", videoFile.getAbsolutePath());
            activity.startActivity(intent);
            activity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public void updateData(List<File> newVideoList) {
        this.videoList = newVideoList;
        notifyDataSetChanged();
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        VideoView videoView;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.video_Model);
            videoView = itemView.findViewById(R.id.videoView);
        }
    }

}
