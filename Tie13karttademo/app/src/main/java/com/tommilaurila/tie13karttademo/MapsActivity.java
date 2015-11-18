package com.tommilaurila.tie13karttademo;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
//import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends FragmentActivity
    implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private Marker omaSijainti;
    private Timer lahetysAjastin = new Timer();
    private String kayttajaNimi;
    private String kayttajaId;
    private String ryhmaNimi;
    private String ryhmaId;
    private String chatViesti = "testi";
    private ArrayList<Kayttaja> kayttajat = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        buildGoogleApiClient();
        createLocationRequest();

        // luetaan mainactivitystä lähetetyt käyttäjä- ja ryhmänimet
        Intent intent = getIntent();

        // TODO: muuta niin, että tänne saapuu ryhmä-id nimien lisäksi
        kayttajaId = intent.getStringExtra("xtraKayttajaId");
        kayttajaNimi = intent.getStringExtra("xtraKayttajaNimi");
        ryhmaId = intent.getStringExtra("xtraRyhmaId");
        ryhmaNimi = intent.getStringExtra("xtraRyhmaNimi");

        // otetaan talteen tämä hetki aikaleimana
        Date nyt = new Date();

        Kayttaja mina = new Kayttaja();
        mina.setNimimerkki(kayttajaNimi);
        mina.setViimeksi_nahty(new Timestamp(nyt.getTime()).getTime());

        // luodaan uusi sijaintiolio, johon koordinaatit sijoitetaan
        // TODO: pyydetään oma alkusijainti laitteen paikannuspalvelulta
        Sijainti olenTassa = new Sijainti(63.731818, 25.333794);

        // asetetaan sijaintiolio mina-olion ominaisuudeksi
        mina.setSijainti(olenTassa);

        // luodaan markkeri, joka sijoitetaan mina-olioon
        if(mMap != null) {
            Marker m = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(olenTassa.getLat(), olenTassa.getLng()))
                    .title(mina.getNimimerkki())
                    .snippet("testi"));

            // asetetaan markkeri m mina-olioon
            mina.setMerkki(m);
        }//if

        // minä itse olen ensimmäinen taulukossa, eli sijoitan itseni
        // paikkaan nolla (0)
        kayttajat.add(0, mina);


//        omaSijainti = mMap.addMarker(new MarkerOptions()
//                .position(new LatLng(63.731818,25.333794))
//                .title(kayttajaNimi)
//                .snippet("Lisätietoja"));

        // luodaan ajastin, joka lähettää omaa sijaintia serverille säännöllisesti
        lahetysAjastin.schedule(new TimerTask(){
            @Override
            public void run() {
                if(mLastLocation != null) {
                    // do your thing here, such as execute AsyncTask or send data to server
                    LatLng munPaikka = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    // oma käyttäjänimi, oma ryhmänimi, oma sijainti, chat-viesti
                    lahetaOmaSijainti(kayttajaId, ryhmaId, munPaikka, chatViesti);
                }//if
            }//run
        }, 15000, 30000); // starts your code after 15000 milliseconds, then repeat it every 30 sec. (30000 milliseconds)
    }// onCreate


    // metodi, joka käy kayttajat-taulukon läpi, ja lisää kunkin käyttäjän
    // sijainnin markkerina kartalle
    private void lisaaKayttajatKartalle() {

    }


    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }


    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    protected void createLocationRequest() {
        Log.d("oma", "create location request");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000); // 10 sek.
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        Log.d("oma", "start location updates");
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        TileProvider wmsTileProvider = TileProviderFactory.getOsgeoWmsTileProvider();
        mMap.addTileOverlay(new TileOverlayOptions().tileProvider(wmsTileProvider));

        LatLng sijainti = new LatLng(63.756853, 25.322207);
        //mMap.addMarker(new MarkerOptions().position(sijainti).title("Minä"));
        //mMap.addMarker(new MarkerOptions().position(new LatLng(63.757080, 25.321990)).title("Sinä"));
        Log.d("oma", "max zoom " + mMap.getMaxZoomLevel());

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sijainti, 4.0f));
    }

    public void onHae(View v) {
        // mMap.addMarker(new MarkerOptions().position(new LatLng(63.736948,25.331698)).title("Haettu"));

        // tarkistetaan ensin, onko meillä verkkoyhteyttä ennen kun yritetään
        // ladata tietoa netistä
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
            // new DownloadWebpageTask().execute("http://172.19.128.105/karttachat/paikka.txt");
            new IlmoitaSijaintiTask().execute("http://172.19.129.105/r1/vastaus.php");
        } else {
            // display error
            Toast.makeText(this, "Ei verkkoyhteyttä", Toast.LENGTH_LONG).show();
        }
    }//onHae

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("oma", "Ollaan onConnected!");
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("oma", "paikka vaihtui");
        mLastLocation = location;
        updateUI();
    }


    private void updateUI() {
        Log.d("oma", "Lat on: " + String.valueOf(mLastLocation.getLatitude()));
        Log.d("oma", "Long on: " + String.valueOf(mLastLocation.getLongitude()));

        // TODO: tässä päivitetään kayttajat-taulukossa olevien käyttäjien sijainnit
        //omaSijainti.setPosition(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()));
        // mMap.addMarker(new MarkerOptions().position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())).title("Olen tässä"));

        // mLastUpdateTimeTextView.setText(mLastUpdateTime);
    }

    // UC7: oman sijainnin lähetys palvelimelle
    private void lahetaOmaSijainti(String kayttajaNimi, String ryhmaNimi,
                                   LatLng sijainti, String chatViesti) {
        // tässä lähetetään kaikki tiedot: oma käyttäjänimi, nykyinen ryhmänimi,
        // oma sijainti ja mahdollinen chat-viesti www-palvelimelle
        // haetaan ryhmät www-palvelimelta asynctaskin avulla
        IlmoitaSijaintiTask ist = new IlmoitaSijaintiTask();
        ist.execute(new String[]{"http://172.19.129.105/r0/sijainti/ilmoita",
                                kayttajaNimi,
                                ryhmaNimi,
                                sijainti.latitude + "",
                                sijainti.longitude + "",
                                chatViesti});

        //Log.d("oma", "Käyttäjänimi: " + kayttajaNimi);
        //Log.d("oma", "Ryhmänimi: " + ryhmaNimi);
        //Log.d("oma", "Oma lat: " + sijainti.latitude);
        //Log.d("oma", "Oma lng: " + sijainti.longitude);
        //Log.d("oma", "Chat-viesti: " + chatViesti);
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    private class IlmoitaSijaintiTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String result = "";

            // "http://172.19.129.105/r0/sijainti/ilmoita"
            // parametrit: url, uid, gid, lat, lng, msg
            result = postData(urls[0], urls[1], urls[2], urls[3], urls[4], urls[5]);

            return result;
        }// doInBackground

        @Override
        protected void onPostExecute(String result) {
            Log.d("oma", "Serveri vastasi (sijaintiupdate): " + result);
        }


        private String postData(String osoite, String uid, String gid, String lat, String lng, String msg) {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(osoite);

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
                nameValuePairs.add(new BasicNameValuePair("uid", uid));
                nameValuePairs.add(new BasicNameValuePair("gid", gid));
                nameValuePairs.add(new BasicNameValuePair("lat", lat));
                nameValuePairs.add(new BasicNameValuePair("lng", lng));
                nameValuePairs.add(new BasicNameValuePair("msg", msg));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);

                // According to the JAVA API, InputStream constructor do nothing.
                //So we can't initialize InputStream although it is not an interface
                InputStream inputStream = response.getEntity().getContent();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder stringBuilder = new StringBuilder();

                String bufferedStrChunk = null;

                while((bufferedStrChunk = bufferedReader.readLine()) != null){
                    stringBuilder.append(bufferedStrChunk);
                }

                Log.d("oma", "Palautus on: " + stringBuilder.toString());

                // palautetaan serveriltä saatu käyttäjä-id
                return stringBuilder.toString();

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }

            return null;
        }// postData
    }//IlmoitaSijaintiTask


    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            String[] koordinaatit = result.split(",");
            LatLng haettuPaikka = new LatLng(Double.parseDouble(koordinaatit[0]), Double.parseDouble(koordinaatit[1]));
            mMap.addMarker(new MarkerOptions().position(haettuPaikka).title("JES!"));
            Log.d("oma", "tulos on " + result);
        }//onPostExec


        private String downloadUrl(String myurl) throws IOException {
            InputStream is = null;
            // Only display the first 500 characters of the retrieved
            // web page content.
            int len = 19;

            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                //Log.d(DEBUG_TAG, "The response is: " + response);
                is = conn.getInputStream();

                // Convert the InputStream into a string
                String contentAsString = readIt(is, len);
                return contentAsString;

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } finally {
                if (is != null) {
                    is.close();
                }
            }//finally
        }//dlUrl


        public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[len];
            reader.read(buffer);
            return new String(buffer);
        }

    }//dlWebTask


}// class mapsactivity
