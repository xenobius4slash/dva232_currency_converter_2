package se.mdh.dva232.dva232currencyconverter2;

import android.annotation.SuppressLint;
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
import android.text.format.DateFormat;
import android.util.JsonReader;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.zip.Inflater;


public class MainActivity extends FragmentActivity {
    private String xmlFilename = "currency_rates.xml";
    private Long lastUpdatedTimestamp;
    private Double currencyRatesAll[][] = new Double[7][7];
    private XmlSerializer xmlSerializer = Xml.newSerializer();
    public Double[][] getCurrencyRatesAll() {
        return currencyRatesAll;
    }
    public Boolean networkConnection = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("LIFECYCLE", "onCreate");

        // delete existing file
        File fileToDelete = getBaseContext().getFileStreamPath(xmlFilename);
        fileToDelete.delete();

        File file = getBaseContext().getFileStreamPath(xmlFilename);
        if( file.exists() ) {
            Log.d("DECISIONS", "XML file exists");
            readXmlFileAndSaveData();
            // with internet
            Log.d("DECISIONS","internet connection is available");
            if (isUpToDate()) {
                Log.d("DECISIONS", "data up-to-date => API call not necessary => Finished");
            } else {
                Log.d("DECISIONS", "data NOT up-to-date => start API call");
                if(isConnected()) {
                    networkConnection = true;
                    Log.d("DECISIONS","internet connection is available");
                    getCurrencyRates(); // ASYNC: receive data from API and save in public array
                    Log.d("DECISIONS", "Finished");
                } else {
                    networkConnection = false;
                    Log.d("DECISIONS", "internet connection is NOT available => Finished");
                }
            }
        } else {
            // Initialisation
            Log.d("DECISIONS", "XML file doesn't exists");
            if( !isConnected() ) {
                // without internet
                networkConnection = false;
                Log.d("DECISIONS", "internet connection is NOT available => use saved default data");
                getSavedCurrencyRates(); // ASYNC: receive data from saved data and save in public array
                Log.d("DECISIONS", "Finished");
            } else {
                networkConnection = true;
                // with internet
                Log.d("DECISIONS", "internet connection is available => start API call");
                getCurrencyRates(); // ASYNC: receive data from API and save in public array
                Log.d("DECISIONS", "Finished");
            }
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager pager = findViewById(R.id.viewPager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(pager, true);

        updateCurrencyRatesAutomatically();
        checkForNetworkConnection();

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

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        Log.d("SAVE","onSaveInstanceState");
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d("SAVE","onRestoreInstanceState");
        super.onRestoreInstanceState(savedInstanceState);
   }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        private MyPagerAdapter(FragmentManager fm) {
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

    public void setLatestUpdateTextView(Long timestamp) {
        Calendar lastUpdated = Calendar.getInstance();
        lastUpdated.setTimeInMillis(timestamp * 1000);
        Log.d("LASTUPDATED", "new last updated: " + DateFormat.format("dd-MM-yyyy HH:mm:ss", lastUpdated).toString());

        TextView tv = findViewById(R.id.latestUpdate);
        tv.setText( DateFormat.format("dd.MM.yyyy HH:mm", lastUpdated).toString() );

    }

    /**
     * Check for internet connection
     * @return  Boolean     true => yes, false => no
     */
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
     * If there are no internet connection and no values then the static values in this function will be used
     */
    public void getSavedCurrencyRates() {
        Log.d("GET_UPDATED_CURRENCY", "getSavedCurrencyRates()");
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Log.d("ASYNC", "start async process");
                lastUpdatedTimestamp = (long) 1544366952;
                currencyRatesAll[0][0] = 1.0;
                currencyRatesAll[0][1] = 10.2671;
                currencyRatesAll[0][2] = 1.14172;
                currencyRatesAll[0][3] = 0.88919;
                currencyRatesAll[0][4] = 7.91939;
                currencyRatesAll[0][5] = 128.76;
                currencyRatesAll[0][6] = 1277.97;
                calculateMissingCurrencyRates();
                getGeneratedXmlContent(lastUpdatedTimestamp);
                setLatestUpdateTextView(lastUpdatedTimestamp);
            }
        });
        Log.d("ASYNC", "end async process");
    }

    /**
     * Runs the update of the currenca rates each 60 minute
     */
    public void updateCurrencyRatesAutomatically() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            Log.d("TIMER","updateCurrencyRatesAutomatically()");
                            if(isConnected()) {
                                networkConnection = true;
                                getCurrencyRates();
                            } else {
                                networkConnection = false;
                            }
                        }
                    },3600000, 3600000      // 60 * 60 * 1000 = 60 Minuten
                );
            }
        });
    }


    public void checkForNetworkConnection() {
        Log.d("PERIOD", "checkForNetworkConnection()");
        if(!networkConnection) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                                Log.d("PERIOD", "checkForNetworkConnection() -> run()");
                                if(!networkConnection) {
                                    if (isConnected()) {
                                        Log.d("PERIOD", "checkForNetworkConnection() -> run() -> connected #2 -> WAIT");
                                        networkConnection = true;
                                        getCurrencyRates();
                                    } else {
                                        Log.d("PERIOD", "checkForNetworkConnection() -> run() -> NOT connected -> WAIT");
                                        networkConnection = false;
                                    }
                                } else {
                                    Log.d("PERIOD", "checkForNetworkConnection() -> run() -> connected #1 -> WAIT");
                                }
                            }
                        },20000, 20000      // 10 Sekunden
                    );
                }
            });
        }
    }

    /**
     * API call for receiving/calculating latest currency rates
     */
    public void getCurrencyRates() {
        Log.d("GET_UPDATED_CURRENCY", "updateCurrencyRates()");
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Log.d("ASYNC", "start async process");
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
                        json.nextName();
                        success = json.nextBoolean();
                        Log.d("JSON", "key: success -> value: " + success);
                        tempString = json.nextName();
                        Log.d("JSON", "key: " + tempString);
                        if (tempString == "error") {
                            Log.d("JSON", "ERROR" + json.nextInt());
                        } else {
                            lastUpdatedTimestamp = (long) json.nextInt();
                            Log.d("JSON", "key: timestamp -> value: " + lastUpdatedTimestamp);
                            json.nextName();
                            base = json.nextString();  // base
                            Log.d("JSON", "key: base -> value: " + base);
                            json.nextName();
                            date = json.nextString();  // date
                            Log.d("JSON", "key: date -> value: " + date);
                            tempString = json.nextName();
                            json.beginObject(); // rates
                            // new JSON object with the rates
                            tempString = json.nextName(); // EUR
                            currencyRatesAll[0][0] = json.nextDouble();
                            Log.d("JSON", "key: " + tempString + " -> value: " + currencyRatesAll[0][0]);
                            tempString = json.nextName(); // SEK
                            currencyRatesAll[0][1] = json.nextDouble();
                            Log.d("JSON", "key: " + tempString + " -> value: " + currencyRatesAll[0][1]);
                            tempString = json.nextName(); // USD
                            currencyRatesAll[0][2] = json.nextDouble();
                            Log.d("JSON", "key: " + tempString + " -> value: " + currencyRatesAll[0][2]);
                            tempString = json.nextName(); // GBP
                            currencyRatesAll[0][3] = json.nextDouble();
                            Log.d("JSON", "key: " + tempString + " -> value: " + currencyRatesAll[0][3]);
                            tempString = json.nextName(); // CNY
                            currencyRatesAll[0][4] = json.nextDouble();
                            Log.d("JSON", "key: " + tempString + " -> value: " + currencyRatesAll[0][4]);
                            tempString = json.nextName(); // JPY
                            currencyRatesAll[0][5] = json.nextDouble();
                            Log.d("JSON", "key: " + tempString + " -> value: " + currencyRatesAll[0][5]);
                            tempString = json.nextName(); // KRW
                            currencyRatesAll[0][6] = json.nextDouble();
                            Log.d("JSON", "key: " + tempString + " -> value: " + currencyRatesAll[0][6]);

                            calculateMissingCurrencyRates();
                            Log.d("UPDATE","currency rates calculated and saved");
                            String xmlContent = getGeneratedXmlContent(lastUpdatedTimestamp);
                            Log.d("UPDATE","XML document content created");
                            saveXmlFile(xmlContent);
                            Log.d("UPDATE","XML file saved");
                            setLatestUpdateTextView(lastUpdatedTimestamp);
                            networkConnection = true;
                        }
                    } catch (Exception ex) {
                        networkConnection = false;
                        Log.e("CON", ex.getMessage());
                    }
                } catch (MalformedURLException ex) {
                    networkConnection = false;
                    Log.e("URL", ex.getMessage());
                }
            }
        });
        Log.d("ASYNC", "end async process");
    }

    /**
     * calculated the missing currency rates
     */
    public void calculateMissingCurrencyRates() {
        Log.d("CALCULATE", "calculateMissingCurrencyRates");
        for (int row = 1; row < 7; row++) {
            for (int col = 0; col < 7; col++) {
                currencyRatesAll[row][col] = (1.0 / currencyRatesAll[0][row]) * currencyRatesAll[0][col];
            }
        }

        for (int row = 0; row < 7; row++) {
            String rowString = "";
            for (int col = 0; col < 7; col++) {
                rowString = rowString + currencyRatesAll[row][col] + ", ";
            }
            Log.d("CURRENCY-CALC", "row[" + row + "] => " + rowString);
        }
    }

    public static String formatDouble(double number) {
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
        otherSymbols.setDecimalSeparator('.');
        DecimalFormat twoDForm = new DecimalFormat("#.0#####", otherSymbols);
        return twoDForm.format(number);
    }

    /**
     * Create a XML file
     *
     * @throws IOException  error while generating XML file
     */
    public String getGeneratedXmlContent(Long timestamp) {

        String xml;
        try {
        StringWriter writer = new StringWriter();

            xmlSerializer.setOutput(writer);

            xmlSerializer.startDocument("UTF-8", true);              // start DOCUMENT

            xmlSerializer.startTag("", "currency_rates");               // start node "currency_rates"

            xmlSerializer.startTag("","timestamp");
            xmlSerializer.text(String.valueOf(timestamp));
            xmlSerializer.endTag("","timestamp");

            createFromStartNode("EUR");
            createToNode("EUR", formatDouble( currencyRatesAll[0][0] ));
            createToNode("SEK", formatDouble( currencyRatesAll[0][1] ));
            createToNode("USD", formatDouble( currencyRatesAll[0][2] ));
            createToNode("GBP", formatDouble( currencyRatesAll[0][3] ));
            createToNode("CNY", formatDouble( currencyRatesAll[0][4] ));
            createToNode("JPY", formatDouble( currencyRatesAll[0][5] ));
            createToNode("KRW", formatDouble( currencyRatesAll[0][6] ));
            createFomEndNode();

            createFromStartNode("SEK");
            createToNode("EUR", formatDouble( currencyRatesAll[1][0] ));
            createToNode("SEK", formatDouble( currencyRatesAll[1][1] ));
            createToNode("USD", formatDouble( currencyRatesAll[1][2] ));
            createToNode("GBP", formatDouble( currencyRatesAll[1][3] ));
            createToNode("CNY", formatDouble( currencyRatesAll[1][4] ));
            createToNode("JPY", formatDouble( currencyRatesAll[1][5] ));
            createToNode("KRW", formatDouble( currencyRatesAll[1][6] ));
            createFomEndNode();

            createFromStartNode("USD");
            createToNode("EUR", formatDouble( currencyRatesAll[2][0] ));
            createToNode("SEK", formatDouble( currencyRatesAll[2][1] ));
            createToNode("USD", formatDouble( currencyRatesAll[2][2] ));
            createToNode("GBP", formatDouble( currencyRatesAll[2][3] ));
            createToNode("CNY", formatDouble( currencyRatesAll[2][4] ));
            createToNode("JPY", formatDouble( currencyRatesAll[2][5] ));
            createToNode("KRW", formatDouble( currencyRatesAll[2][6] ));
            createFomEndNode();

            createFromStartNode("GBP");
            createToNode("EUR", formatDouble( currencyRatesAll[3][0] ));
            createToNode("SEK", formatDouble( currencyRatesAll[3][1] ));
            createToNode("USD", formatDouble( currencyRatesAll[3][2] ));
            createToNode("GBP", formatDouble( currencyRatesAll[3][3] ));
            createToNode("CNY", formatDouble( currencyRatesAll[3][4] ));
            createToNode("JPY", formatDouble( currencyRatesAll[3][5] ));
            createToNode("KRW", formatDouble( currencyRatesAll[3][6] ));
            createFomEndNode();

            createFromStartNode("CNY");
            createToNode("EUR", formatDouble( currencyRatesAll[4][0] ));
            createToNode("SEK", formatDouble( currencyRatesAll[4][1] ));
            createToNode("USD", formatDouble( currencyRatesAll[4][2] ));
            createToNode("GBP", formatDouble( currencyRatesAll[4][3] ));
            createToNode("CNY", formatDouble( currencyRatesAll[4][4] ));
            createToNode("JPY", formatDouble( currencyRatesAll[4][5] ));
            createToNode("KRW", formatDouble( currencyRatesAll[4][6] ));
            createFomEndNode();

            createFromStartNode("JPY");
            createToNode("EUR", formatDouble( currencyRatesAll[5][0] ));
            createToNode("SEK", formatDouble( currencyRatesAll[5][1] ));
            createToNode("USD", formatDouble( currencyRatesAll[5][2] ));
            createToNode("GBP", formatDouble( currencyRatesAll[5][3] ));
            createToNode("CNY", formatDouble( currencyRatesAll[5][4] ));
            createToNode("JPY", formatDouble( currencyRatesAll[5][5] ));
            createToNode("KRW", formatDouble( currencyRatesAll[5][6] ));
            createFomEndNode();

            createFromStartNode("KRW");
            createToNode("EUR", formatDouble( currencyRatesAll[6][0] ));
            createToNode("SEK", formatDouble( currencyRatesAll[6][1] ));
            createToNode("USD", formatDouble( currencyRatesAll[6][2] ));
            createToNode("GBP", formatDouble( currencyRatesAll[6][3] ));
            createToNode("CNY", formatDouble( currencyRatesAll[6][4] ));
            createToNode("JPY", formatDouble( currencyRatesAll[6][5] ));
            createToNode("KRW", formatDouble( currencyRatesAll[6][6] ));
            createFomEndNode();

            xmlSerializer.endDocument();

            xml = writer.toString();
            Log.d("XML", "write XML content: " + xml);

            return xml;
        } catch (IOException e) {
            e.printStackTrace();
            return e.toString();
        }
    }

    /**
     * create XML-Tag <from currency="...">
     * @param fromCurrency  String  currency shortened
     * @throws IOException  error while creating XML start tag "from"
     */
    public void createFromStartNode(String fromCurrency) throws IOException {
        xmlSerializer.startTag("", "from");                     // start node "from"
        xmlSerializer.attribute("", "currency", fromCurrency);  // add attribute "currency"
    }

    /**
     * create XML-Tag </from>
     * @throws IOException  error while creating XML end tag "from>
     */
    public void createFomEndNode() throws IOException {
        xmlSerializer.endTag("","from");
    }

    /**
     * create XML-Tag <to currency="...">...</to>
     * @param toCurrency    String      currency shortened
     * @param value         String      currency rate
     * @throws IOException  error while creating XML tag "to"
     */
    public void createToNode(String toCurrency, String value) throws IOException {
        xmlSerializer.startTag("","to");                        // start node "to"
        xmlSerializer.attribute("","currency", toCurrency);     // add attribute "currency"
        xmlSerializer.text(value);                                               // add value of the node
        xmlSerializer.endTag("","to");                          // close node "to"
    }

    /**
     * save XML file by given content
     * @param xmlContent    error while saving file
     */
    public void saveXmlFile(String xmlContent) {
        FileOutputStream xmlFile;
        try {
            xmlFile = openFileOutput(xmlFilename, Context.MODE_PRIVATE);
            xmlFile.write(xmlContent.getBytes());
            xmlFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * read saved XML file and save included data in public array and variable
     */
    public void readXmlFileAndSaveData() {
        String temp;
        Long tempLong;

        try {
            FileInputStream xmlFile = openFileInput(xmlFilename);
            InputStreamReader inputStreamReader = new InputStreamReader(xmlFile);
            char[] inputBuffer = new char[xmlFile.available()];
            inputStreamReader.read(inputBuffer);
            String inputData = new String(inputBuffer);
            inputStreamReader.close();
            xmlFile.close();
            Log.d("XML","read XML content: " + inputData);
            XmlPullParserFactory xmlFactory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlParser = xmlFactory.newPullParser();
            xmlParser.setInput( new StringReader(inputData));
            xmlParser.nextTag();
            xmlParser.nextTag();    // timestamp
            lastUpdatedTimestamp = Long.parseLong(xmlParser.nextText());
            setLatestUpdateTextView(lastUpdatedTimestamp);

            int row = 0;
            int col = 0;
            String tempFrom = null;
            String tempTo = null;
            while(xmlParser.next() != XmlPullParser.END_DOCUMENT) {
                // from TAGS
                if( xmlParser.getName().equals("from") ) {
                    if(xmlParser.getEventType() == XmlPullParser.START_TAG) {
                        Log.d("XML", "from START_TAG => col=0");
                        col = 0;
                        tempFrom = xmlParser.getAttributeValue("","currency");
                    } else if(xmlParser.getEventType() == XmlPullParser.END_TAG) {
                        Log.d("XML", "from START_TAG => row++");
                        row++;
                    }
                }
                // to TAGS
                else if( xmlParser.getName().equals("to") ) {
                    tempTo = xmlParser.getAttributeValue("","currency");
                    currencyRatesAll[row][col] = Double.parseDouble(xmlParser.nextText());
                    Log.d("XML","from "+tempFrom+" to "+tempTo+" => ["+row+"]["+col+"] => " + currencyRatesAll[row][col]);
                    col++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

    }

    /**
     * Check for updateable data
     * @return      Boolean     true => up-to-date, false => not up-to-date
     */
    public Boolean isUpToDate() {
        int expiredMinutes = 60;
        Calendar now = Calendar.getInstance();
        Log.d("TIMESTAMP", "now: " + DateFormat.format("dd-MM-yyyy HH:mm:ss", now).toString() );

        Calendar lastUpdated = Calendar.getInstance();
        lastUpdated.setTimeInMillis(lastUpdatedTimestamp * 1000);
        Log.d("TIMESTAMP", "last updated: " + DateFormat.format("dd-MM-yyyy HH:mm:ss", lastUpdated).toString());

        long diffMinutes = ( now.getTimeInMillis() - lastUpdated.getTimeInMillis() ) / ( 1000 * 60 );
        Log.d("TIMESTAMP", "difference: " + diffMinutes + " minutes");

        if( diffMinutes > expiredMinutes ) {
            Log.d("TIMESTAMP", "update data");
            return false;
        } else {
            Log.d("TIMESTAMP", "data actual -> no update");
            return true;
        }
    }

}