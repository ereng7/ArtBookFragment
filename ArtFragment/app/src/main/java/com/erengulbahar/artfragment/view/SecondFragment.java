package com.erengulbahar.artfragment.view;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.erengulbahar.artfragment.R;
import com.erengulbahar.artfragment.databinding.FragmentSecondBinding;
import com.erengulbahar.artfragment.model.Art;
import com.erengulbahar.artfragment.roomdb.ArtDao;
import com.erengulbahar.artfragment.roomdb.ArtDatabase;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SecondFragment extends Fragment
{
    private FragmentSecondBinding binding;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    ArtDatabase db;
    ArtDao artDao;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    Bitmap selectedImage;
    SQLiteDatabase database;
    Art artFromMain;
    String info = "";

    public SecondFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        registerLauncher();
        db = Room.databaseBuilder(requireContext(),ArtDatabase.class,"Arts").build();
        artDao = db.artDao();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        binding = FragmentSecondBinding.inflate(inflater,container,false);
        View view = binding.getRoot();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = requireActivity().openOrCreateDatabase("Arts", Context.MODE_PRIVATE, null);

        if(getArguments() != null)
        {
            info = SecondFragmentArgs.fromBundle(getArguments()).getInfo();
        }

        else
        {
            info = "new";
        }

        binding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(v);
            }
        });

        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData(v);
            }
        });

        binding.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteData(v);
            }
        });


        if (info.equals("new"))
        {
            binding.saveButton.setVisibility(View.VISIBLE);
            binding.deleteButton.setVisibility(View.GONE);

            binding.artName.setText("");
            binding.artistName.setText("");
            binding.yearName.setText("");
            binding.imageView.setImageResource(R.drawable.selected);
        }

        else
        {
            int artId = SecondFragmentArgs.fromBundle(getArguments()).getArtId();

            binding.saveButton.setVisibility(View.GONE);
            binding.deleteButton.setVisibility(View.VISIBLE);
            compositeDisposable.add(artDao.getArtById(artId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(SecondFragment.this::handleResponseWithOldArt));
        }

    }


    public void handleResponseWithOldArt(Art art)
    {
        artFromMain = art;
        binding.artName.setText(art.artname);
        binding.artistName.setText(art.artistName);
        binding.yearName.setText(art.year);

        Bitmap bitmap = BitmapFactory.decodeByteArray(art.image,0,art.image.length);
        binding.imageView.setImageBitmap(bitmap);
    }

    public void selectImage(View view)
    {
        binding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.READ_EXTERNAL_STORAGE))
                    {
                        Snackbar.make(view,"Permission needed for gallery!",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                                //request permission
                                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                            }
                        }).show();
                    }

                    else
                    {
                        //request permission
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }

                else
                {
                    //gallery
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);
                }
            }
        });
    }

    public void saveData(View view)
    {
        String painterName = binding.artistName.getText().toString();
        String artName = binding.artName.getText().toString();
        String year = binding.yearName.getText().toString();

        Bitmap smallImage = makeSmallerImage(selectedImage,300);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        smallImage.compress(Bitmap.CompressFormat.JPEG,50,outputStream);
        byte[] byteArray = outputStream.toByteArray();

        Art art = new Art(painterName,artName,year,byteArray);

        compositeDisposable.add(artDao.insert(art).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(SecondFragment.this::handleResponse));
    }

    public void handleResponse()
    {
        NavDirections action = SecondFragmentDirections.actionSecond();
        Navigation.findNavController(requireView()).navigate(action);
    }

    public void deleteData(View view)
    {
        compositeDisposable.add(artDao.delete(artFromMain).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(SecondFragment.this::handleResponse));
    }

    public Bitmap makeSmallerImage(Bitmap image, int maximumSize)
    {

        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;

        if (bitmapRatio > 1) {
            width = maximumSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maximumSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image,width,height,true);
    }

    private void registerLauncher()
    {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result)
            {
                if(result.getResultCode() == RESULT_OK)
                {
                    Intent intentFromResult = result.getData();

                    if(intentFromResult != null)
                    {
                        Uri imageData = intentFromResult.getData();

                        try
                        {
                            if(Build.VERSION.SDK_INT >= 28)
                            {
                                ImageDecoder.Source source = ImageDecoder.createSource(requireActivity().getContentResolver(),imageData);
                                selectedImage = ImageDecoder.decodeBitmap(source);
                                binding.imageView.setImageBitmap(selectedImage);
                            }

                            else
                            {
                                selectedImage = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(),imageData);
                                binding.imageView.setImageBitmap(selectedImage);
                            }
                        }

                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        permissionLauncher =registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result)
            {
                if(result)
                {
                    //permission granted
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);
                }

                else
                {
                    //permission denied
                    Toast.makeText(requireActivity(),"Permission needed!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        binding = null;
        compositeDisposable.clear();
    }
}