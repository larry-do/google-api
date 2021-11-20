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

        driveAPIService.getSubFolders(DriveAPIService.ROOT_FOLDER);
    }
}
