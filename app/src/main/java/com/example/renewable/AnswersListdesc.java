package com.example.renewable;

public class AnswersListdesc {
   String  id="", answers="", desc="";

    public String getDesc() {
        return desc;
    }

    public AnswersListdesc(String id, String answers, String desc) {
        this.id = id;
        this.answers = answers;
        this.desc = desc;
    }

    public String getId() {
        return id;
    }

    public String getAnswers() {
        return answers;
    }
}
