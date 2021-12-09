import com.efasttask.api.google.drive.DriveAPIService;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class Main {
    public static void main(String[] args) throws GeneralSecurityException, IOException {
        DriveAPIService driveAPIService = new DriveAPIService("Paint Service",
                Main.class.getClassLoader().getResource("credentials.json").getPath(),
                "drive_token", DriveScopes.DRIVE);

        String pageToken = null;
        do {
            FileList result = driveAPIService.getDriveService().files().list()
                    .setQ("name contains 'FILE IN'")
                    .setSpaces("drive")
                    .setFields("nextPageToken, files(id, name)")
                    .setPageToken(pageToken)
                    .execute();
            for (File file : result.getFiles()) {
                System.out.printf("Found file: %s (%s)\n",
                        file.getName(), file.getId());
                driveAPIService.storeFileToDisk(System.getProperty("user.dir") + "/" + file.getName(), file.getId());
            }
            pageToken = result.getNextPageToken();
        } while (pageToken != null);
    }
}
