package com.example.mypokedex.recycle;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mypokedex.Pokemon;
import com.example.mypokedex.PokemonActivity;
import com.example.mypokedex.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class adapterPokemon extends RecyclerView.Adapter<adapterPokemon.MyViewHolder> {

    private final ArrayList<Pokemon> data;

    public adapterPokemon(ArrayList<Pokemon> data){
        this.data = data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemList = LayoutInflater.from(parent.getContext()).inflate(R.layout.pokemonlistitem, parent, false);

        return new MyViewHolder(itemList);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Pokemon pokemon = data.get(position);

        holder.pokemonNameView.setText(pokemon.name);
        holder.pokemonTypeView.setText(pokemon.type);
        holder.pokemon = pokemon;

        AsyncTask<Void, Void, Void> addImageTask = new MyAsyncTask(pokemon, holder.pokemonImageView);
        addImageTask.execute();
    }

    @Override
    public int getItemCount() {
        if (data != null) {
            return data.size();
        } else{
            return 0;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        Pokemon pokemon;
        ImageView pokemonImageView;
        TextView pokemonNameView, pokemonTypeView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            pokemonNameView = itemView.findViewById(R.id.pokemonName);
            pokemonImageView = itemView.findViewById(R.id.pokemonImage);
            pokemonTypeView = itemView.findViewById(R.id.pokemonType);

            Context ctx = itemView.getContext();
            itemView.setOnClickListener(view -> toPokemonActivity(ctx, pokemon.name));
            itemView.setOnLongClickListener(view -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                builder.setTitle("Deseja deletar este Pokemon?");
                builder.setPositiveButton("Sim", (dialog, which) -> {
                    int position = data.indexOf(pokemon);
                    data.remove(position);
                    notifyItemRemoved(position);
                });
                builder.setNegativeButton("Não", (dialog, which) -> dialog.cancel());
                builder.show();
                return false;
            });
        }
    }

    private class MyAsyncTask extends AsyncTask<Void, Void, Void> {

        private Pokemon pokemon;
        private ImageView pokemonView;
        private Drawable d = null;

        public MyAsyncTask(Pokemon pokemon, ImageView pokemonView) {
            this.pokemon = pokemon;
            this.pokemonView = pokemonView;
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            System.out.println(pokemon.imageURL);
            try {
                InputStream is = (InputStream) new URL(pokemon.imageURL).getContent();
                d = Drawable.createFromStream(is, "src name");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pokemonView.setImageDrawable(d);
        }
    }

    public void toPokemonActivity(Context ctx, String areaNome) {
        Intent intent = new Intent(ctx, PokemonActivity.class);
        intent.putExtra("pokemonNick", areaNome);
        ctx.startActivity(intent);
    }
}
