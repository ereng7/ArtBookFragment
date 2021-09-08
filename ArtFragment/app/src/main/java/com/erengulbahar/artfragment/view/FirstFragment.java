package com.erengulbahar.artfragment.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.erengulbahar.artfragment.R;
import com.erengulbahar.artfragment.adapter.ArtAdapter;
import com.erengulbahar.artfragment.databinding.FragmentFirstBinding;
import com.erengulbahar.artfragment.model.Art;
import com.erengulbahar.artfragment.roomdb.ArtDao;
import com.erengulbahar.artfragment.roomdb.ArtDatabase;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class FirstFragment extends Fragment
{
    private FragmentFirstBinding binding;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    ArtDao artDao;
    ArtDatabase artDatabase;
    ArtAdapter artAdapter;
    private final CompositeDisposable mDisposable = new CompositeDisposable();

    public FirstFragment()
    {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        artDatabase = Room.databaseBuilder(requireContext(),ArtDatabase.class,"Arts").build();
        artDao = artDatabase.artDao();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        binding =FragmentFirstBinding.inflate(inflater,container,false);
        View view =binding.getRoot();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());
        binding.recyclerView.setLayoutManager(layoutManager);
        getData();
    }

    public void getData()
    {
        mDisposable.add(artDao.getAll().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(FirstFragment.this::handleResponse));
    }

    public void handleResponse(List<Art> artList)
    {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        artAdapter = new ArtAdapter((ArrayList<Art>) artList);
        binding.recyclerView.setAdapter(artAdapter);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        binding = null;
        mDisposable.clear();
    }
}