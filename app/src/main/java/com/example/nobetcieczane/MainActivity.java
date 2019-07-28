package com.example.nobetcieczane;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.nobetcieczane.modals.Eczane;
import com.example.nobetcieczane.modals.EczaneDetay;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    String tokenText;
    WebView webView;
    Spinner spinner;
    Document document;
    List<EczaneDetay> eczaneList;
    EczaneAdapter eczaneAdapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listview);
        webView = new WebView(getApplicationContext());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new JsBridge(), "Android");// name'e android yazılmalı baska birsey yazılmamalı
        this.getToken();
        final String ilceler[] = {"Adalar", "Arnavutkoy",
                "Ataşehir", "Avcılar", "Bağcılar", "Bahçelievler", "Bakırköy",
                "Başakşehir", "Bayrampaşa", "Beşiktaş", "Beykoz", "Beylikdüzü",
                "Beyoğlu", "Büyükçekmece", "Çatalca", "Çekmeköy", "Esenler",
                "Esenyurt", "Eyüp", "Fatih", "Gaziosmanpaşa", "Güngören", "Kadıköy",
                "Kağıthane", "Kartal", "Küçükcekmece", "Maltepe", "Pendik",
                "Sancaktepe", "Sarıyer", "Şile", "Silivri", "Şişli", "Sultanbeyli",
                "Sultangazi", "Tuzla", "Ümraniye", "Üsküdar", "Zeytinburnu"};
        final int ilceid[] = {1, 33, 34, 2, 3, 4, 5, 35, 6, 7, 8, 36, 9, 10, 11, 37, 13, 38, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 39, 24, 27, 25, 28, 26, 40, 29, 30, 31, 32};
        spinner = (Spinner) findViewById(R.id.ilceSpinner);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ilceler);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
     /*   spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            int index= Integer.parseInt(String.valueOf(java.util.Arrays.asList(ilceler).indexOf(spinner.getSelectedItem().toString())));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

    }

    public void getEczane(String id) {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                view.loadUrl("javascript:window.Android.htmlEczaneDetay(" +
                        "'<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
            }
        });
        webView.loadUrl("https://apps.istanbulsaglik.gov.tr/Eczane/nobetci?id=" + id + "&token=" + tokenText);
    }

    public void getToken() {
        webView.setWebViewClient(new WebViewClient() {
                                     @Override
                                     public void onPageFinished(WebView view, String url) {
                                         super.onPageFinished(view, url);
                                         view.loadUrl("javascript:window.Android.htmlContentForToken(" +
                                                 "'<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                                     }
                                 }


        );
        webView.loadUrl("https://apps.istanbulsaglik.gov.tr/Eczane");

    }


    class JsBridge extends MainActivity {
        @JavascriptInterface
        public void htmlContentForToken(String str) {

            String token[] = str.split("token");

            if (token.length > 1) {
                String[] token2 = token[1].split(Pattern.quote("}"));
                tokenText = token2[0].replaceAll(" ", "")
                        .replaceAll(":", "").replaceAll("\"", "");
                Message message = new Message();
                message.what = 1;
                message.obj = tokenText;
                handler.sendMessage(message);
            }
        }

        @JavascriptInterface
        public void htmlEczaneDetay(String str) {
            Message message = new Message();
            message.what = 2;
            message.obj = str;
            handler.sendMessage(message);

        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                tokenText = (String) msg.obj;
            } else if (msg.what == 2) {

                Eczane ec = parseHtml((String) msg.obj);
                eczaneList = ec.getEczaneDetay();
                eczaneAdapter = new EczaneAdapter(eczaneList, MainActivity.this, MainActivity.this);
                listView.setAdapter(eczaneAdapter);
            }
        }
    };

    private Eczane parseHtml(String htmlKaynak) {

        document = Jsoup.parse(htmlKaynak);
        Elements table = document.select("table.ilce-nobet-detay");
        Elements ilceDetay = table.select("caption>b");
        Eczane eczane = new Eczane();
        eczane.setTarih(ilceDetay.get(0).text());
        eczane.setIlceIsmi(ilceDetay.get(1).text());
        Log.i("cevapp", "" + ilceDetay);
        Elements eczaneDetayElemet = document.select("table.nobecti-eczane");
        // Log.i("cevapppp",""+eczaneDetayElemet.size());
        List<EczaneDetay> eczaneDetayList = new ArrayList<>();
        for (Element el : eczaneDetayElemet) {
            EczaneDetay eczaneDetay = getEczaneDetay(el);
            Log.i("cevappp", eczaneDetay.toString());
            eczaneDetay = getEczaneDetay(el);
            if (eczaneDetay != null)
                eczaneDetayList.add(eczaneDetay);
        }
        eczane.setEczaneDetay(eczaneDetayList);
        return eczane;

    }

    public EczaneDetay getEczaneDetay(Element el) {
        String fax = "";
        String adres = "";
        String tel = "";
        String adresTarif = "";
        EczaneDetay eczaneDetay = new EczaneDetay();
        Elements eczaneIsmıTag = el.select("thead");
        String eczaneIsmi = eczaneIsmıTag.select("div").attr("title");
        eczaneDetay.setEczaneIsmi(eczaneIsmi);
        //Log.i("cevapppp", "" + eczaneIsmi);

        Elements trTags = el.select("tbody>tr");
        Elements adresTags = trTags.select("tr#adres");
        adres = adresTags.select("label").get(1).text();
        eczaneDetay.setAdres(adres);

        Elements telTags = trTags.select("tr#Tel");
        tel = telTags.select("label").get(1).text();
        //Log.i("cevapppp", "" + tel);
        eczaneDetay.setTelefon(tel);

        Element faxTags = trTags.get(2);
        fax = faxTags.select("label").get(1).text();
        if (!fax.equals("")) {
            eczaneDetay.setFax(fax);
        }

        Element adresTarifTags = trTags.get(3);
        adresTarif = adresTarifTags.select("label").get(1).text();
        if (!adresTarif.equals("")) {
            eczaneDetay.setTarif(adresTarif);
        }

        return eczaneDetay;
    }
}
