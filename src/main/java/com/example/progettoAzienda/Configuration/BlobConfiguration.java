package com.example.progettoAzienda.Configuration;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.common.StorageSharedKeyCredential;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BlobConfiguration {

    //@Value("${azure.storage.account-name}")
    private String accountName="blobprogetto";

    //@Value("${azure.storage.account-key}")
    private String accountKey = "u9tw+Gs+FQ9u0pzk/XUoI81LHvEeSHdV941B00D4w6Rb4BqFrUcbQlT3etA9Jf/Ubk2H2zLs/v+d+AStpfobgg==";

    //@Value("${azure.storage.blob-endpoint}")
    private String blobEndpoint = "https://blobprogetto.blob.core.windows.net/";

    @Bean
    public BlobServiceClient blobServiceClient() {
        StorageSharedKeyCredential credential = new StorageSharedKeyCredential(accountName, accountKey);
        return new BlobServiceClientBuilder()
                .endpoint(blobEndpoint)
                .credential(credential)
                .buildClient();
    }
}