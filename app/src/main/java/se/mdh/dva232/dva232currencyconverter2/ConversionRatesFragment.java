package se.mdh.dva232.dva232currencyconverter2;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

public class ConversionRatesFragment extends Fragment {

    public ConversionRatesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
    * @return A new instance of fragment ConversionRatesFragment.
     */
    public static ConversionRatesFragment newInstance() {
        Log.d("FRAGMENT 1","ConversionRatesFragment newInstance()");
        ConversionRatesFragment fragment = new ConversionRatesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("FRAGMENT 1","onCreateView(...)");
        View rootView = inflater.inflate(R.layout.fragment_conversion_rates, container, false);

        /*
         * resource (array)
         */
        final String[] currencies = getResources().getStringArray(R.array.currencies_array);
        //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.currencies_array, android.R.layout.simple_list_item_1);

        /*
         * ListView
         */
        ListView listView = rootView.findViewById(R.id.conversion_rates_list_all);
        CustomListViewAdapter cus = new CustomListViewAdapter(getContext(), currencies);
        //listView.setAdapter(adapter);
        listView.setAdapter(cus);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent,View view, int pos, long id) {
                Log.d("TEST", "ListView click: " + parent.getItemAtPosition(pos).toString());
                AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext());
                String output = getStringFromCurrencyArrayByPosition(pos);
                /*
                switch( pos ) {
                    case 0: String[] currencyEUR = getResources().getStringArray(R.array.EUR);
                        output = getStringFromArray(currencyEUR);
                        break;
                    case 1: String[] currencySEK = getResources().getStringArray(R.array.SEK);
                        output = getStringFromArray(currencySEK);
                        break;
                    case 2: String[] currencyUSD = getResources().getStringArray(R.array.USD);
                        output = getStringFromArray(currencyUSD);
                        break;
                    case 3: String[] currencyGBP = getResources().getStringArray(R.array.GBP);
                        output = getStringFromArray(currencyGBP);
                        break;
                    case 4: String[] currencyCNY = getResources().getStringArray(R.array.CNY);
                        output = getStringFromArray(currencyCNY);
                        break;
                    case 5: String[] currencyJPY = getResources().getStringArray(R.array.JPY);
                        output = getStringFromArray(currencyJPY);
                        break;
                    case 6: String[] currencyKRW = getResources().getStringArray(R.array.KRW);
                        output = getStringFromArray(currencyKRW);
                        break;
                }
                */
                String from = getString(R.string.dialog_conversion_rates_from);
                //String fromCurrency = parent.getItemAtPosition(pos).toString();
                String fromCurrency = currencies[pos];
                String to = getString(R.string.dialog_conversion_rates_to);
                builder.setMessage(output).setTitle( getString(R.string.menu_conversion_rates) + " " + from+" "+fromCurrency+" "+to);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        return rootView;
    }

    public String getStringFromCurrencyArrayByPosition(Integer pos) {
        String[] cas = getResources().getStringArray(R.array.currencies_array); // currency string
        Double[][] cr = ((MainActivity) getActivity()).getCurrencyRatesAll();   // currency rates
        return cas[0]+": "+fn(cr[pos][0])+"\n"+cas[1]+": "+fn(cr[pos][1])+"\n"+cas[2]+": "+fn(cr[pos][2])+"\n"+cas[3]+": "+fn(cr[pos][3])+"\n"+cas[4]+": "+fn(cr[pos][4])+"\n"+cas[5]+": "+fn(cr[pos][5])+"\n"+cas[6]+": "+fn(cr[pos][6]);
    }

    public String fn(Double value) {
        NumberFormat format = NumberFormat.getCurrencyInstance();
        DecimalFormatSymbols dfs = ((DecimalFormat) format).getDecimalFormatSymbols();
        dfs.setCurrencySymbol("");
        ((DecimalFormat) format).setDecimalFormatSymbols(dfs);
        return format.format(value);
    }

}
