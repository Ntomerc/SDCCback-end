package com.example.progettoAzienda;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PerCategoria {
    String categoria;
    int quantita;
    double prezzo;

    public PerCategoria(){

    }

    public PerCategoria(String categoria, int quantita, double prezzo){
        this.categoria=categoria;
        this.prezzo=prezzo;
        this.quantita=quantita;
    }
}
