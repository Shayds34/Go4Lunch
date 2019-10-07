package com.example.theshayds.go4lunch.utils;

import com.example.theshayds.go4lunch.models.Coworker;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class CoworkerHelper {

    private static final String COLLECTION_NAME = "coworkers";

    // Firebase Firestore Instance
    public static FirebaseFirestore getInstance(){
        return FirebaseFirestore.getInstance();    }

    // Collection Reference
    public static CollectionReference getCoworkersCollection(){
        return getInstance().collection(COLLECTION_NAME);
    }

    public static Query getAllCoworkers(){
        return getCoworkersCollection().document().collection(COLLECTION_NAME);
    }

    // Create
    public static void createUser(String uid,
                                  String username,
                                  String urlPicture,
                                  boolean hasChosen,
                                  String placeID,
                                  String placeName,
                                  String placeAddress,
                                  String placeWebsite,
                                  String placePhoto,
                                  String placePhone,
                                  String placeUrl){

        Coworker userToCreate = new Coworker(uid, username, urlPicture, hasChosen, placeID, placeName, placeAddress, placeWebsite, placePhoto, placePhone, placeUrl);
        CoworkerHelper.getCoworkersCollection().document(uid).set(userToCreate);
    }

    // Get
    public static Task<DocumentSnapshot> getUser(String uid){
        return CoworkerHelper.getCoworkersCollection().document(uid).get();
    }

    public static Task<Void> updatePlace(String uid,
                                         String username,
                                         boolean hasChosen,
                                         String placeID, String placeName){

        return CoworkerHelper.getCoworkersCollection().document(uid).update(
                "userName", username,
                "hasChosen", hasChosen,
                "placeName", placeName,
                "placeID", placeID);
    }

    // Delete user (not used yet)
    public static Task<Void> deleteUser(String uid){
        return CoworkerHelper.getCoworkersCollection().document(uid).delete();
    }


}
