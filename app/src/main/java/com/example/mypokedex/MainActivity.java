package com.example.mypokedex;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.AsyncTaskLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.AsyncNotedAppOp;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.mypokedex.recycle.adapterPokemon;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    adapterPokemon adapter;
    private ArrayList<Pokemon> data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();

        PokemonKeeper pokemonKeeper = (PokemonKeeper) this.getApplicationContext();
        data = pokemonKeeper.pokemon_data;

        ImageView addButton = findViewById(R.id.addPokemonBtn);

        adapter = new adapterPokemon(data);

        recyclerView = findViewById(R.id.pokemonRecyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        Context ctx = this;

        addButton.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setTitle("Adicionar Pokemon...");
            EditText textView = new EditText(ctx);
            String defaultName = "Pokemon " + (data.size()+1);
            textView.setText("Pikachu");
            builder.setView(textView);
            builder.setPositiveButton("Próximo", (dialog, which) -> {
                String pokeName = textView.getText().toString().toLowerCase();
                try {
                    AlertDialog.Builder nameBuilder = new AlertDialog.Builder(ctx);
                    EditText nickTextView = new EditText(ctx);
                    nickTextView.setText(defaultName);
                    nameBuilder.setView(nickTextView);
                    nameBuilder.setTitle("Escolha o apelido do Pokemon");
                    nameBuilder.setPositiveButton("Adicionar", (dialog2, which2) -> {
                        int exists = 0;
                        for (Pokemon p : data) {
                            System.out.println(nickTextView.getText().toString());
                            System.out.println(p.name);
                            if (nickTextView.getText().toString().equals(p.name)) {
                                exists = 1;
                            }
                        }
                        if (exists == 0) {
                            AsyncTask<Void, Void, Void> addTask = new MyAsyncTask("https://pokeapi.co/api/v2/pokemon/" + pokeName, nickTextView.getText().toString(), this);
                            addTask.execute();
                        } else {
                            AlertDialog.Builder builder2 = new AlertDialog.Builder(ctx);
                            builder2.setTitle("Pokemon inserido com o apelido \"" + nickTextView.getText().toString() + "\" já existe!!!");
                            builder2.setPositiveButton("Ok", (dialog3, which3) -> {
                                dialog3.cancel();
                                dialog.cancel();
                            });
                            builder2.show();
                        }
                    });
                    nameBuilder.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            builder.setNegativeButton("Cancelar", (dialog, which) -> {
                dialog.cancel();
            });
            builder.show();
        });

    }

    private class MyAsyncTask extends AsyncTask<Void, Void, Void> {

        private String urlData, name;
        private Context ctx;
        private int success = 1;

        public MyAsyncTask(String urlData, String name, Context ctx) {
            this.urlData = urlData;
            this.name = name;
            this.ctx = ctx;
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            URL url = null;
            try {
                url = new URL(urlData);
                InputStream inputStream = url.openStream();
                System.out.println(urlData);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String bufferRdr;
                StringBuilder responseStrBuilder = new StringBuilder();
                while ((bufferRdr = bufferedReader.readLine()) != null) {
                    responseStrBuilder.append(bufferRdr);
                }
                JSONObject pokeJson = new JSONObject(responseStrBuilder.toString());
                int pokeId = pokeJson.getInt("id");
                String pokeType = pokeJson.getJSONArray("types").getJSONObject(0).getJSONObject("type").getString("name");
                String pokeName = pokeJson.getString("name");
                String imagePath = pokeJson.getJSONObject("sprites").getString("front_default");
                Pokemon toAddPokemon = new Pokemon(name, pokeId, pokeType, pokeName, imagePath);
                data.add(toAddPokemon);

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                success = 0;
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (success == 0) {
                AlertDialog.Builder builder2 = new AlertDialog.Builder(this.ctx);
                builder2.setTitle("Pokemon inserido não reconhecido!!!");
                builder2.setPositiveButton("Ok", (dialog2, which2) -> dialog2.cancel());
                builder2.show();
            } else {
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        }
        return super.onKeyDown(keyCode, event);
    }

}