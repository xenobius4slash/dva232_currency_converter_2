package se.mdh.dva232.dva232currencyconverter2;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;


public class CurrencyConverterFragment extends Fragment {

    public Integer integerCurrency1 = null;
    public Integer integerCurrency2 = null;
    public Boolean savedInstance = false;
    public String SAVED_ET1;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("FRAGMENT 0", "onCreateView(...)");
        // Inflate the layout for this fragment
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
        spinner1.setSelection(0);   // init value
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
        if(savedInstanceState!=null && savedInstanceState.containsKey("ET1")) {
            Log.d("SAVE","key 'ET1' exist");
            SAVED_ET1 = savedInstanceState.getString("ET1");
        }
        editText1.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if( editText1.hasFocus() && !savedInstance ) {
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
        Spinner spinner2 = rootView.findViewById(R.id.currencies_spinner2);
        spinner2.setAdapter(adapter);
        spinner2.setSelection(1);   // init value
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
        String conversionRate;
        switch( currencyFrom ) {
            case 0: String[] currencyEUR = getResources().getStringArray(R.array.EUR);
                conversionRate = currencyEUR[currencyTo];
                break;
            case 1: String[] currencySEK = getResources().getStringArray(R.array.SEK);
                conversionRate = currencySEK[currencyTo];
                break;
            case 2: String[] currencyUSD = getResources().getStringArray(R.array.USD);
                conversionRate = currencyUSD[currencyTo];
                break;
            case 3: String[] currencyGBP = getResources().getStringArray(R.array.GBP);
                conversionRate = currencyGBP[currencyTo];
                break;
            case 4: String[] currencyCNY = getResources().getStringArray(R.array.CNY);
                conversionRate = currencyCNY[currencyTo];
                break;
            case 5: String[] currencyJPY = getResources().getStringArray(R.array.JPY);
                conversionRate = currencyJPY[currencyTo];
                break;
            case 6: String[] currencyKRW = getResources().getStringArray(R.array.KRW);
                conversionRate = currencyKRW[currencyTo];
                break;
            default: conversionRate = "0";
        }
        return Double.parseDouble(conversionRate);
    }

    /**
     *
     * @param d     Double for round (#.##)
     * @return      Double
     */
    double roundTwoDecimals(double d)
    {
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
            resultText = String.valueOf(result);
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

}
