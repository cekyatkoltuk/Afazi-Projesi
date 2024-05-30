package com.aze.afaziapp.models;

public class ModelListen {

    String question;
    String oa;
    String ob;
    String oc;
    String od;
    String ans;

    public ModelListen(String question, String oa, String ob, String oc, String od, String ans) {
        this.question = question;
        this.oa = oa;
        this.ob = ob;
        this.oc = oc;
        this.od = od;
        this.ans = ans;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getOa() {
        return oa;
    }

    public void setOa(String oa) {
        this.oa = oa;
    }

    public String getOb() {
        return ob;
    }

    public void setOb(String ob) {
        this.ob = ob;
    }

    public String getOc() {
        return oc;
    }

    public void setOc(String oc) {
        this.oc = oc;
    }

    public String getOd() {
        return od;
    }

    public void setOd(String od) {
        this.od = od;
    }

    public String getAns() {
        return ans;
    }

    public void setAns(String ans) {
        this.ans = ans;
    }
}
