package com.aze.afaziapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aze.afaziapp.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        binding = FragmentHomeBinding.bind(view);

        binding.memory.setOnClickListener(v -> startActivity(new Intent(getActivity(), MemoryCardsActivity.class)));
        binding.readLl.setOnClickListener(v -> openCategoryActivity(false));
        binding.writeLl.setOnClickListener(v -> openCategoryActivity(true));
        binding.listenComprehension.setOnClickListener(v -> startActivity(new Intent(getActivity(), ListenComprehensionActivity.class)));
        binding.notes.setOnClickListener(v -> startActivity(new Intent(getActivity(), NotesActivity.class)));
        binding.imgLl.setOnClickListener(v -> startActivity(new Intent(getActivity(), VisualComprehensionActivity.class)));

        binding.ai.setOnClickListener(v -> startActivity(new Intent(getActivity(), AiActivity.class)));
        return view;
    }

    private void openCategoryActivity(boolean isWritingMode) {
        Intent intent = new Intent(getActivity(), CategoryActivity.class);
        intent.putExtra("isWritingMode", isWritingMode);
        startActivity(intent);
     }
}