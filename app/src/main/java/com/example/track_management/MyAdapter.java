package com.example.track_management;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.LinkedList;
import model.Tache;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private LinkedList<Tache> taches;
    private Context context;
    private OnItemClickListener mListener;

    // Interface pour écouter les clics sur les éléments de la liste
    public interface OnItemClickListener {
        void onItemClick(Tache tache);
    }

    // Constructeur prenant les données de la liste, le contexte et l'interface OnItemClickListener
    public MyAdapter(LinkedList<Tache> taches, Context context, OnItemClickListener listener) {
        this.taches = taches;
        this.context = context;
        mListener = listener;
    }

    // Méthode pour créer de nouvelles vues (invocable par le layout manager)
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Créer une nouvelle vue
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item_layout, parent, false);
        return new MyViewHolder(itemLayoutView);
    }

    // Méthode pour remplacer le contenu d'une vue (invocable par le layout manager)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Obtenir l'élément de la liste à cette position
        Tache currentTache = taches.get(position);
        // Remplacer le contenu de la vue avec cet élément
        holder.title.setText(currentTache.getTitle());
        // Référence à un fichier image dans le stockage Cloud
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(currentTache.getImg());
        // Télécharger directement depuis StorageReference en utilisant Glide
        Glide.with(context)
                .load(storageReference)
                .into(holder.img);
    }

    // Méthode pour obtenir le nombre d'éléments dans la liste
    @Override
    public int getItemCount() {
        return taches.size();
    }

    // Classe interne pour fournir une référence aux vues pour chaque élément de données
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title;
        public ImageView img;

        // Constructeur prenant une vue d'élément de liste
        public MyViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            title = itemLayoutView.findViewById(R.id.title);
            img = itemLayoutView.findViewById(R.id.img);
            itemLayoutView.setOnClickListener(this);
        }

        // Méthode pour gérer les clics sur les éléments de la liste
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                mListener.onItemClick(taches.get(position));
            }
        }
    }
}
