package com.example.keith.myapplication;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.SearchView;
import android.view.View;

import org.mapsforge.map.android.view.MapView;

public class MainActivity extends ActionBarActivity {
    MenuItem search;
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if(fragment==null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainer,new MapViewFragment(),"MapView")
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView)menu.findItem(R.id.menu_item_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        search = menu.findItem(R.id.menu_item_search);
        MenuItemCompat.setOnActionExpandListener(search,new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                if(getSupportFragmentManager().getBackStackEntryCount()==2){
                    getSupportFragmentManager().popBackStackImmediate();
                    Log.d(TAG,"Top stack popped");
                    Log.d(TAG,"Current stack count is "+getSupportFragmentManager().getBackStackEntryCount());
                }
                ResultListFragment resultListFragment = (ResultListFragment)getSupportFragmentManager().findFragmentByTag("ResultList");
                if(resultListFragment==null){
                    Fragment fragment = new ResultListFragment();
                    startFragment(fragment, "ResultList");
                }
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                ResultListFragment resultListFragment = (ResultListFragment)getSupportFragmentManager().findFragmentByTag("ResultList");
                if(resultListFragment.isVisible()) {
                    getSupportFragmentManager().popBackStackImmediate();
                }
                return true;
            }
        });
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNewIntent(Intent intent){
        if(Intent.ACTION_SEARCH.equals(intent.getAction())){
            ResultListFragment fragment = (ResultListFragment)getSupportFragmentManager().findFragmentByTag("ResultList");
            if(fragment != null) {
                fragment.processQuery(intent.getStringExtra(SearchManager.QUERY));
            }
        }
        else if(ResultListFragment.VIEWPOI_INTENT.equals(intent.getAction())){
            Log.d(TAG,"A POI was selected");
            MapViewFragment fragment = new MapViewFragment();
            Bundle args = new Bundle();
            args.putString("poiId",intent.getStringExtra(MapViewFragment.EXTRA_POI_ID));
            fragment.setArguments(args);
            startFragment(fragment, "MapView");
            search.collapseActionView();
            fragment.processPOI();
        }
    }

    private void startFragment(Fragment fragment, String fragmentTag){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment, fragmentTag)
                .addToBackStack(null)
                .commit();
        getSupportFragmentManager().executePendingTransactions();
        Log.d(TAG, "New fragment launched. Current stack count is: "+ getSupportFragmentManager().getBackStackEntryCount() );
    }
}
