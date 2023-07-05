package com.example.project

import android.app.Application
import android.util.Log
import com.amplifyframework.core.Amplify
import com.amplifyframework.storage.StorageException
import com.amplifyframework.storage.options.StorageDownloadFileOptions
import com.amplifyframework.storage.result.StorageDownloadFileResult
import com.amplifyframework.storage.result.StorageTransferProgress
import com.amplifyframework.storage.result.StorageUploadFileResult
import java.io.File

/**
 * This object is for managing the uploading and downloading of database files to and from AWS S3
 */
object AWSHelper {
    // this method will upload all 3 database files to S3 in a folder named with the user's ID
    fun backupRoom(application: Application) {
        // main db file
        val dbFile = File(application.getDatabasePath("room_database").absolutePath)
        Amplify.Storage.uploadFile(
            Amplify.Auth.currentUser.userId + "/RoomBackup",
            dbFile,
            { result: StorageUploadFileResult ->
                Log.i("AWSHelperUpload", "Successfully uploaded: " + result.key)
            },
            { storageFailure: StorageException? ->
                Log.e("AWSHelperUpload", "Upload failed", storageFailure)
            }
        )
        // wal file
        val dbWalFile = File(dbFile.path + "-wal")
        Amplify.Storage.uploadFile(
            Amplify.Auth.currentUser.userId + "/RoomBackupWAL",
            dbWalFile,
            { result: StorageUploadFileResult ->
                Log.i("AWSHelperUpload", "Successfully uploaded: " + result.key)
            },
            { storageFailure: StorageException? ->
                Log.e("AWSHelperUpload", "Upload failed", storageFailure)
            }
        )
        // shm file
        val dbShmFile = File(dbFile.path + "-shm")
        Amplify.Storage.uploadFile(
            Amplify.Auth.currentUser.userId + "/RoomBackupSHM",
            dbShmFile,
            { result: StorageUploadFileResult ->
                Log.i("AWSHelperUpload", "Successfully uploaded: " + result.key)
            },
            { storageFailure: StorageException? ->
                Log.e("AWSHelperUpload", "Upload failed", storageFailure)
            }
        )
    }

    // this method will download all 3 database files from the current user's S3 directory
    // if the user is new or just does not have any backup files in S3 then this will result in an
    // exception but the app will still work appropriately
    fun loadFromBackup(application: Application) {
        // these are nested in the onSuccess callbacks so that the order of the download is controlled
        // main db file
        val dbFile = File(application.getDatabasePath("room_database").absolutePath)
        Amplify.Storage.downloadFile(
            Amplify.Auth.currentUser.userId + "/RoomBackup",
            dbFile,
            StorageDownloadFileOptions.defaultInstance(),
            { progress: StorageTransferProgress ->
                Log.i("AWSHelperDownload", "Fraction completed: " + progress.fractionCompleted)
            },
            { result: StorageDownloadFileResult ->
                Log.i("AWSHelperDownload", "Successfully downloaded: " + result.file.name)

                // download wal file
                val dbWalFile = File(dbFile.path + "-wal")
                Amplify.Storage.downloadFile(
                    Amplify.Auth.currentUser.userId + "/RoomBackupWAL",
                    dbWalFile,
                    StorageDownloadFileOptions.defaultInstance(),
                    { progress: StorageTransferProgress ->
                        Log.i("AWSHelperDownload", "Fraction completed: " + progress.fractionCompleted)
                    },
                    { result: StorageDownloadFileResult ->
                        Log.i("AWSHelperDownload", "Successfully downloaded: " + result.file.name)

                        // download shm file
                        val dbShmFile = File(dbFile.path + "-shm")
                        Amplify.Storage.downloadFile(
                            Amplify.Auth.currentUser.userId + "/RoomBackupSHM",
                            dbShmFile,
                            StorageDownloadFileOptions.defaultInstance(),
                            { progress: StorageTransferProgress ->
                                Log.i("AWSHelperDownload", "Fraction completed: " + progress.fractionCompleted)
                            },
                            { result: StorageDownloadFileResult ->
                                Log.i("AWSHelperDownload", "Successfully downloaded: " + result.file.name)
                            },
                            { error: StorageException? ->
                                Log.e("AWSHelperDownload", "Download Failure", error)
                            }
                        )
                    },
                    { error: StorageException? ->
                        Log.e("AWSHelperDownload", "Download Failure", error)
                    }
                )
            },
            { error: StorageException? ->
                Log.e("AWSHelperDownload", "Download Failure", error)
            }
        )
    }
}