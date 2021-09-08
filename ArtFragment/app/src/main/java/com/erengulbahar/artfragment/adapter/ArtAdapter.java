package com.erengulbahar.artfragment.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.erengulbahar.artfragment.databinding.RecyclerRowBinding;
import com.erengulbahar.artfragment.model.Art;
import com.erengulbahar.artfragment.view.FirstFragmentDirections;
import com.erengulbahar.artfragment.view.SecondFragment;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ArtAdapter extends RecyclerView.Adapter<ArtAdapter.ArtHolder>
{
    ArrayList<Art> artArrayList;

    public ArtAdapter(ArrayList<Art> artArrayList)
    {
        this.artArrayList = artArrayList;
    }

    @NonNull
    @Override
    public ArtHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        RecyclerRowBinding recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);

        return new ArtHolder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtHolder holder, int position)
    {
        holder.binding.recyclerViewTextView.setText(artArrayList.get(position).artistName);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                FirstFragmentDirections.ActionFirst action = FirstFragmentDirections.actionFirst("old");
                action.setArtId(artArrayList.get(position).id);
                action.setInfo("old");
                Navigation.findNavController(v).navigate(action);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return artArrayList.size();
    }

    public class ArtHolder extends RecyclerView.ViewHolder
    {
        private RecyclerRowBinding binding;

        public ArtHolder(RecyclerRowBinding binding)
        {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}