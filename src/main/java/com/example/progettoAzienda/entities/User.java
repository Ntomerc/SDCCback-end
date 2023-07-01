package com.example.progettoAzienda.entities;

import com.azure.spring.data.cosmos.core.mapping.Container;
import com.azure.spring.data.cosmos.core.mapping.PartitionKey;
import com.example.progettoAzienda.Acquisto;
import com.example.progettoAzienda.CategoriaAnnoSpese;
import com.example.progettoAzienda.PerCategoria;
import com.example.progettoAzienda.Stringa;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import java.util.List;
import java.util.UUID;

@Container(containerName = "Utenti")
    @Getter
    @Setter
    @ToString


    public class User {
    public User() {

    }

        @Id
        private String id= UUID.randomUUID().toString();

        private String firstName;

        private String lastName;

        private String address;
    @PartitionKey
        private String email;

        private String numeroTel;

        private String compleanno;

        private List<Acquisto> acquisti;

        private List<PerCategoria> xcategoria;

        private List<CategoriaAnnoSpese> speseAnno;
        private List<Stringa> year;

        /*public User(String firstName, String lastName, String address, String email, List<Acquisto> acquisti) {

            this.firstName = firstName;
            this.lastName = lastName;

            this.address = address;
            this.email = email;
            this.acquisti= new ArrayList<>(acquisti) ;
        }*/
    }
