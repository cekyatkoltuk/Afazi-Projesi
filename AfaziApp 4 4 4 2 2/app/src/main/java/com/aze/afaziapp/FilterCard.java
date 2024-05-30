package com.aze.afaziapp;

import android.widget.Filter;

import com.aze.afaziapp.adapters.AdapterCard;
import com.aze.afaziapp.models.ModelCard;

import java.util.ArrayList;

public class FilterCard extends Filter {

    private ArrayList<ModelCard> filterList;
    private AdapterCard adapter;

    public FilterCard(ArrayList<ModelCard> filterList, AdapterCard adapter) {
        this.filterList = filterList;
        this.adapter = adapter;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        if (constraint != null && constraint.length() > 0) {
            String filterPattern = constraint.toString().toLowerCase().trim();
            ArrayList<ModelCard> filteredModels = new ArrayList<>();
            for (ModelCard model : filterList) {
                if (model.getTitle().toLowerCase().contains(filterPattern)) {
                    filteredModels.add(model);
                }
            }
            results.count = filteredModels.size();
            results.values = filteredModels;
        } else {
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.cardArrayList = (ArrayList<ModelCard>) results.values;
        adapter.notifyDataSetChanged();
    }
}

