package com.example.progettoAzienda.Service;


import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.models.CosmosPatchOperations;
import com.azure.cosmos.models.PartitionKey;
import com.example.progettoAzienda.Acquisto;
import com.example.progettoAzienda.CategoriaAnnoSpese;
import com.example.progettoAzienda.PerCategoria;
import com.example.progettoAzienda.Stringa;
import com.example.progettoAzienda.Support.exceptions.FillInAllTheFieldsException;
import com.example.progettoAzienda.entities.User;
import com.example.progettoAzienda.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;


    @Value("${spring.cloud.azure.cosmos.uri}")
    private String uri;

    @Value("${spring.cloud.azure.cosmos.key}")
    private String key;
    @Value("${spring.cloud.azure.cosmos.database}")
    private String dbName;
    private static final String containerName = "Utenti";

    public List<Acquisto> findAcquisti (String email) {
        User user = userRepository.findByEmail(email);
        return user.getAcquisti();};

    public  List<PerCategoria> findCategorie(String email){
        User user = userRepository.findByEmail(email);
        return user.getXcategoria();};

    public  List<CategoriaAnnoSpese> findCategorieAnno(String email){
        User user = userRepository.findByEmail(email);
        return user.getSpeseAnno();};

    public List<Stringa> findAnni(String email){
        User user = userRepository.findByEmail(email);
        return user.getYear();};


   /* public void addPurchase(Acquisto acquisto){
        CosmosClient client = new CosmosClientBuilder()
                .endpoint("https://db-azienda.documents.azure.com:443/")
                .key("Q2RQT6KmKS3nNA88KuTIjkRH1wB0B7BjSw6Z1P7lBsx6GZa23z3jDYYRY8y56H1LTWsgMs6NcxWeACDbDUlyoA==")
                .buildClient();

        CosmosDatabase database = client.getDatabase("Utenti");
        CosmosContainer container = database.getContainer("Utenti");

        // Recupera il documento utente dal database
        String userId = "acquisto.getIdUser()";
        CosmosItemResponse<User> response = container.readItem(userId, new PartitionKey(userId), User.class);
        User user= userRepository.findByEmail(acquisto.getEmail());
        List<Acquisto> acquisti= user.getAcquisti();
        acquisti.add(acquisto);
        System.out.println("AAAAAAAAAAAAAAAAAAA"+acquisti);
        container.replaceItem(user, response.getItem().getId(), new PartitionKey(response.getItem().getId()), null);


    }*/
   boolean trovato=false;
   boolean found= false;
    @Transactional(readOnly = false)
    public void addPurchase(Acquisto acquisto) throws FillInAllTheFieldsException {
        CosmosClientBuilder clientBuilder = new CosmosClientBuilder().endpoint(uri).key(key);
        CosmosClient cosmosClient = clientBuilder.buildClient();
        CosmosContainer container = ((CosmosClient) cosmosClient).getDatabase(dbName).getContainer(containerName);
        User user = userRepository.findByEmail(acquisto.getEmail());
        List<Acquisto> acquisti= user.getAcquisti();
        acquisti.add(acquisto);
        container.patchItem( acquisto.getIdUser(), new PartitionKey(acquisto.getEmail()),
                CosmosPatchOperations.create().replace("/acquisti", acquisti),
                User.class
        );
        if (acquisto.getNomeProdotto().isEmpty()|| acquisto.getCategoria().isEmpty() || acquisto.getDescrizione().isEmpty() || acquisto.getPrezzo() == 0)
            throw new FillInAllTheFieldsException();

        List<PerCategoria> categorie = user.getXcategoria();

        trovato=false;
        if(categorie.isEmpty()){
            categorie.add(new PerCategoria(acquisto.getCategoria(), 1, acquisto.getPrezzo()));
            trovato=true;
        }
        for(int i=0; i<categorie.size();i++){
            System.out.println("CerrrrrrrrrrrrrCCCCCCCCCCCCC");
            String c= categorie.get(i).getCategoria();
            if(c.equals(acquisto.getCategoria()) && trovato ==false){
                categorie.get(i).setQuantita(categorie.get(i).getQuantita()+1);
                categorie.get(i).setPrezzo(acquisto.getPrezzo()+categorie.get(i).getPrezzo());
                trovato=true;
            }
        }
        if(trovato==false) {
            System.out.println("CICCCCCCCCCCCCCRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR");
            categorie.add(new PerCategoria(acquisto.getCategoria(), 1, acquisto.getPrezzo()));
            trovato=true;
        }
        container.patchItem( acquisto.getIdUser(), new PartitionKey(acquisto.getEmail()),
                CosmosPatchOperations.create().replace("/xcategoria", categorie),
                User.class
        );
        List<CategoriaAnnoSpese> categorieAnno = user.getSpeseAnno();

        found=false;
        if(categorieAnno.isEmpty()){

            categorieAnno.add(new CategoriaAnnoSpese(acquisto.getCategoria(), acquisto.getAnnoFiscaleDiRiferimento(), acquisto.getPrezzo(),1));
            found=true;
        }
        for(int i=0; i<categorieAnno.size();i++){
            System.out.println("CerrrrrrrrrrrrrCCCCCCCCCCCCC");
            String c= categorieAnno.get(i).getCategoria();
            int anno = categorieAnno.get(i).getAnno();
            if(found ==false && c.equals(acquisto.getCategoria()) && anno == acquisto.getAnnoFiscaleDiRiferimento()  ){
                categorieAnno.get(i).setPrezzo(acquisto.getPrezzo()+categorieAnno.get(i).getPrezzo());
                categorieAnno.get(i).setQuantity(categorieAnno.get(i).getQuantity()+1);
                found=true;
            }
        }
        for(int i=0; i<categorieAnno.size();i++) {
            String c = categorieAnno.get(i).getCategoria();
            int anno = categorieAnno.get(i).getAnno();
            if (found == false && c.equals(acquisto.getCategoria()) && anno != acquisto.getAnnoFiscaleDiRiferimento()) {
                categorieAnno.add(new CategoriaAnnoSpese(acquisto.getCategoria(), acquisto.getAnnoFiscaleDiRiferimento(), acquisto.getPrezzo(), 1));
                found = true;
            }
        }

        if(found==false) {
            System.out.println("CICCCCCCCCCCCCCRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR");
            categorieAnno.add(new CategoriaAnnoSpese(acquisto.getCategoria(), acquisto.getAnnoFiscaleDiRiferimento(), acquisto.getPrezzo(),1));
            found=true;
        }

        Collections.sort(categorieAnno);

        container.patchItem( acquisto.getIdUser(), new PartitionKey(acquisto.getEmail()),
                CosmosPatchOperations.create().replace("/speseAnno", categorieAnno),
                User.class
        );

        boolean annoCe=false;
        List<Stringa> years= user.getYear();
        int i=acquisto.getAnnoFiscaleDiRiferimento();
        String si= String.valueOf(i);
        Stringa s= new Stringa(si);
        System.out.println("VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV"+si);
        if(years==null|| years.isEmpty())
            years.add(s);
        for(int j =0; j<years.size(); j++){
            if(years.get(j).getAnno().equals(si))
                annoCe=true;
        }
        if(annoCe==false)
            years.add(s);

        container.patchItem( acquisto.getIdUser(), new PartitionKey(acquisto.getEmail()),
                CosmosPatchOperations.create().replace("/year", years),
                User.class
        );
    }




    @Transactional(readOnly = true)
    public User showUser(String email){
        return userRepository.findByEmail(email);
    }

    /*@Transactional(readOnly = true)
    */public Optional<User> showUserID(String id){
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Iterable<User> trova(){
        System.out.println("ciao");
        Iterable<User> utenti = userRepository.findAll();
        for (User u : utenti)
            System.out.println(u);
        System.out.println(utenti);
        return  utenti;
    }
    @Transactional(readOnly = true)
    public User registerUser(User user) {
/*
        if ( userRepository.getNumberOfUserWithEmail(user.getEmail())==1 ) {
            throw new MailUserAlreadyExistsException();
        }
        if (user.getEmail().isEmpty()|| user.getEmail().isEmpty()|| user.getAddress().isEmpty()||user.getFirstName().isEmpty()||user.getLastName().isEmpty())
            throw new FillInAllTheFieldsException();
*/

        return userRepository.save(user);
    }

    public boolean esisteUtente(String email){
        if (userRepository.getNumberOfUserWithEmail(email) == 1)
            return true;
        return false;
    }

    public String findId(String email){
        return userRepository.findByEmail(email).getId();
    }

/*    @Transactional(readOnly = true)
    public User registerUser(User user){
        return userRepository.save(user);
    }
    */

}