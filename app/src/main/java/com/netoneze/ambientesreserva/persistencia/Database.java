package com.netoneze.ambientesreserva.persistencia;

import com.google.firebase.firestore.FirebaseFirestore;

public abstract class Database {
    private static FirebaseFirestore db;

    protected Database(FirebaseFirestore db) {
        Database.db = db;
    }

    public static FirebaseFirestore getDatabase(){
        db = FirebaseFirestore.getInstance();
        return db;
    }
}
