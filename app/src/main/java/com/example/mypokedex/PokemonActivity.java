package com.example.mypokedex;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

public class PokemonActivity extends AppCompatActivity {

    private ArrayList<Pokemon> data;
    private Pokemon mainPokemon;
    private String pokemonNick;
    private TextView pokemonNickView, pokemonIdView, pokemonTypeView, pokemonSpeciesView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon);
        Objects.requireNonNull(getSupportActionBar()).hide();

        PokemonKeeper pokemonKeeper = (PokemonKeeper) this.getApplicationContext();
        data = pokemonKeeper.pokemon_data;

        pokemonNick = getIntent().getStringExtra("pokemonNick");

        for (Pokemon p : data) {
            if (p.name.equals(pokemonNick)) {
                mainPokemon = p;
            }
        }

        pokemonNickView = (TextView) findViewById(R.id.pokemonSName);
        pokemonTypeView = (TextView) findViewById(R.id.pokemonTypeView);
        pokemonIdView = (TextView) findViewById(R.id.pokemonIdView);
        pokemonSpeciesView = (TextView) findViewById(R.id.pokemonSpecies);
        pokemonNickView.setText(pokemonNick);
        pokemonTypeView.setText("Tipo: " + mainPokemon.type);
        pokemonSpeciesView.setText("Espécie: " + mainPokemon.pokemonName);
        pokemonIdView.setText("#" + mainPokemon.pokedexNumber);

        Context ctx = this;
        ImageView imageView = findViewById(R.id.pokemonImageSView);
        AsyncTask<Void, Void, Void> addImageTask = new MyAsyncTask(mainPokemon, imageView);
        addImageTask.execute();

        pokemonNickView.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setTitle("Mudar nome.");
            EditText mNameView = new EditText(ctx);
            mNameView.setText(pokemonNick);
            builder.setView(mNameView);
            builder.setPositiveButton("Ok", (dialog, which) -> {
                int exists = 0;
                for (Pokemon p : data) {
                    if (mNameView.getText().toString().equals(p.name)) {
                        exists = 1;
                    }
                }
                if (mNameView.getText().toString().equals(mainPokemon.name)) {
                    dialog.cancel();
                }
                else if (exists == 1) {
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(ctx);
                    builder2.setTitle("Pokemon com o apelido \"" + mNameView.getText().toString() + "\" já existe!!!");
                    builder2.setPositiveButton("Ok", (dialog3, which3) -> {
                        dialog3.cancel();
                        dialog.cancel();
                    });
                    builder2.show();
                } else {
                    mainPokemon.name = mNameView.getText().toString();
                    pokemonNickView.setText(mainPokemon.name);
                }
            });
            builder.show();
        });
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent startMain = new Intent(this, MainActivity.class);
            this.startActivity(startMain);
        }
        return super.onKeyDown(keyCode, event);
    }

}