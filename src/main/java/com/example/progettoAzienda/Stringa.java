package com.example.progettoAzienda;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Stringa {
    String anno;

    public Stringa(){

    }

    public Stringa(String anno){
        this.anno=anno;
    }
}
