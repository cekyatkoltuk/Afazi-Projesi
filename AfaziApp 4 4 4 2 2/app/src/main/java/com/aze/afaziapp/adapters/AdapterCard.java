package com.aze.afaziapp.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aze.afaziapp.FilterCard;
import com.aze.afaziapp.MemoryCardsDetailActivity;
import com.aze.afaziapp.databinding.RowCardBinding;
import com.aze.afaziapp.models.ModelCard;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.transition.Hold;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterCard extends RecyclerView.Adapter<AdapterCard.HolderCard> implements Filterable {

    private Context context;
    public ArrayList<ModelCard> cardArrayList, filterList;
    private RowCardBinding binding;

    private FilterCard filter;

    public AdapterCard(Context context, ArrayList<ModelCard> cardArrayList) {
        this.context = context;
        this.cardArrayList = cardArrayList;
        this.filterList = cardArrayList;

    }

    @NonNull
    @Override
    public HolderCard onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowCardBinding.inflate(LayoutInflater.from(context), parent, false);

        return new HolderCard(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderCard holder, int position) {
        ModelCard model = filterList.get(position);
        String uid = model.getUid();
        String title= model.getTitle();
        String downloadurl = model.getDownloadurl();
        String useremail = model.getUseremail();
        String cardId = model.getCardId();

        holder.nameTv.setText(title);
        Picasso.get().load(downloadurl).into(holder.cImg);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MemoryCardsDetailActivity.class);
                intent.putExtra("cardImage", downloadurl);
                intent.putExtra("cardTitle", title);
                context.startActivity(intent);
            }
        });

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Sil")
                        .setMessage("Silmek istediğinize emin misiniz?")
                        .setPositiveButton("Sil", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(context, "Siliniyor...", Toast.LENGTH_SHORT).show();
                                deleteCard(model, holder);
                            }
                        })
                        .setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();

            }
        });

    }

    private void deleteCard(ModelCard model, HolderCard holder) {

        String id = model.getCardId();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Cards");
        ref.child(id)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //Toast.makeText(context, "Silindi", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    @Override
    public int getItemCount() {
        return cardArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter==null)
        {
            filter= new FilterCard(filterList,this);
        }
        return filter;
    }

    class HolderCard extends RecyclerView.ViewHolder{

        TextView nameTv;
        ImageView cImg;
        ImageButton deleteBtn;


        public HolderCard(@NonNull View itemView) {
            super(itemView);

            nameTv = binding.nameTv;
            cImg = binding.cImg;
            deleteBtn = binding.deleteBtn;


        }
    }

    public interface OnItemClickListener {
        void onItemClick(ModelCard card);
    }
}
