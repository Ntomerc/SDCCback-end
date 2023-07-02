package com.example.progettoAzienda.controllers.rest;


import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobItem;
import com.example.progettoAzienda.Acquisto;
import com.example.progettoAzienda.CategoriaAnnoSpese;
import com.example.progettoAzienda.PerCategoria;
import com.example.progettoAzienda.Service.UserService;
import com.example.progettoAzienda.Stringa;
import com.example.progettoAzienda.Support.ResponseMessage;
import com.example.progettoAzienda.Support.exceptions.FillInAllTheFieldsException;
import com.example.progettoAzienda.entities.User;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

// ...
@RestController
@RequestMapping("/users")

public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private BlobServiceClient blobServiceClient;
/*
    @ResponseStatus(code = HttpStatus.OK)
    @PostMapping("/save")
    public ResponseEntity create (@RequestBody @Valid User user){
       try{
           User added = userService.registerUser(user);
           return new ResponseEntity(added, HttpStatus.OK);
       } catch (FillInAllTheFieldsException e) {
           return new ResponseEntity<>(new ResponseMessage("FILL IN ALL THE FIELDS"), HttpStatus.BAD_REQUEST);
       } catch (MailUserAlreadyExistsException e) {
           return new ResponseEntity<>(new ResponseMessage("ERROR_MAIL_USER_ALREADY_EXISTS"), HttpStatus.BAD_REQUEST);
       }

    }
*/

    @GetMapping("/register")
    public String addUser(@RequestParam(value = "id_token") String token, HttpServletResponse response) throws ParseException, ParseException {
        //Estrarre informazioni dal token ricevuto come query param
        JWTClaimsSet claims = JWTParser.parse(token).getJWTClaimsSet();
        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"+claims);
        String firstName= claims.getStringClaim("given_name");
        String email = claims.getStringListClaim("emails").get(0);
        //Reindirizzare la richiesta alla pagina di Flutter. Fin quando non fai il deploy cambiare la porta

        if (userService.esisteUtente(email)==false){
            String compleanno=claims.getStringClaim("extension_Compleanno");
            String numeroTelefono=claims.getStringClaim("extension_Numeroditelefono");
            String lastName = claims.getStringClaim("family_name");
            String address = claims.getStringClaim("streetAddress");

            String id= UUID.randomUUID().toString();
            List<Acquisto> acquisti = new ArrayList<>();
            List<PerCategoria> categorie= new ArrayList<>();
            List<Stringa> anni= new ArrayList<>();
            List<CategoriaAnnoSpese> categorieAnno = new ArrayList<>();
            User user= new User();
            user.setAcquisti(acquisti);
            user.setEmail(email);
            user.setLastName(lastName);
            user.setFirstName(firstName);
            user.setAddress(address);
            user.setId(id);
            user.setCompleanno(compleanno);
            user.setNumeroTel(numeroTelefono);
            user.setXcategoria(categorie);
            user.setSpeseAnno(categorieAnno);
            user.setYear(anni);
            System.out.println("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE"+user);
            User added = userService.registerUser(user);
            String welcomePageFlutter = "https://progettoaziendamercuri.b2clogin.com/progettoaziendamercuri.onmicrosoft.com/oauth2/v2.0/authorize?p=B2C_1_utenti-progetto&client_id=d6042e84-3cc7-4cab-b72b-724b51b1114b&nonce=defaultNonce&redirect_uri=https%3A%2F%2Faziendaapp.azurewebsites.net%2Fusers%2Fregister&scope=openid&response_type=id_token&response_mode=query&prompt=login";
            response.setStatus(HttpServletResponse.SC_FOUND);
            response.setHeader("Location", welcomePageFlutter);
            response.setContentLength(0);

        }
        else{
        String welcomePageFlutter = "https://salmon-ground-03957ce10.3.azurestaticapps.net/#/welcome?name="+firstName+"&"+"email="+email;
        response.setStatus(HttpServletResponse.SC_FOUND);
        response.setHeader("Location", welcomePageFlutter);
        response.setContentLength(0);
        }
        return "Helloo!";


    }


    @PostMapping("/upload")
    public String uploadFile(@RequestBody byte[] fileData, @RequestParam("fileName") String fileName, @RequestParam("email") String email) {
        try {

            // Recupera il contenitore BLOB
            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient("azienda");

            // Crea un nome univoco per il BLOB utilizzando il nome originale del file
            //String blobName = java.util.UUID.randomUUID() + "-" + file.getOriginalFilename();

            // Carica il file nel BLOB
            BlobClient blobClient = containerClient.getBlobClient(email + "/"+fileName);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(fileData);
            blobClient.upload(inputStream, fileData.length);

            return "File caricato con successo: ";
        } catch (Exception e) {
            // Gestisci l'errore
            return "Errore durante il caricamento del file: " + e.getMessage();
        }
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadFolder(@RequestParam("folderName") String folderName) {
        try {
            System.out.println("SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS");
            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient("azienda");

            String zipFilePath = "C:/Users/PC-1-/Desktop/files.zip"; // Percorso in cui memorizzare il file ZIP
            FileOutputStream fileOutputStream = new FileOutputStream(zipFilePath);
            ZipArchiveOutputStream zipOutputStream = new ZipArchiveOutputStream(fileOutputStream);

            Iterable<BlobItem> blobs = containerClient.listBlobsByHierarchy( folderName + "/");

            for (BlobItem blobItem : blobs) {
                if (!blobItem.isPrefix()) {
                    String fileName = blobItem.getName();

                    BlobClient blobClient = containerClient.getBlobClient(fileName);
                    BinaryData fileContent = blobClient.downloadContent();
                    byte[] fileBytes = ((BinaryData) fileContent).toBytes();

                    ZipArchiveEntry zipEntry = new ZipArchiveEntry(fileName);
                    zipOutputStream.putArchiveEntry(zipEntry);
                    zipOutputStream.write(fileBytes);
                    zipOutputStream.closeArchiveEntry();
                }
            }

            zipOutputStream.finish();
            zipOutputStream.close();

            byte[] zipBytes = Files.readAllBytes(Paths.get(zipFilePath));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", folderName + ".zip");

            return new ResponseEntity<>(zipBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);

        }

    }


    @GetMapping("/downloadFile")
    public ResponseEntity<byte[]> downloadFile(@RequestParam("nomeFile") String nomeFile, String email) {
        try {


            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient("azienda");
            BlobClient blobClient = containerClient.getBlobClient(email + "/"+nomeFile);

            BinaryData fileContent = blobClient.downloadContent();
            byte[] bytes = ((BinaryData) fileContent).toBytes();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", nomeFile);
            FileOutputStream outputStream = new FileOutputStream("C:/Users/PC-1-/Desktop/"+nomeFile);
            outputStream.write(bytes);
            outputStream.close();
            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @GetMapping("/user")
    public User getUser(@RequestParam(value= "email") String email) {
        System.out.println("CIAOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO"+userService.showUser(email));
        return userService.showUser(email);
    }

    @GetMapping("/userId")
    public Optional<User> getUserID(@RequestParam(value= "id", defaultValue = "0") String id) {

        return userService.showUserID(id);
    }

    @ResponseStatus(code = HttpStatus.OK)
    @PostMapping("/addPurchase")
    public ResponseEntity addPurchase (@RequestBody @Valid Acquisto acquisto) throws FillInAllTheFieldsException {
        userService.addPurchase(acquisto);
        System.out.println("ANTONELAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        System.out.println(acquisto);
        return new ResponseEntity<>(new ResponseMessage("Added successful!"), HttpStatus.OK);
    }


    @GetMapping("/getAcquisti")
    public List<Acquisto> getAcquisti(@RequestParam(value= "email", defaultValue = "0") String email) {
        System.out.println("111111111111111111111111111111111111111111111111111");
        System.out.println(userService.findAcquisti(email));
        return userService.findAcquisti(email);
    }

    @GetMapping("/getCategorie")
    public List<PerCategoria> getCategorie(@RequestParam(value= "email", defaultValue = "0") String email) {
        System.out.println("9999999999999999999999999999999999999"+ userService.findCategorie(email));
        return userService.findCategorie(email);
    }

    @GetMapping("/getCategorieAnno")
    public List<CategoriaAnnoSpese> getCategorieAnno(@RequestParam(value= "email", defaultValue = "0") String email) {
        System.out.println("9999999999999999999999999999999999999"+ userService.findCategorie(email));
        return userService.findCategorieAnno(email);
    }


    @GetMapping("/getAnni")
    public List<Stringa> getAnni(@RequestParam(value= "email", defaultValue = "0") String email) {
        System.out.println("888888888888888888888888888"+ userService.findAnni(email));
        return userService.findAnni(email);
    }

    @GetMapping("/getId")
    public String getId(@RequestParam(value= "email", defaultValue = "0") String email) {
        System.out.println("22222222222222222222222222");
        System.out.println("ID:"+userService.findId(email));
        return userService.findId(email);
    }
}
