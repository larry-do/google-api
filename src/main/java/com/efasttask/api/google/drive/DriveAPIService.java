package com.efasttask.api.google.drive;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DriveAPIService {
    public static final String ROOT_FOLDER = "root";

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private final String APPLICATION_NAME;
    private final String TOKENS_DIRECTORY_PATH;

    private final Drive driveService;

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private final List<String> SCOPES;
    private final InputStream credentialsFileInputStream;

    public DriveAPIService(final String APPLICATION_NAME, final InputStream credentialsFileInputStream, final String TOKENS_DIRECTORY_PATH, String... SCOPES) throws GeneralSecurityException, IOException {
        this.APPLICATION_NAME = APPLICATION_NAME;
        this.credentialsFileInputStream = credentialsFileInputStream;
        this.TOKENS_DIRECTORY_PATH = TOKENS_DIRECTORY_PATH;
        this.SCOPES = Arrays.asList(SCOPES);

        this.driveService = createService();
    }

    public DriveAPIService(final String APPLICATION_NAME, final String CREDENTIALS_FILE_PATH, final String TOKENS_DIRECTORY_PATH, String... SCOPES) throws GeneralSecurityException, IOException {
        this(APPLICATION_NAME, new FileInputStream(CREDENTIALS_FILE_PATH), TOKENS_DIRECTORY_PATH, SCOPES);
    }

    /**
     * Create an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(credentialsFileInputStream));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    private Drive createService() throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public Drive getDriveService() {
        return driveService;
    }

    public List<File> getSubFolders(String parentFolderId) throws IOException {
        String query = " mimeType = 'application/vnd.google-apps.folder' and '%s' in parents";

        List<File> folders = new ArrayList<>();
        String pageToken = null;
        do {
            FileList result = this.driveService.files().list().setQ(String.format(query, parentFolderId)).setSpaces("drive")
                    .setFields("nextPageToken, files(id, name, createdTime)")
                    .setPageToken(pageToken).execute();
            folders.addAll(result.getFiles());
            pageToken = result.getNextPageToken();
        } while (pageToken != null);

        return folders;
    }

    public File makeFolder(final String parentFolderId, String newFolderName) throws IOException {
        File fileMetadata = new File();
        fileMetadata.setName(newFolderName);
        fileMetadata.setMimeType("application/vnd.google-apps.folder");
        if (parentFolderId != null) {
            List<String> parents = Collections.singletonList(parentFolderId);
            fileMetadata.setParents(parents);
        }
        return driveService.files().create(fileMetadata).setFields("id, name").execute();
    }

    public File uploadFile(String folderId, String contentType,
                           String fileName, AbstractInputStreamContent uploadStreamContent) throws IOException {
        File fileMetadata = new File();
        fileMetadata.setName(fileName);
        List<String> parents = Collections.singletonList(folderId);
        fileMetadata.setParents(parents);
        return driveService.files().create(fileMetadata, uploadStreamContent)
                .setFields("id, webContentLink, webViewLink, parents").execute();
    }

    public File uploadFile(String folderId, String contentType,
                           String fileName, byte[] data) throws IOException {
        AbstractInputStreamContent uploadStreamContent = new ByteArrayContent(contentType, data);
        return uploadFile(folderId, contentType, fileName, uploadStreamContent);
    }

    public File uploadFile(String folderId, String contentType,
                           String fileName, java.io.File file) throws IOException {
        AbstractInputStreamContent uploadStreamContent = new FileContent(contentType, file);
        return this.uploadFile(folderId, contentType, fileName, uploadStreamContent);
    }

    public File uploadFile(String folderId, String fileName, java.io.File file) throws IOException {
        AbstractInputStreamContent uploadStreamContent = new FileContent(URLConnection.guessContentTypeFromName(file.getName()), file);
        return this.uploadFile(folderId, URLConnection.guessContentTypeFromName(file.getName()), fileName, uploadStreamContent);
    }

    public File uploadFile(String folderId, java.io.File file) throws IOException {
        AbstractInputStreamContent uploadStreamContent = new FileContent(URLConnection.guessContentTypeFromName(file.getName()), file);
        return this.uploadFile(folderId, URLConnection.guessContentTypeFromName(file.getName()), file.getName(), uploadStreamContent);
    }

    public File uploadFile(String folderId, String contentType,
                           String fileName, InputStream inputStream) throws IOException {
        AbstractInputStreamContent uploadStreamContent = new InputStreamContent(contentType, inputStream);
        return uploadFile(folderId, contentType, fileName, uploadStreamContent);
    }

    public File findFile(String fileId) throws IOException {
        return this.driveService.files().get(fileId).execute();
    }

    public ByteArrayOutputStream downloadFile(String fileId) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        this.driveService.files().get(fileId)
                .executeMediaAndDownloadTo(outputStream);
        return outputStream;
    }

    public void storeFileToDisk(String directory, String fileId) throws IOException {
        Files.write((new java.io.File(directory)).toPath(), this.downloadFile(fileId).toByteArray());
    }

    public InputStream getFileInputStream(String fileId) throws IOException {
        return this.driveService.files().get(fileId)
                .executeMediaAsInputStream();
    }
}