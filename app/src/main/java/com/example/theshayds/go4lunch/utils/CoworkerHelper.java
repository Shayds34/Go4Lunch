package com.example.theshayds.go4lunch.utils;

import com.example.theshayds.go4lunch.models.Coworker;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.w3c.dom.Document;

public class CoworkerHelper {

    private static final String COLLECTION_NAME = "coworkers";

    // Firebase Firestore Instance
    public static FirebaseFirestore getInstance(){
        return FirebaseFirestore.getInstance();    }

    // Collection Reference
    public static CollectionReference getCoworkersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public static Query getAllCoworkers(){
        return getCoworkersCollection().document().collection(COLLECTION_NAME);
    }

    // Create
    public static Task<Void> createUser(String uid, String username, String urlPicture, boolean hasChosen, String choice){
        Coworker userToCreate = new Coworker(uid, username, urlPicture, hasChosen, choice);
        return CoworkerHelper.getCoworkersCollection().document(uid).set(userToCreate);
    }

    // Get
    public static Task<DocumentSnapshot> getUser(String uid){
        return CoworkerHelper.getCoworkersCollection().document(uid).get();
    }

    // Update
    public static Task<Void> updateUsername(String uid, String userName){
        return CoworkerHelper.getCoworkersCollection().document(uid).update("username", userName);
    }

    public static Task<Void> updateHasChosen(String uid, boolean hasChosen){
        return CoworkerHelper.getCoworkersCollection().document(uid).update("hasChosen", hasChosen);
    }


    public static Task<Void> updatePlace(String uid, String placeChoice){
        return CoworkerHelper.getCoworkersCollection().document(uid).update("placeChoice", placeChoice);
    }

    // Delete user (not used yet)
    public static Task<Void> deleteUser(String uid){
        return CoworkerHelper.getCoworkersCollection().document(uid).delete();
    }


}
