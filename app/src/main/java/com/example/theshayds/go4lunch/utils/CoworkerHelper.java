package com.example.theshayds.go4lunch.utils;

import com.example.theshayds.go4lunch.models.Coworker;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class CoworkerHelper {

    private static final String COLLECTION_NAME = "coworkers";

    // Firebase Firestore Instance
    public static FirebaseFirestore getInstance(){
        return FirebaseFirestore.getInstance();    }

    // Collection Reference
    public static CollectionReference getCoworkersCollection(){
        return getInstance().collection(COLLECTION_NAME);
    }

    // Create User
    public static void createUser(String uid,
                                  String username,
                                  String urlPicture,
                                  boolean hasChosen,
                                  String placeID,
                                  String placeName){

        Coworker userToCreate = new Coworker(uid, username, urlPicture, hasChosen, placeID, placeName);
        CoworkerHelper.getCoworkersCollection().document(uid).set(userToCreate);
    }

    public static Task<Void> updatePlace(String uid,
                                         boolean hasChosen,
                                         String placeID,
                                         String placeName){

        return CoworkerHelper.getCoworkersCollection().document(uid).update(
                "hasChosen", hasChosen,
                "placeName", placeName,
                "placeID", placeID);
    }

}
