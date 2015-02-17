package com.example.keith.myapplication;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.mapsforge.core.model.LatLong;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ResultListFragment extends ListFragment {

    public static final String VIEWPOI_INTENT = "viewPOI";

//    private PoiDB db;
    private ArrayList<POI> results = new ArrayList<POI>();
    private ResultAdapter adapter;

    public ResultListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        db = new PoiDB(getActivity());
//        adapter = new ArrayAdapter<POI>(getActivity(),android.R.layout.simple_list_item_1,results);
        adapter = new ResultAdapter(results);
        setListAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_result_list,container,false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id){
//        POI p = (POI)(getListAdapter()).getItem(position);
        POI p = ((ResultAdapter)getListAdapter()).getItem(position);
        Log.d("ResultListFragment",p.getName() + " was clicked");
        Intent i = new Intent(getActivity(),MainActivity.class);
        i.setAction(VIEWPOI_INTENT);
        i.putExtra(MapViewFragment.EXTRA_POI_ID, p.getId());
        startActivity(i);
    }

    private class ResultAdapter extends ArrayAdapter<POI>{
        public ResultAdapter(ArrayList<POI> results){
            super(getActivity(),0,results);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            if(convertView == null){
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_result,null);
            }
            POI p = getItem(position);

            TextView idTextView = (TextView)convertView.findViewById(R.id.result_list_item_id);
            idTextView.setText(p.getId());
            TextView nameTextView = (TextView)convertView.findViewById(R.id.result_list_item_name);
            nameTextView.setText(p.getName());

            return convertView;
        }
    }



    public void processQuery(String query){
        results.clear();
//        ArrayList<POI>poiList = db.getPois(query);
        ArrayList<POI>poiList = DBHelper.getInstance(getActivity()).getPois(query);
        if(poiList.isEmpty()) {
            results.add(new POI("0","No results found",new LatLong(0,0)));
        }
        else {
            for (POI x : poiList) {
                results.add(x);
            }
        }
        adapter.notifyDataSetChanged();

    }

}
