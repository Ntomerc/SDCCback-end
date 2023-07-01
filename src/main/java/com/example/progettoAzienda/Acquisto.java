package com.example.progettoAzienda;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Acquisto {


    String nomeProdotto;
    String descrizione;
    Double prezzo;
    String categoria;
    String idUser;
    String email;
    Integer annoFiscaleDiRiferimento;
    String ricevuta;
    public Acquisto(){

    }
    public Acquisto (String nomeProdotto, String descrizione, double prezzo, String categoria, String idUser, String email, Integer annoFiscaleDiRiferimento, String ricevuta){
        this.categoria=categoria;
        this.prezzo=prezzo;
        this.descrizione=descrizione;
        this.nomeProdotto=nomeProdotto;
        this.idUser=idUser;
        this.email=email;
        this.annoFiscaleDiRiferimento=annoFiscaleDiRiferimento;
        this.ricevuta=ricevuta;
    }
    //documento Ricevuta



}
