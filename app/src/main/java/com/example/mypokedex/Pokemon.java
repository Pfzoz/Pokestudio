package com.example.mypokedex;

public class Pokemon {

    public String name, type, pokemonName, imageURL;
    public int pokedexNumber;

    public Pokemon(String name, int pokedexNumber, String type, String pokemonName, String imageURL) {
        this.name = name;
        this.type = type;
        this.pokedexNumber = pokedexNumber;
        this.pokemonName = pokemonName;
        this.imageURL = imageURL;
    }

}
