package com.tommilaurila.tie13karttademo;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends ActionBarActivity
    implements LisaaRyhmaDialogFragment.LisaaRyhmaDialogListener,
        LisaaKayttajaDialogFragment.LisaaKayttajaDialogListener {

    // taulukko, joka sisältää ryhmäolioita
    public ArrayList<Ryhma> ryhmat = new ArrayList<>();
    ListView lvRyhmaLista;
    RyhmalistaAdapter arrayAdapter;
    String kayttaja;
    String ryhmaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // haetaan ryhmät www-palvelimelta asynctaskin avulla
        haeRyhmatTask task = new haeRyhmatTask();
        task.execute(new String[]{"http://172.19.129.105/r2/ryhma/haeryhmat"});

        // etsitään listview-komponentti layoutista
        lvRyhmaLista = (ListView)findViewById(R.id.lvRyhmaLista);

        // luodaan sovitin ryhmätaulukon ja listviewin välille
        arrayAdapter = new RyhmalistaAdapter(this,R.layout.listarivi_ryhmat, ryhmat);

        // liitetään luotu sovitin listviewin kanssa yhteen
        lvRyhmaLista.setAdapter(arrayAdapter);

        lvRyhmaLista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                // Toast.makeText(getBaseContext(), item, Toast.LENGTH_LONG).show();
                // avataan kartta-aktiviteetti
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);

                // lähetetään käyttäjänimi sekä valittu ryhmä kartta-aktiviteetille

                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                //oletus-id on -1, joka tarkoittaa, että käyttäjä-id:tä ei voitu lukea
                String k_id = sharedPref.getString("kayttajaid", "-1");

                // TODO: muuta kovakoodatut nimet
                intent.putExtra("xtraKayttajaId", k_id);
                intent.putExtra("xtraKayttajaNimi", kayttaja);
                // lisätään tyhjät lainaukset loppuun -> string-muoto
                intent.putExtra("xtraRyhmaId", ryhmat.get(position).getRyhma_id() + "");
                intent.putExtra("xtraRyhmaNimi", ryhmat.get(position).getNimi());

                startActivity(intent);
            }
        });

        // yritetään lukea käyttäjätunnus puhelimen muistista
        // jos sitä ei löydy, näytetään rekisteröintidialogi
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        kayttaja = sharedPref.getString("kayttajatunnus", "");
        Log.d("oma", "luettiin muistista käyttäjä " + kayttaja);
        setTitle(getString(R.string.title_activity_main) + " (" + kayttaja + ")");

        // jos puhelimen muistissa ei ole käyttäjätunnusta, näytetään rekisteröintidialogi
        if(kayttaja.length() < 1) {
            DialogFragment kayttajaFragment = new LisaaKayttajaDialogFragment();
            kayttajaFragment.show(getSupportFragmentManager(), "lisaakayttaja");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if(id == R.id.action_rekisterointi) {
            // tässä käynnistetään rekisteröintitoiminto
            DialogFragment kayttajaFragment = new LisaaKayttajaDialogFragment();
            kayttajaFragment.show(getSupportFragmentManager(), "lisaakayttaja");
            return true;
        }
        // kun painetaan lisää ryhmä -kuvaketta action barissa
        else if(id == R.id.action_add_group) {
            DialogFragment newFragment = new LisaaRyhmaDialogFragment();
            newFragment.show(getSupportFragmentManager(), "lisaaryhma");
            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // otetaan vastaan dialogi (joka lähetti itsensä tänne)
        // ja luetaan dialogin syöttökenttien sisältö
        Dialog dialogView = dialog.getDialog();
        EditText etRyhmaNimi = (EditText)dialogView.findViewById(R.id.ryhmanimi);
        EditText etRyhmaSalasana = (EditText)dialogView.findViewById(R.id.ryhmasalasana);

        // luetaan oma käyttäjä-id puhelimen muistista
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        String kayttajaid = sharedPref.getString("kayttajaid", "");

        // lähetetään käyttäjän kirjoittaman ryhmän nimi ja salasana serverille
        new lisaaRyhmaTask().execute(kayttajaid,
                etRyhmaNimi.getText().toString(),
                etRyhmaSalasana.getText().toString());

        // lisätään syötetty ryhmä ryhmat-taulukkoon
        // TODO: muuta tämä niin, että se toimii ryhmäolioiden avulla
        // ryhmat.add(etRyhmaNimi.getText().toString());
        // arrayAdapter.notifyDataSetChanged();

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button
    }


    @Override
    public void onKayttajaPositiveClick(DialogFragment dialog) {
        // otetaan vastaan dialogi (joka lähetti itsensä tänne)
        // ja luetaan dialogin syöttökenttien sisältö
        Dialog dialogView = dialog.getDialog();
        EditText etKayttajaNimi = (EditText)dialogView.findViewById(R.id.kayttajanimi);
        EditText etKayttajaSalasana = (EditText)dialogView.findViewById(R.id.kayttajasalasana);

        // lisätään uusi käyttäjä (Rekisteröinti)
        Log.d("oma", "Yritit rekisteröityä tunnuksella: " + etKayttajaNimi.getText().toString());
        Log.d("oma", "Yritit rekisteröityä salasanalla: " + etKayttajaSalasana.getText().toString());

        // lähetetään käyttäjätunnukset palvelimelle asynctaskin avulla
        new rekisteroidyTask().execute(etKayttajaNimi.getText().toString(),
                etKayttajaSalasana.getText().toString());

        // TODO: nämä ovat väärässä paikassa, nyt tiedot tallentuvat puhelimeen
        // ennen tarkistusta!

        // tallennetaan käyttäjätunnus ja salasana puhelimen muistiin
        // TODO: muuta kovakoodatut avainnimet
        // TODO: lähetä käyttäjätiedot palvelimelle
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("kayttajatunnus", etKayttajaNimi.getText().toString());
        editor.putString("kayttajasalasana", etKayttajaSalasana.getText().toString());
        editor.apply();
    }

    @Override
    public void onKayttajaNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button
    }


    //sisäluokka, joka hoitaa yhteydenottoja serverin suuntaan
    // AsyncTask<params, progress, result>
    private class rekisteroidyTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... tunnukset) {
            Log.d("oma", "annoit tunnuksen: " + tunnukset[0]);
            Log.d("oma", "annoit salasanan: " + tunnukset[1]);

            // TODO: mitä jos postData palauttaa null?
            String k_id = postData(tunnukset[0], tunnukset[1]);

            return k_id;
        }// DoInBackground

        @Override
        protected void onPostExecute(String result) {
            // jos result on null, käyttäjä-id:tä ei palautunut serveriltä
            if(result != null && result.length() > 0) {
                // tallennetaan käyttäjä-id puhelimen muistiin
                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("kayttajaid", result);
                editor.apply();

                kayttaja = sharedPref.getString("kayttajatunnus", "");
                setTitle(getString(R.string.title_activity_main) + " (" + kayttaja + ")");
            }// if
            else {
                // TODO: muuta valikkoteksti strings-tiedostoon
                Toast.makeText(getApplicationContext(),
                        "Käyttäjä-id:n haku epäonnistui",
                        Toast.LENGTH_LONG).show();
            }// else

        }// onPostExec


        private String postData(String kt, String ss) {

            HashMap<String, String> arvoParit = new HashMap<>();

            /*Sijoitetaan lähetettävät tiedot hashmappiin*/
            arvoParit.put("kt", kt);
            arvoParit.put("ss", ss);

            /*Lähetetään hashmap url osoitteeseen*/
            performPostCall("http://172.19.129.105/r2/kayttaja/lisaa", arvoParit);

            return null;
        }// postData

        public String  performPostCall(String requestURL,
                                       HashMap<String, String> postDataParams) {

            URL url;
            String response = "";
            try {
                url = new URL(requestURL);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);


                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();
                int responseCode=conn.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line=br.readLine()) != null) {
                        response+=line;
                    }
                }
                else {
                    response="";

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return response;
        }

        private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException{
            StringBuilder result = new StringBuilder();
            boolean first = true;
            for(Map.Entry<String, String> entry : params.entrySet()){
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }

            return result.toString();
        }//getPostDataString

    }//rek.task


    //sisäluokka, joka hoitaa yhteydenottoja serverin suuntaan
    // AsyncTask<params, progress, result>
    private class lisaaRyhmaTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... tunnukset) {

            // TODO: mitä jos postData palauttaa null?
            // tunnukset[0]=ryhmän luojan käyttäjä-id
            // tunnukset[1]=ryhmän nimi
            // tunnukset[2]=ryhmän salasana
            String r_id = postData(tunnukset[0], tunnukset[1], tunnukset[2]);

            return r_id;
        }// DoInBackground

        @Override
        protected void onPostExecute(String result) {
            // jos result on null, käyttäjä-id:tä ei palautunut serveriltä
            if (result != null && result.length() > 0) {
                // ryhmän lisäys onnistui
                Log.d("oma", "serveriltä palautui ryhmä-id: " + result);
                ryhmaId = result;
            }// if
            else {
                // TODO: muuta valikkoteksti strings-tiedostoon
                Toast.makeText(getApplicationContext(),
                        "Ryhmän lisäys epäonnistui",
                        Toast.LENGTH_LONG).show();
            }// else
        }// onPostExec


        private String postData(String rl, String rn, String rs) {
            // Create a new HttpClient and Post Header
            HashMap<String, String> arvoParit = new HashMap<>();

            /*Sijoitetaan lähetettävät tiedot hashmappiin*/
            arvoParit.put("rl", rl);
            arvoParit.put("rn", rn);
            arvoParit.put("rs", rs);

            /*Lähetetään hashmap url osoitteeseen*/
            ApuHttp lisaaRyhma = new ApuHttp();
            lisaaRyhma.postData("http://172.19.129.105/r2/ryhma/lisaa", arvoParit);

            return null;
        }// postData

        public String performPostCall(String requestURL,
                                      HashMap<String, String> postDataParams) {

            URL url;
            String response = "";
            try {
                url = new URL(requestURL);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);


                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();
                int responseCode = conn.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                } else {
                    response = "";

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return response;
        }

        private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
            StringBuilder result = new StringBuilder();
            boolean first = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }

            return result.toString();
        }//getPostDataString

    }//rek.task


    //sisäluokka, joka hakee serveriltä kaikki ryhmät
    // AsyncTask<params, progress, result>
    private class haeRyhmatTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            HttpURLConnection uc = null;
            String palautus = "";
            try{
                URL url = new URL(urls[0]);
                uc = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(uc.getInputStream());
                palautus = readStream(in);
            } catch (Exception e){
                Log.d("oma", "Tuli virhe: " + e);
            } finally {
                if (uc != null) uc.disconnect();
            }

            return palautus;
        }// DoInBackground

        @Override
        protected void onPostExecute(String result) {
            // täällä ryhmien tiedot parsitaan ulos saapuneesta JSON-objektista
            // ja sijoitetaan ryhmat-listaan
            Log.d("oma", "saapui ryhmälista: " + result);

            //yritetään parsia ryhmät ulos JSON-objektista
            // serveriltä saapuu JSON-array [ {...} ]
            try {
                JSONArray ryhmalista = new JSONArray(result);
                Log.d("oma", "JSONArrayn pituus on " + ryhmalista.length());

                // käydään JSONtaulukko läpi ja luodaan ryhma-olio, jokaisesta
                // JSON-objektista
                for(int i=0; i<ryhmalista.length(); i++) {
                    Ryhma r = new Ryhma();

                    // haetaan JSONarraysta i:nnes objekti
                    JSONObject job = ryhmalista.getJSONObject(i);

                    // täytetään ryhmäolion kentät JSONobjektista
                    r.setRyhma_id(job.getInt("ryhma_id"));
                    r.setLuoja(job.getInt("luoja"));
                    r.setNimi(job.getString("nimi"));
                    r.setSalasana(job.getString("salasana"));
                    r.setPerustamisaika(job.getString("perustamisaika"));

                    // lisätään luotu ryhmäolio aktiviteetin ryhmat-taulukkoon
                    ryhmat.add(r);
                }//for
            }//try
            catch (Exception e) {
                Log.d("oma", "tuli virhe "+ e.toString());
            }
        }// onPostExec

        private String readStream(InputStream is){
            try{
                ByteArrayOutputStream bo = new ByteArrayOutputStream();
                int i = is.read();
                while(i != -1){
                    bo.write(i);
                    i = is.read();
                }
                return bo.toString();
            } catch (IOException e) {
                return "";
            }
        }

    }//haeRyhmat.task

}// ****************  mainactivity   ***************************
