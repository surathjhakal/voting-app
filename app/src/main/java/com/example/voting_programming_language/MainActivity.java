package com.example.voting_programming_language;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Tag;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText name;
    private Spinner languages;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    ProgressBar progress_loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("Be A Voter");
        languages = findViewById(R.id.languages);
        String[] options = {"Python", "Java", "Javascript","Go","C++"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languages.setAdapter(adapter);
        languages.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item's TextView
                TextView textView = (TextView) view;

                // Set the text color for the selected item
                textView.setTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.black));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle nothing selected if needed
            }
        });
        languages.setSelection(0);
    }
    public void voteLanguage(View v) {
        progress_loader=findViewById(R.id.progress_loader);
        progress_loader.setVisibility(View.VISIBLE);
        name = findViewById(R.id.name);
        Spinner spinner = findViewById(R.id.languages);
        String selectedLanguage = spinner.getSelectedItem().toString();
        String nameInput = name.getText().toString();

        if (TextUtils.isEmpty(nameInput)) {
            Toast.makeText(MainActivity.this, "Please add name", Toast.LENGTH_SHORT).show();
            progress_loader.setVisibility(View.GONE);
        } else {
            addDatatoFirestore(nameInput, selectedLanguage);
        }
    }
    private void addDatatoFirestore(String nameInput, String selectedLanguage) {
         db.collection("VotingInfo").whereEqualTo("name", nameInput).whereEqualTo("language",selectedLanguage).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
             @Override
             public void onComplete(@NonNull Task<QuerySnapshot> task) {
                 if (task.isSuccessful()) {
                     if(task.getResult().size()==0){
                         VotingInfo votingInfo = new VotingInfo(nameInput,selectedLanguage,1);

                         db.collection("VotingInfo").add(votingInfo).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                             @Override
                             public void onSuccess(DocumentReference documentReference) {
                                 progress_loader.setVisibility(View.GONE);
                                 Toast.makeText(MainActivity.this, "Your vote has been submited", Toast.LENGTH_SHORT).show();
                             }
                         }).addOnFailureListener(new OnFailureListener() {
                             @Override
                             public void onFailure(@NonNull Exception e) {
                                 progress_loader.setVisibility(View.GONE);
                                 Toast.makeText(MainActivity.this, "Fail to vote \n" + e, Toast.LENGTH_SHORT).show();
                             }
                         });
                     }else{
                         for (QueryDocumentSnapshot document : task.getResult()) {
                             Map<String, Object> data = document.getData();
                             int count=Integer.parseInt(data.get("count").toString());
                             data.put("count", count+1);
                             db.collection("VotingInfo").document(document.getId()).set(data, SetOptions.merge());
                             Log.d("Firestore", document.getId() + " => " + document.getData());
                         }
                         progress_loader.setVisibility(View.GONE);
                         Toast.makeText(MainActivity.this, "Your vote has been submited", Toast.LENGTH_SHORT).show();
                     }
                 } else {
                     progress_loader.setVisibility(View.GONE);
                     Log.d("Firestore", "Error getting documents: ", task.getException());
                 }
             }
         });
    }
    public void goToResultsPage(View v){
        Intent intent = new Intent(this, ShowResults.class);
        startActivity(intent);
    }
}