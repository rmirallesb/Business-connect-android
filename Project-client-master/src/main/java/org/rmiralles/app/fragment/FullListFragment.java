package org.rmiralles.app.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import org.rmiralles.app.R;
import org.rmiralles.app.activity.TextActivity;
import org.rmiralles.app.adapter.Adapter;
import org.rmiralles.app.base.Text;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class FullListFragment extends Fragment implements AdapterView.OnItemClickListener {

    private SharedPreferences sharedPreferences;

    private Adapter adapter;

    private ListView lvTexts;
    private List<Text> textList;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        sharedPreferences = getActivity().getSharedPreferences("org.rmiralles.preferences", getActivity().MODE_PRIVATE);
        if (sharedPreferences.getInt("id", -1) == -1){
            getActivity().finish();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if (sharedPreferences.getInt("id", -1) == -1){
            getActivity().finish();
        }
    }

    private void rowList(List<Text> textList){
        this.textList = textList;
        adapter = new Adapter(getActivity(), R.layout.add_row, this.textList);
        lvTexts.setAdapter(adapter);
        lvTexts.setOnItemClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_fulllist, container, false);
        new Connect().execute();

        lvTexts = (ListView) view.findViewById(R.id.lvTexts);

        return  view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
        Text text = textList.get(i);

        Intent intent = new Intent(getActivity(), TextActivity.class);
        intent.putExtra("text", String.valueOf(text.getId()));
        startActivity(intent);
    }

    public class Connect extends AsyncTask<Void, Void, List> {

        @Override
        protected List doInBackground(Void... params) {
            try {
                final String URL = "http://192.168.1.50:8080/gettexts";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                Text[] texts = restTemplate.getForObject(URL, Text[].class);
                List<Text> textList = new ArrayList<Text>();
                for (Text text : texts) {
                    textList.add(text);
                }
                return textList;
            }catch(Exception e) {
                Log.e("", e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(List textList) {
            rowList(textList);
        }
    }
}