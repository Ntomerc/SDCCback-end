package com.example.progettoAzienda.repositories;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import com.azure.spring.data.cosmos.repository.Query;
import com.example.progettoAzienda.entities.User;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends CosmosRepository<User, String> {


    @Override
    Optional<User> findById(String s);

    //@Query("select c from c where c.email = @email")
    User findByEmail(@Param("email") String email);


    @Override
    Iterable<User> findAll();

    @Query("select value count(1) from c where c.email = @email")
    int getNumberOfUserWithEmail (@Param("email") String email);
}
