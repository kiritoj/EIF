package com.example.mifans.eif.other;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mifans.eif.Activities.MainActivity;
import com.example.mifans.eif.Activities.MoreActivity;
import com.example.mifans.eif.R;
import com.example.mifans.eif.Tools.MyImageLoader;

import org.w3c.dom.Text;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private List<Songbean> songbeans;
    private Context context;
    public RecyclerAdapter(List<Songbean> songbeans,Context context) {
        this.songbeans = songbeans;
        this.context = context;
    }

    @NonNull
    @Override

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.itemcollect,viewGroup,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Intent intent = new Intent(context,MoreActivity.class);
                Songbean songbean = songbeans.get(position);
                intent.putExtra("songbean",songbean);
                context.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Songbean songbean = songbeans.get(i);
        viewHolder.songname.setText(songbean.getSongName());
        viewHolder.singername.setText(songbean.getSingerName());
        MyImageLoader.with(context).into(viewHolder.cover)
                .placeholder(R.drawable.ic_default_bottom_music_icon)
                .load(songbean.getCoverUrl());
    }

    @Override
    public int getItemCount() {
        return songbeans.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View song;
        ImageView cover;
        TextView songname;
        TextView singername;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            song = itemView;
            cover = itemView.findViewById(R.id.cover);
            songname = itemView.findViewById(R.id.item_song_name);
            singername = itemView.findViewById(R.id.item_singer_name);
        }
    }
}
