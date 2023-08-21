package com.example.voting_programming_language;

public class VotingInfo {
    private String name;
    private String language;
    private int count;

    public VotingInfo(String name,String langauge,int count) {
        this.name=name;
        this.language=langauge;
        this.count=count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getLanguage(){
        return language;
    }

    public void setLanguage(String language){
        this.language=language;
    }
}
