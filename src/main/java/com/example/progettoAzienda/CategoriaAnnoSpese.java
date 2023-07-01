package com.example.progettoAzienda;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CategoriaAnnoSpese implements Comparable<CategoriaAnnoSpese> {
    String categoria;
    Integer anno;
    double prezzo;
    Integer quantity;
public CategoriaAnnoSpese(){

}

    public CategoriaAnnoSpese(String categoria, int anno, double spese, int quantity) {
        this.categoria = categoria;
        this.anno = anno;
        this.prezzo = spese;
        this.quantity=quantity;
    }


        @Override
        public int compareTo(CategoriaAnnoSpese o) {
            // Ordina per anno crescente
            int compareByYear = Integer.compare(this.anno, o.anno);
            if (compareByYear != 0) {
                return compareByYear;
            }

            // Se gli anni sono uguali, ordina per spese crescenti
            return Double.compare(this.prezzo, o.prezzo);
        }
    }
