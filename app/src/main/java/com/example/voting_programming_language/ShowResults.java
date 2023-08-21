package com.example.voting_programming_language;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ShowResults extends AppCompatActivity {
    private LinearLayout languages;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ProgressBar progress_loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.see_results);
        getSupportActionBar().setTitle("Be A Voter");
        languages=findViewById(R.id.languagesList);

        progress_loader=findViewById(R.id.progress_loader1);
        progress_loader.setVisibility(View.VISIBLE);

        System.out.println("hello");
        db.collection("VotingInfo")
                .get()
                .addOnCompleteListener(voter -> {
                    if (voter.isSuccessful()) {
                        Map<String, HashMap<String,Integer>> languagesObj=new HashMap<>();
                        for (QueryDocumentSnapshot document : voter.getResult()) {
                            // Access document fields using document.getData()
                            String name = document.getString("name");
                            String language = document.getString("language");
                            int count=Integer.parseInt(document.getData().get("count").toString());
                            if(languagesObj.containsKey(language)){
                                HashMap<String,Integer> names=languagesObj.get(language);
                                names.put(name,count);
                                languagesObj.put(language,names);
                            }else{
                                languagesObj.put(language,new HashMap<String,Integer>(){{put(name,count);}});
                            }
                        }
                        System.out.println(languagesObj);
                        List<Map.Entry<String, HashMap<String,Integer>>> sortedList=sortLanguages(languagesObj);
                        System.out.println(sortedList);
                        String[] colors={"#e69810","#ed2e2e","#16f28c","#f2ef16","#c829f2"};
                        int index=0;
                        for (Map.Entry<String, HashMap<String,Integer>> entry : sortedList) {
                            String langName=entry.getKey();
                            HashMap<String,Integer> langVoter=entry.getValue();
                            System.out.println(langName);
                            System.out.println(langVoter);
                            System.out.println("hello 1");
                            // Adding Language Name & total count Container
                            LinearLayout languageNameLayout = new LinearLayout(this);
                            languageNameLayout.setOrientation(LinearLayout.HORIZONTAL);
                            languageNameLayout.setPadding(20,20,20,20);
                            System.out.println("hello 2");
                            languageNameLayout.setBackgroundColor(Color.parseColor(colors[index]));
                            index++;
                            System.out.println("hello 3");

                            TextView languageName = new TextView(this);
                            TextView languageCount = new TextView(this);

                            languageName.setText(langName);
                            languageName.setTextSize(30);
                            languageName.setWidth(850);

                            System.out.println("hello 4");

                            languageCount.setText(getVoterCount(langVoter));
                            System.out.println("hello 4.1");
                            languageCount.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            System.out.println("hello 4.2");
                            languageCount.setTextSize(30);

                            System.out.println("hello 5");

                            languageNameLayout.addView(languageName);
                            languageNameLayout.addView(languageCount);

                            languages.addView(languageNameLayout);

                            System.out.println("hello 6");

                            // Adding Language Voter List with their count Container
                            ScrollView scrollVoterList = new ScrollView(this);
                            scrollVoterList.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                            );

                            layoutParams.bottomMargin = getResources().getDimensionPixelSize(R.dimen.margin_bottom);

                            scrollVoterList.setLayoutParams(layoutParams);

                            System.out.println("hello 7");

                            LinearLayout languageVoterList = new LinearLayout(this);
                            languageVoterList.setOrientation(LinearLayout.VERTICAL);
                            languageVoterList.setPadding(40,40,40,40);

                            System.out.println("hello 8");

                            List<Map.Entry<String, Integer>> sortedVoters=sortVoters(langVoter);

                            for(Map.Entry<String,Integer> voterObj:sortedVoters){
                                LinearLayout languageVoterName = new LinearLayout(this);
                                languageVoterName.setOrientation(LinearLayout.HORIZONTAL);

                                TextView voterName = new TextView(this);
                                TextView voterCount = new TextView(this);

                                voterName.setText(voterObj.getKey());
                                voterName.setTextSize(20);
                                voterName.setWidth(800);
                                voterName.setTextColor(Color.parseColor("#000000"));

                                voterCount.setText(Integer.toString(voterObj.getValue()));
                                voterCount.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                voterCount.setTextSize(20);
                                voterCount.setTextColor(Color.parseColor("#000000"));

                                languageVoterName.addView(voterName);
                                languageVoterName.addView(voterCount);

                                languageVoterList.addView(languageVoterName);
                            }
                            progress_loader.setVisibility(View.GONE);
                            scrollVoterList.addView(languageVoterList);
                            languages.addView(scrollVoterList);
                        }
//                        setContentView(R.layout.see_results);
                    } else {
                        Log.w("Firestore", "Error getting documents.", voter.getException());
//                        setContentView(R.layout.see_results);
                    }
                });
    }
    public String getVoterCount(HashMap<String,Integer> voterList){
        int count=0;
        for(Map.Entry<String,Integer> voter:voterList.entrySet()){
            count+=voter.getValue();
        }
        return Integer.toString(count);
    }
    public List<Map.Entry<String, HashMap<String,Integer>>> sortLanguages(Map<String, HashMap<String,Integer>> languagesObj){
        List<Map.Entry<String, HashMap<String,Integer>>> entryList = new ArrayList<>(languagesObj.entrySet());

        // Sort the List based on values using a custom comparator
        entryList.sort(new Comparator<Map.Entry<String, HashMap<String,Integer>>>() {
            @Override
            public int compare(Map.Entry<String, HashMap<String,Integer>> entry1, Map.Entry<String, HashMap<String,Integer>> entry2) {
                // Compare values in ascending order
                return getVoterCount(entry2.getValue()).compareTo(getVoterCount(entry1.getValue()));
            }
        });
        return entryList;
    }
    public List<Map.Entry<String, Integer>> sortVoters(Map<String, Integer> languagesObj){
        List<Map.Entry<String,Integer>> entryList = new ArrayList<>(languagesObj.entrySet());

        // Sort the List based on values using a custom comparator
        entryList.sort(new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> entry1, Map.Entry<String, Integer> entry2) {
                // Compare values in ascending order
                return entry2.getValue().compareTo(entry1.getValue());
            }
        });
        return entryList;
    }
    public void goToMainPage(View v){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
