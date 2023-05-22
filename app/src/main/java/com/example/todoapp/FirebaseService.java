package com.example.todoapp;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseService {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<Note> items = new ArrayList<>();
    private MainActivity mainActivity;

    public FirebaseService(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        startListener();
    }

    public void addNote(String text){
        DocumentReference ref = db.collection("notes").document();
        Map<String, Object> map = new HashMap<>();
        map.put("text", text);
        map.put("timestamp", FieldValue.serverTimestamp());
        ref.set(map);
    }

    public void updateNote(String id, String text){
        DocumentReference ref = db.collection("notes").document(id);
        ref.update("text", text);
    }

    public void deleteNote(String id){
        DocumentReference ref = db.collection("notes").document(id);
        ref.delete();
    }

    public void startListener(){
        db.collection("notes")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((snap, error) -> {
                    items.clear();
                    for(DocumentSnapshot doc: snap.getDocuments()){
                        String id = doc.getId();
                        String text = doc.getData().get("text").toString();
                        items.add(new Note(id, text));
                    }
                    mainActivity.adapter.notifyDataSetChanged();
                });
    }
}

