package se.mdh.dva232.dva232currencyconverter2;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.util.JsonReader;
import android.util.Log;
import android.util.Xml;
import android.view.View;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends FragmentActivity {

    private XmlSerializer xmlSerializer = Xml.newSerializer();
    //private Double currencyRates[] = new Double[7];
    private Double currencyRates[] = {0.0, 10.2671, 1.14172, 0.08642, 7.91939, 128.76, 1277.97};    // EUR, SEK, USD, GBP, CNY, JPY, KRW

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("LIFECYCLE", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager pager = findViewById(R.id.viewPager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(pager, true);

        try {
            getGeneratedXmlContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        Log.d("LIFECYCLE", "onStart");
        super.onStart();
   }

    @Override
    protected void onResume() {
        Log.d("LIFECYCLE", "onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d("LIFECYCLE", "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d("LIFECYCLE", "onStop");
        super.onStop();
     }

    @Override
    protected void onDestroy() {
        Log.d("LIFECYCLE", "onDestroy");
        super.onDestroy();
   }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch(pos) {
                case 0: return CurrencyConverterFragment.newInstance();
                case 1: return ConversionRatesFragment.newInstance();
                default: return CurrencyConverterFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }


    public void updateCurrencyRates() {
        Log.d("UPDATECURR", "updateCurrencyRates()");

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if( isConnected() ){
                    try {
                        URL updateUrl = new URL("http://data.fixer.io/api/latest?access_key=fb6981cfe26b466276af83e03afeee15&symbols=EUR,SEK,USD,GBP,CNY,JPY,KRW");
                        try {
                            String tempString;
                            Boolean success;
                            Integer timestamp;
                            String base;
                            String date;
                            HttpURLConnection con = (HttpURLConnection) updateUrl.openConnection();
                            InputStream responseBody = con.getInputStream();
                            InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
                            JsonReader json = new JsonReader(responseBodyReader);
                            json.beginObject();
                            json.nextName(); success = json.nextBoolean();
                            Log.d("JSON", "key: success -> value: " + success );
                            tempString = json.nextName();
                            Log.d("JSON", "key: " + tempString );
                            if(tempString == "error"){
                                Log.d("JSON", "ERROR" + json.nextInt());
                            } else {
                                timestamp = json.nextInt();
                                Log.d("JSON", "key: timestamp -> value: " + timestamp );
                                json.nextName(); base = json.nextString();  // base
                                Log.d("JSON", "key: base -> value: " + base );
                                json.nextName(); date = json.nextString();  // date
                                Log.d("JSON", "key: date -> value: " + date );
                                tempString = json.nextName(); json.beginObject(); // rates
                                // new JSON object with the rates
                                tempString = json.nextName(); // EUR
                                currencyRates[0] = json.nextDouble();
                                Log.d("JSON", "key: " + tempString + " -> value: " + currencyRates[0] );
                                tempString = json.nextName(); // SEK
                                currencyRates[1] = json.nextDouble();
                                Log.d("JSON", "key: " + tempString + " -> value: " + currencyRates[1] );
                                tempString = json.nextName(); // USD
                                currencyRates[2] = json.nextDouble();
                                Log.d("JSON", "key: " + tempString + " -> value: " + currencyRates[2] );
                                tempString = json.nextName(); // GBP
                                currencyRates[3] = json.nextDouble();
                                Log.d("JSON", "key: " + tempString + " -> value: " + currencyRates[3] );
                                tempString = json.nextName(); // CNY
                                currencyRates[4] = json.nextDouble();
                                Log.d("JSON", "key: " + tempString + " -> value: " + currencyRates[4] );
                                tempString = json.nextName(); // JPY
                                currencyRates[5] = json.nextDouble();
                                Log.d("JSON", "key: " + tempString + " -> value: " + currencyRates[5] );
                                tempString = json.nextName(); // KRW
                                currencyRates[6] = json.nextDouble();
                                Log.d("JSON", "key: " + tempString + " -> value: " + currencyRates[6] );
                            }
                        }catch(Exception ex){
                            Log.e("CON", ex.getMessage());
                        }
                    }
                    catch (MalformedURLException ex){
                        Log.e("URL", ex.getMessage());
                        //Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.d("CON", "no connection detected");
                    //Toast.makeText(getContext(), "no connection detected", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public Boolean isConnected() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }

    /**
     * Create a XML file
     *
     * @throws IOException
     */
    public void getGeneratedXmlContent() throws IOException {
        String xml;

        StringWriter writer = new StringWriter();
        xmlSerializer.setOutput(writer);
        xmlSerializer.startDocument("UTF-8", true);              // start DOCUMENT

        xmlSerializer.startTag("", "currency_rates");               // start node "currency_rates"

        createFromStartNode("EUR");
        createToNode("EUR", "0");                         // 0.0
        createToNode("SEK", String.valueOf(currencyRates[1]));  // 10.2671
        createToNode("USD", String.valueOf(currencyRates[2]));  // 1.14172
        createToNode("GBP", String.valueOf(currencyRates[3]));  // 0.08642
        createToNode("CNY", String.valueOf(currencyRates[4]));  // 7.91939
        createToNode("JPY", String.valueOf(currencyRates[5]));  // 128.76
        createToNode("KRW", String.valueOf(currencyRates[6]));  // 1277.97
        createFomEndNode();


        createFromStartNode("SEK");
        createToNode("EUR", String.valueOf( 1.0 / currencyRates[1] ));
        createToNode("SEK", "0");
        createToNode("USD", String.valueOf( (1.0 / currencyRates[1]) * currencyRates[2] ));  // 0.11094
        createToNode("GBP", String.valueOf( (1.0 / currencyRates[1]) * currencyRates[3] ));  // 0.8642  TODO
        createToNode("CNY", String.valueOf( (1.0 / currencyRates[1]) * currencyRates[4] ));  // 0.76952
        createToNode("JPY", String.valueOf( (1.0 / currencyRates[1]) * currencyRates[5] ));  // 12.51
        createToNode("KRW", String.valueOf( (1.0 / currencyRates[1]) * currencyRates[6] ));  // 124.179
        createFomEndNode();

        createFromStartNode("USD");
        createToNode("EUR", String.valueOf( (1.0 / currencyRates[2]) * currencyRates[0] )); // TODO
        createToNode("SEK", String.valueOf( (1.0 / currencyRates[2]) * currencyRates[1] ));
        createToNode("USD", "0");
        createToNode("GBP", String.valueOf( (1.0 / currencyRates[2]) * currencyRates[3] ));
        createToNode("CNY", String.valueOf( (1.0 / currencyRates[2]) * currencyRates[4] ));
        createToNode("JPY", String.valueOf( (1.0 / currencyRates[2]) * currencyRates[5] ));
        createToNode("KRW", String.valueOf( (1.0 / currencyRates[2]) * currencyRates[6] ));
        createFomEndNode();

        createFromStartNode("GBP");
        createToNode("EUR", String.valueOf( (1.0 / currencyRates[3]) * currencyRates[0] )); // TODO
        createToNode("SEK", String.valueOf( (1.0 / currencyRates[3]) * currencyRates[1] ));
        createToNode("USD", String.valueOf( (1.0 / currencyRates[3]) * currencyRates[2] ));
        createToNode("GBP", "0");
        createToNode("CNY", String.valueOf( (1.0 / currencyRates[3]) * currencyRates[4] ));
        createToNode("JPY", String.valueOf( (1.0 / currencyRates[3]) * currencyRates[5] ));
        createToNode("KRW", String.valueOf( (1.0 / currencyRates[3]) * currencyRates[6] ));
        createFomEndNode();

        createFromStartNode("CNY");
        createToNode("EUR", String.valueOf( (1.0 / currencyRates[4]) * currencyRates[0] ));
        createToNode("SEK", String.valueOf( (1.0 / currencyRates[4]) * currencyRates[1] ));
        createToNode("USD", String.valueOf( (1.0 / currencyRates[4]) * currencyRates[2] ));
        createToNode("GBP", String.valueOf( (1.0 / currencyRates[4]) * currencyRates[3] ));
        createToNode("CNY", "0");
        createToNode("JPY", String.valueOf( (1.0 / currencyRates[4]) * currencyRates[5] ));
        createToNode("KRW", String.valueOf( (1.0 / currencyRates[4]) * currencyRates[6] ));
        createFomEndNode();

        createFromStartNode("JPY");
        createToNode("EUR", String.valueOf( (1.0 / currencyRates[5]) * currencyRates[0] ));
        createToNode("SEK", String.valueOf( (1.0 / currencyRates[5]) * currencyRates[1] ));
        createToNode("USD", String.valueOf( (1.0 / currencyRates[5]) * currencyRates[2] ));
        createToNode("GBP", String.valueOf( (1.0 / currencyRates[5]) * currencyRates[3] ));
        createToNode("CNY", String.valueOf( (1.0 / currencyRates[5]) * currencyRates[4] ));
        createToNode("JPY", "0");
        createToNode("KRW", String.valueOf( (1.0 / currencyRates[5]) * currencyRates[6] ));
        createFomEndNode();

        createFromStartNode("KRW");
        createToNode("EUR", String.valueOf( (1.0 / currencyRates[6]) * currencyRates[0] ));
        createToNode("SEK", String.valueOf( (1.0 / currencyRates[6]) * currencyRates[1] ));
        createToNode("USD", String.valueOf( (1.0 / currencyRates[6]) * currencyRates[2] ));
        createToNode("GBP", String.valueOf( (1.0 / currencyRates[6]) * currencyRates[3] ));
        createToNode("CNY", String.valueOf( (1.0 / currencyRates[6]) * currencyRates[4] ));
        createToNode("JPY", String.valueOf( (1.0 / currencyRates[6]) * currencyRates[5] ));
        createToNode("KRW", "0");
        createFomEndNode();

        xmlSerializer.endDocument();

        xml = writer.toString();
        Log.d("XML", "content: " + xml);
    }

    public void createFromStartNode(String fromCurrency) throws IOException {
        xmlSerializer.startTag("", "from");                     // start node "from"
        xmlSerializer.attribute("", "currency", fromCurrency);  // add attribute "currency"
    }

    public void createFomEndNode() throws IOException {
        xmlSerializer.endTag("","from");
    }

    public void createToNode(String toCurrency, String value) throws IOException {
        xmlSerializer.startTag("","to");                        // start node "to"
        xmlSerializer.attribute("","currency", toCurrency);     // add attribute "currency"
        xmlSerializer.text(value);                                               // add value of the node
        xmlSerializer.endTag("","to");                          // close node "to"
    }

}