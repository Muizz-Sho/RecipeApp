package com.official.recipeapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.official.recipeapp.R;
import com.official.recipeapp.model.Recipe;
import com.official.recipeapp.screens.RecipeDetails;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeAdapterViewHolder> implements Filterable {

    Context context;
    ArrayList<Recipe> recipeList;
    ArrayList<Recipe> recipeListFiltered;

    public RecipeAdapter(Context context, ArrayList<Recipe> recipeList) {
        this.context = context;
        this.recipeList = recipeList;
        this.recipeListFiltered = new ArrayList<>(recipeList);
    }

    @NonNull
    @Override
    public RecipeAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_layout,parent,false);
        RecipeAdapterViewHolder viewHolder = new RecipeAdapterViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecipeAdapterViewHolder holder, final int position) {

        final Recipe recipeCaller = recipeList.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent redirect = new Intent(context, RecipeDetails.class);
                redirect.putExtra("recipeID",recipeCaller.getRecipeID());
                context.startActivity(redirect);
            }
        });

        holder.name.setText(recipeCaller.getRecipeName());
        holder.type.setText(recipeCaller.getRecipeType());
        holder.ingredient.setText(recipeCaller.getRecipeIngredient());
        holder.steps.setText(recipeCaller.getRecipeSteps());
        if (recipeCaller.getRecipeImageURL().equals("default")){
            holder.image.setImageResource(R.drawable.ic_launcher_background);
        }else {
            Glide.with(context).load(recipeCaller.getRecipeImageURL()).into(holder.image);
        }
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //FilterAdapter to change data
    @Override
    public Filter getFilter() {
        return recipeFilter;
    }

    private Filter recipeFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Recipe> filteredResultList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0){
                filteredResultList.addAll(recipeListFiltered);
            }else {
                String filterInput = constraint.toString().toLowerCase().trim();
                for (Recipe item : recipeListFiltered){
                    if (item.getRecipeType().toLowerCase().contains(filterInput)){
                        filteredResultList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredResultList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            recipeList.clear();
            recipeList.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };

    class RecipeAdapterViewHolder extends RecyclerView.ViewHolder{

        TextView name,type,ingredient,steps;
        ImageView image;

         RecipeAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.list_recipeName);
            type = itemView.findViewById(R.id.list_recipeType);
            image = itemView.findViewById(R.id.list_recipeImage);
            ingredient = itemView.findViewById(R.id.list_recipeIngredient);
            steps = itemView.findViewById(R.id.list_recipeSteps);

        }
    }
}
