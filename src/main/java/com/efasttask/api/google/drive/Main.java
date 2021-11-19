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

        driveAPIService.uploadFile(DriveAPIService.ROOT_FOLDER, new File("E:\\Music\\Shun Akiyama (秋山 駿) - Baka Mitai (馬鹿みたい) Lyrics (Romaji+Kanji+Eng Trans) Yakuza 5 (龍が如く) OST.mp3"));
    }
}
