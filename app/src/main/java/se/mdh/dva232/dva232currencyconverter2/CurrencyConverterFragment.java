package se.mdh.dva232.dva232currencyconverter2;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;


public class CurrencyConverterFragment extends Fragment{

    public Integer integerCurrency1 = null;
    public Integer integerCurrency2 = null;
    public Boolean savedInstance = false;

    public CurrencyConverterFragment() {
        // Required empty public constructor
    }

    public static CurrencyConverterFragment newInstance() {
        Log.d("FRAGMENT 0", "CurrencyConverterFragment newInstance()");
        CurrencyConverterFragment fragment = new CurrencyConverterFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("LIFECYCLE F0", "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        Log.d("FRAGMENT 0", "onCreateView(...)");
        // Inflate the layout for this fragment
        Log.d("COUNTRY","country: " + getUserCountry(getContext()) );
        final View rootView = inflater.inflate(R.layout.fragment_currency_converter, container, false);

        /*
         * resource (array)
         */
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.currencies_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        /*
         * top spinner
         */
        Spinner spinner1 = rootView.findViewById(R.id.currencies_spinner1);
        spinner1.setAdapter(adapter);
        spinner1.setSelection( getSpinnerPositionByUserCountry( getUserCountry(getContext()) ) );   // init value
        spinner1.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        integerCurrency1 = pos;
                        setTextForTextView(rootView);
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) { }
                }
        );

        /*
         * top editText
         */
        final EditText editText1 = rootView.findViewById(R.id.input_value);

        editText1.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if( editText1.hasFocus() ) {
                    String resultText;
                    resultText = getTextForEditText(s.toString());
                    TextView textView = rootView.findViewById(R.id.result);
                    textView.setText(resultText);
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
        });

        /*
         * bottom spinner
         */
        Integer posSpiner2;
        Spinner spinner2 = rootView.findViewById(R.id.currencies_spinner2);
        spinner2.setAdapter(adapter);
        if( getSpinnerPositionByUserCountry( getUserCountry(getContext()) ) == 1) {
            spinner2.setSelection(0);   // init value
        } else {
            spinner2.setSelection(1);   // init value
        }
        spinner2.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        integerCurrency2 = pos;
                        setTextForTextView(rootView);
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                }
        );

        /*
         * bottom textView
         */
        TextView textView = rootView.findViewById(R.id.result);
        textView.setText(R.string.hint_input_value);

        return rootView;
    }

    @Override
    public void onStart() {
        Log.d("LIFECYCLE F0", "onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d("LIFECYCLE F0", "onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d("LIFECYCLE F0", "onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d("LIFECYCLE F0", "onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d("LIFECYCLE F0", "onDestroy");
        super.onDestroy();
    }

    /**
     * Returns the conversion rate for a source (from) and target (to) currency
     *
     * @param currencyFrom      source currency
     * @param currencyTo        target currency
     * @return                  conversion rate
     */
    public Double getCurrencyRate(int currencyFrom, int currencyTo) {
        Log.d("SPINNER","getCurrencyRate("+currencyFrom+", "+currencyTo+")");
        return ((MainActivity) getActivity()).getCurrencyRatesAll()[currencyFrom][currencyTo];
    }

    /**
     *
     * @param d     Double for round (#.##)
     * @return      Double
     */
    double roundTwoDecimals(double d) {

        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
        otherSymbols.setDecimalSeparator('.');
        DecimalFormat twoDForm = new DecimalFormat("#.##", otherSymbols);
        return Double.valueOf(twoDForm.format(d));
    }

    /**
     * Returns the text for the target TextView field.
     *
     * @param input         String included the text of the EditText
     * @return              String
     */
    public String getTextForEditText(String input) {
        Double from;
        Double result;
        Double currencyRate;
        String resultText;

        if (input.length() > 0 && integerCurrency1 != null && integerCurrency2 != null) {
            from = Double.parseDouble(input);
            currencyRate = getCurrencyRate(integerCurrency1, integerCurrency2);
            if (currencyRate == 0.0) {
                result = roundTwoDecimals(from);
            } else {
                result = roundTwoDecimals((from * currencyRate));
            }
            //resultText = String.valueOf(result);
            NumberFormat format = NumberFormat.getCurrencyInstance();
            DecimalFormatSymbols dfs = ((DecimalFormat) format).getDecimalFormatSymbols();
            dfs.setCurrencySymbol("");
            ((DecimalFormat) format).setDecimalFormatSymbols(dfs);
            resultText = format.format(result);
        } else {
            resultText = "";
        }
        return resultText;
    }

    /**
     * Set the new value of the TextView, which is called by the selection of each spinner
     *
     * @param rootView      View-Object
     */
    public void setTextForTextView(View rootView) {
        EditText editText1 = rootView.findViewById(R.id.input_value);
        String input = editText1.getText().toString();
        TextView textView = rootView.findViewById(R.id.result);
        textView.setText( getTextForEditText(input) );
    }

    public static String getUserCountry(Context context) {
        TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getNetworkCountryIso();
    }

    public Integer getSpinnerPositionByUserCountry(String country) {
        Integer pos;
        if( country.equals("se")) {
            pos = 1;
        } else if( country.equals("vi") || country.equals("as") || country.equals("vg") || country.equals("ec") || country.equals("gu") || country.equals("tc") || country.equals("mh")
                || country.equals("fm") || country.equals("mp") || country.equals("tl") || country.equals("pw") || country.equals("pr") || country.equals("us") || country.equals("um") ) {
            pos = 2;
        } else if( country.equals("io") || country.equals("uk") ) {
            pos = 3;
        } else if( country.equals("cn") ) {
            pos = 4;
        } else if( country.equals("jp") ) {
            pos = 5;
        } else if( country.equals("kr") ) {
            pos = 6;
        } else {
            pos = 0;
        }
        return pos;
    }
}
