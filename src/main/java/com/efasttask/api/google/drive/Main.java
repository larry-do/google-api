package com.efasttask.api.google.drive;

import com.google.api.services.drive.DriveScopes;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class Main {
    public static void main(String[] args) throws GeneralSecurityException, IOException {
        DriveAPIService driveAPIService = new DriveAPIService("Paint Service",
                Main.class.getClassLoader().getResource("credentials.json").getPath(),
                "drive_token", DriveScopes.DRIVE);

        com.google.api.services.drive.model.File file = driveAPIService.uploadFile(DriveAPIService.ROOT_FOLDER,
                new File("C:\\Users\\Admin\\Documents\\DEMO DANG BAI\\TGH1321\\TGH1321 - FILE IN.jpg"));
        if (file != null) {
            System.out.println(file.toPrettyString());
        }
    }
}
