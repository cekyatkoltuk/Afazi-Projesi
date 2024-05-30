package com.aze.afaziapp.models;

public class ModelCard {

    String cardId,uid,useremail,downloadurl,title;


    public ModelCard() {

    }

    public ModelCard(String cardId,String uid, String useremail, String downloadurl, String title) {
        this.cardId=cardId;
        this.uid = uid;
        this.useremail = useremail;
        this.downloadurl = downloadurl;
        this.title = title;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUseremail() {
        return useremail;
    }

    public void setUseremail(String useremail) {
        this.useremail = useremail;
    }

    public String getDownloadurl() {
        return downloadurl;
    }

    public void setDownloadurl(String downloadurl) {
        this.downloadurl = downloadurl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }
}
