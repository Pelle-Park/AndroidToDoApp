package com.example.todoapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    ArrayAdapter<Note> adapter;
    private FirebaseService firebaseService;
    private ListView listView;
    private Button newNoteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseService = new FirebaseService(this);
        adapter = new ArrayAdapter<>(this, R.layout.myrow, R.id.rowTextView, firebaseService.items);
        listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> openEditDialog(firebaseService.items.get(position)));
        listView.setOnItemLongClickListener((parent, view, position, id) -> openDeleteDialog(position));
        newNoteButton = findViewById(R.id.newNoteButton);
        newNoteButton.setOnClickListener(v -> openAddDialog());
    }

    private boolean openDeleteDialog(int position) {
        Note note = firebaseService.items.get(position);
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Delete Note")
                .setMessage(Html.fromHtml("Would you like to delete this note?<br/><br/><b>" + note.getText() + "</b>", Html.FROM_HTML_MODE_COMPACT))
                .setPositiveButton(android.R.string.yes, (dialog, which) -> firebaseService.deleteNote(note.getId()))
                .setNegativeButton(android.R.string.no, null)
                .show();
        return true;
    }

    public void openAddDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_layout, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .setTitle("New Note")
                .setPositiveButton("Add", (dialog1, id) -> {
                    EditText editText = view.findViewById(R.id.editText);
                    String text = editText.getText().toString();
                    firebaseService.addNote(text);
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }
    public void openEditDialog(Note note) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_layout, null);
        EditText editText = view.findViewById(R.id.editText);
        editText.setText(note.getText());
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .setTitle("Edit Note")
                .setPositiveButton("Save", (dialog1, id) -> {
                    String text = editText.getText().toString();
                    firebaseService.updateNote(note.getId(), text);
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

}
