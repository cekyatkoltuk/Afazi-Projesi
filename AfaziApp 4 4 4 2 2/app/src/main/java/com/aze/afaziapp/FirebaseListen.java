package com.aze.afaziapp;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseListen {

    private DatabaseReference databaseReference;


    public FirebaseListen(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Stories");

    }

    public void addData(){

        DatabaseReference story1Ref = databaseReference.child("story1");
        story1Ref.child("content").setValue("İki çocuk parkta oyun oynuyordu. Bir çocuk kırmızı topu alıp diğer çocuğa attı. Diğer çocuk topu yakalayamadı ve düşerek dizini yaraladı. Çocuk ağlamaya başladı. Annesi yanına koştu ve dizine pansuman yaptı. Çocuk daha sonra oyununa devam etti.");
        story1Ref.child("question1").setValue("Top hangi renkti?");
        story1Ref.child("question1Options").child("option1").setValue("Siyah");
        story1Ref.child("question1Options").child("option2").setValue("Mavi");
        story1Ref.child("question1Options").child("option3").setValue("Kırmızı");
        story1Ref.child("question1Options").child("option4").setValue("Sarı");
        story1Ref.child("question1Answer").setValue("Kırmızı");



        DatabaseReference story2Ref = databaseReference.child("story2");
        story2Ref.child("content").setValue("Ali ve Ayşe ormanda yürüyüş yapıyordu. Bir kuş sesi duydular" +
                " ve ağaca doğru baktılar. Ağaçta güzel renkli bir kuş vardı. Ali kuşu izlemeye başladı " +
                "ve Ayşe'ye gösterdi. Kuş göğe doğru uçup gitti. Ali ve Ayşe mutlu bir şekilde yürüyüşlerine " +
                "devam ettiler.");
        story2Ref.child("question1").setValue(" Ali ve Ayşe hangi ortamda yürüyüş yapıyordu?");
        story2Ref.child("question1Options").child("option1").setValue("Okulda");
        story2Ref.child("question1Options").child("option2").setValue("Ormanda");
        story2Ref.child("question1Options").child("option3").setValue("Plajda");
        story2Ref.child("question1Options").child("option4").setValue("Sokakta");
        story2Ref.child("question1Answer").setValue("Ormanda");

        story2Ref.child("question2").setValue("Ali ve Ayşe ağaçta ne gördüler?");
        story2Ref.child("question2Options").child("option1").setValue("Kuş");
        story2Ref.child("question2Options").child("option2").setValue("Karga");
        story2Ref.child("question2Options").child("option3").setValue("Kelebek");
        story2Ref.child("question2Options").child("option4").setValue("Muz");
        story2Ref.child("question2Answer").setValue("Kuş");

        story2Ref.child("question3").setValue("Kuş nereye uçtu?");
        story2Ref.child("question3Options").child("option1").setValue("Denize");
        story2Ref.child("question3Options").child("option2").setValue("Göğe");
        story2Ref.child("question3Options").child("option3").setValue("Ağaçtan başka bir ağaca");
        story2Ref.child("question3Options").child("option4").setValue("Yanlarına");
        story2Ref.child("question3Answer").setValue("Göğe");


        DatabaseReference story3Ref = databaseReference.child("story3");
        story3Ref.child("content").setValue("Ali markete gitmek için evden çıktı. Yolda bir köpeğe rastladı ve onu sevmek istedi. Köpek Ali'ye sevgiyle yaklaştı ve kuyruğunu salladı. Ali köpekle oynadıktan sonra markete doğru yoluna devam etti. Marketten alışverişini tamamladıktan sonra eve döndü ve köpeğin yanına geri geldi. Köpek Ali'yi sevinçle karşıladı.");
        story3Ref.child("question1").setValue("Ali markete gitmek nereden çıktı?");
        story3Ref.child("question1Options").child("option1").setValue("Okuldan");
        story3Ref.child("question1Options").child("option2").setValue("Evden");
        story3Ref.child("question1Options").child("option3").setValue("Parktan");
        story3Ref.child("question1Options").child("option4").setValue("Sinemadan");
        story3Ref.child("question1Answer").setValue("Evden");

        story3Ref.child("question2").setValue("Ali yolda neyle karşılaştı?");
        story3Ref.child("question2Options").child("option1").setValue("Köpek");
        story3Ref.child("question2Options").child("option2").setValue("Kedi");
        story3Ref.child("question2Options").child("option3").setValue("Kelebek");
        story3Ref.child("question2Options").child("option4").setValue("Tırtıl");
        story3Ref.child("question2Answer").setValue("Köpek");

        story3Ref.child("question3").setValue("Köpek Aliye ne yaptı?");
        story3Ref.child("question3Options").child("option1").setValue("Ali'den kaçtı");
        story3Ref.child("question3Options").child("option2").setValue("Sevgiyle yaklaştı");
        story3Ref.child("question3Options").child("option3").setValue("Ali'ye saldırdı");
        story3Ref.child("question3Options").child("option4").setValue("Oyun oynadı");
        story3Ref.child("question3Answer").setValue("Sevgiyle yaklaştı");

        // Yeni Hikayeler...
        DatabaseReference story4Ref = databaseReference.child("story4");
        story4Ref.child("content").setValue("Seda Hanım, alışverişe gitmek için hazırlandı. Süpermarket, evlerinden uzakta olduğu için çocuğu Mehmet'in arabasıyla birlikte yola çıktılar. Süpermarkete geldiklerinde, Seda Hanım bir tane alışveriş arabası aldı ve sürmeye başladı. Seda Hanım, yiyecekler alırken tazeliklerine dikkat ediyordu. Alışverişlerini tamamladıktan sonra evlerine geri döndüler.");
        story4Ref.child("question1").setValue("Seda Hanım ve çocuğu Mehmet alışverişe nereye gitmişler?");
        story4Ref.child("question1Options").child("option1").setValue("Süpermarket’e");
        story4Ref.child("question1Options").child("option2").setValue("Market'e");
        story4Ref.child("question1Options").child("option3").setValue("Bakkal'a");
        story4Ref.child("question1Options").child("option4").setValue("Manav'a");
        story4Ref.child("question1Answer").setValue("Süpermarket’e");

        story4Ref.child("question2").setValue("Seda Hanım ve çocuğu Mehmet markete ne ile gitmişler?");
        story4Ref.child("question2Options").child("option1").setValue("Yürüyerek");
        story4Ref.child("question2Options").child("option2").setValue("Otobüsle");
        story4Ref.child("question2Options").child("option3").setValue("Taksi ile");
        story4Ref.child("question2Options").child("option4").setValue("Mehmet'in arabasıyla");
        story4Ref.child("question2Answer").setValue("Mehmet'in arabasıyla");

        story4Ref.child("question3").setValue("Markete geldiklerinde Seda Hanım ne sürmeye başladı?");
        story4Ref.child("question3Options").child("option1").setValue("Bez çanta");
        story4Ref.child("question3Options").child("option2").setValue("Sepet");
        story4Ref.child("question3Options").child("option3").setValue("Alışveriş arabası");
        story4Ref.child("question3Options").child("option4").setValue("Bisiklet");
        story4Ref.child("question3Answer").setValue("Alışveriş arabası");

        // Yeni hikaye 2: Ayşe Hanım ve eş Mehmet Bey Köyde
        DatabaseReference story5Ref = databaseReference.child("story5");
        story5Ref.child("content").setValue("Ayşe Hanım ve eşi Mehmet Bey, her yaz tatilinde köylerine giderlermiş. Köylerindeki tarlaya domates ve biber ekerlermişler. Her yaz domates ve biberleri ekerler, Ağustos ayında da hasat yaparlarmış. Ancak bu yıl Ayşe Hanım, biberlerin yanına eklen çilek eklemiş. Mehmet Bey de eklemiş. Ağustos ayında tüm eklediklerini hasat etme zamanı gelmiş, ve Ağustos ayında hasat etmişler. Çilek en az ürünü veren olmuş. Ama domates ve biberler oldukça fazla ürün vermiş. Ayşe Hanım fazla domateslerden salça, fazla biberlerden de kahvaltılık sos yapmış. Mehmet Bey de hem Ayşe Hanım’a yardım etmiş.");
        story5Ref.child("question1").setValue("Ayşe Hanım ve Mehmet Bey birlikte her yaz nereye giderlermiş?");
        story5Ref.child("question1Options").child("option1").setValue("Köylerine");
        story5Ref.child("question1Options").child("option2").setValue("Şehire");
        story5Ref.child("question1Options").child("option3").setValue("Denize");
        story5Ref.child("question1Options").child("option4").setValue("Tatile");
        story5Ref.child("question1Answer").setValue("Köylerine");

        story5Ref.child("question2").setValue("Ayşe Hanım ve Mehmet Bey köylerindeki tarlaya her sene ne ekerlermiş?");
        story5Ref.child("question2Options").child("option1").setValue("Buğday");
        story5Ref.child("question2Options").child("option2").setValue("Patates");
        story5Ref.child("question2Options").child("option3").setValue("Domates ve biber");
        story5Ref.child("question2Options").child("option4").setValue("Mısır");
        story5Ref.child("question2Answer").setValue("Domates ve biber");

        story5Ref.child("question3").setValue("Ayşe Hanım bu sefer tarlaya biber ve domatesin yanına ne eklemek istemiş?");
        story5Ref.child("question3Options").child("option1").setValue("Salatalık");
        story5Ref.child("question3Options").child("option2").setValue("Çilek");
        story5Ref.child("question3Options").child("option3").setValue("Havuç");
        story5Ref.child("question3Options").child("option4").setValue("Patlıcan");
        story5Ref.child("question3Answer").setValue("Çilek");

        DatabaseReference story6Ref = databaseReference.child("story6");
        story6Ref.child("content").setValue("Ali Bey, yazları her sabah balık tutmak için yola çıkarmış. Kırmızı renkteki oltası çok uzun bir oltaymış. Aynı zamanda en çok sevdiği oltası da kırmızı oltasıymış. Ali Bey, öğlene kadar 3 tane balık tutmuş. Tuttuğu 3 balığı akşam yemeğinde ailesiyle yemek için eve getirmiş. Eşi Meryem Hanım, her balığı önce ayıklamış. Sonrasında da pişirmek için hazırlamış. Ali Bey de salata yapmış. Sonrasında beraber sofrayı hazırlamışlar. Birlikte balıkları yedikten sonra sofrayı toplamışlar.");
        story6Ref.child("question1").setValue("Ali Bey, yazları her sabah ne için yola çıkarmış?");
        story6Ref.child("question1Options").child("option1").setValue("Piknik yapmak");
        story6Ref.child("question1Options").child("option2").setValue("Balık tutmak");
        story6Ref.child("question1Options").child("option3").setValue("Yürüyüş yapmak");
        story6Ref.child("question1Options").child("option4").setValue("Spor yapmak");
        story6Ref.child("question1Answer").setValue("Balık tutmak");

        story6Ref.child("question2").setValue("Ali Beyin en sevdiği ve en uzun oltası hangi renkti?");
        story6Ref.child("question2Options").child("option1").setValue("Mavi");
        story6Ref.child("question2Options").child("option2").setValue("Yeşil");
        story6Ref.child("question2Options").child("option3").setValue("Siyah");
        story6Ref.child("question2Options").child("option4").setValue("Kırmızı");
        story6Ref.child("question2Answer").setValue("Kırmızı");

        story6Ref.child("question3").setValue("Ali Bey, o gün öğlene kadar kaç adet balık tutmuş?");
        story6Ref.child("question3Options").child("option1").setValue("1 tane");
        story6Ref.child("question3Options").child("option2").setValue("2 tane");
        story6Ref.child("question3Options").child("option3").setValue("3 tane");
        story6Ref.child("question3Options").child("option4").setValue("4 tane");
        story6Ref.child("question3Answer").setValue("3 tane");
    }
}
