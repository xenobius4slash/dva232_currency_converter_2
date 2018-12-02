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
                String output = null;
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

    public String getStringFromArray(String[] array) {
        String[] ca = getResources().getStringArray(R.array.currencies_array);
        return ca[0]+": "+array[0]+"\n"+ca[1]+": "+array[1]+"\n"+ca[2]+": "+array[2]+"\n"+ca[3]+": "+array[3]+"\n"+ca[4]+": "+array[4]+"\n"+ca[5]+": "+array[5]+"\n"+ca[6]+": "+array[6];
    }

}
