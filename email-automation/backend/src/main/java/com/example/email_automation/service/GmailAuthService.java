package com.example.email_automation.service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;

/** 
 * This service authenticates Gmail API requests
*/

@Service
public class GmailAuthService {
    
    private static final String APPLICATION_NAME = "Email Automation";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens"; // store tokens locally

    // Scopes define what the app can do (e.g., read, send emails)
    private static final List<String> SCOPES = Collections.singletonList(GmailScopes.GMAIL_READONLY);

    private static Credential getCredentials() throws Exception {
        // load client secrets from credentials.json
        InputStream in = GmailAuthService.class.getResourceAsStream("/credentials.json");

        if (in == null) {
            throw new Exception("Resource not found: /credentials.json. " +
                "Ensure the file is located in the config directory.");
        }

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        
        // Build flow and trigger user authorization request
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
            GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, clientSecrets, SCOPES)
            .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
            .setAccessType("offline")
            .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public Gmail getGmailClient() throws Exception{
     
        // call getCredentials
        Credential credentials = getCredentials();

        // build Gmail client and return the Gmail client()
        return new Gmail.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                credentials)
            .setApplicationName(APPLICATION_NAME)
            .build();
    }
}
