package com.example.keith.myapplication;

//import android.graphics.Color;
//import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.graphhopper.GraphHopper;

import org.mapsforge.core.graphics.Canvas;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.graphics.Color;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.Point;
import org.mapsforge.core.util.MercatorProjection;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.graphics.AndroidResourceBitmap;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.overlay.FixedPixelCircle;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.rendertheme.InternalRenderTheme;

import java.io.File;


public class MapViewFragment extends Fragment {

    private static final String MAPFILE = "semenyih.map";
    private GraphHopper hopper;
    private MapView mapView;
    private TileCache tileCache;
    private TileRendererLayer tileRendererLayer;
    private File mapsFolder = new File(Environment.getExternalStorageDirectory(),"semenyih-gh");
    public static final String EXTRA_POI_ID = "com.myapplication.poi_id";

    public MapViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidGraphicFactory.createInstance(this.getActivity().getApplication());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_map_view,container,false);
        mapView = (MapView)rootView.findViewById(R.id.mapView);
        mapView.setClickable(true);
        mapView.setBuiltInZoomControls(false);
        tileCache = AndroidUtil.createTileCache(this.getActivity().getApplicationContext(),
                "mapcache", mapView.getModel().displayModel.getTileSize(), 1f,
                this.mapView.getModel().frameBufferModel.getOverdrawFactor());
        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();
        mapView.getModel().mapViewPosition.setMapLimit(new BoundingBox(2.9074, 101.8045, 2.978, 101.9149));
        mapView.getModel().mapViewPosition.setCenter(new LatLong(2.943332,101.875841));
        mapView.getModel().mapViewPosition.setZoomLevel((byte) 16);
        tileRendererLayer = new TileRendererLayer(tileCache, mapView.getModel().mapViewPosition, false, true, AndroidGraphicFactory.INSTANCE);
        tileRendererLayer.setMapFile(getMapFile());
        tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.OSMARENDER);
        mapView.getLayerManager().getLayers().add(tileRendererLayer);
//        loadGraphStorage();
    }

    @Override
    public void onStop(){
        super.onStop();
        mapView.getLayerManager().getLayers().remove(tileRendererLayer);
        tileRendererLayer.onDestroy();
        AndroidResourceBitmap.clearResourceBitmaps();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        tileCache.destroy();
//        hopper.close();
        mapView.getModel().mapViewPosition.destroy();
        mapView.destroy();
    }

    private File getMapFile(){
        File file = new File(Environment.getExternalStorageDirectory(),MAPFILE);
        return file;
    }

    void loadGraphStorage(){

        logUser("loading graph");
        new GHAsyncTask<Void, Void, Path>()
        {
            protected Path saveDoInBackground( Void... v ) throws Exception
            {
                GraphHopper tmpHopp = new GraphHopper().forMobile();
                tmpHopp.setCHShortcuts("fastest");
                tmpHopp.load(new File(mapsFolder, "semenyih").getAbsolutePath());
                hopper = tmpHopp;
                return null;
            }

            protected void onPostExecute( Path o )
            {
                if (hasError())
                {
                    logUser("An error happened while creating graph:"
                            + getErrorMessage());
                } else
                {
                    logUser("Finished loading graph. Press long to define where to start and end the route.");
                }

            }
        }.execute();
    }

    public void processPOI(){
        Bundle b = getArguments();
        String id = b.getString("poiId");
        POI p = DBHelper.getInstance(getActivity()).getPoi(id);
        logUser(p.getName());
        mapView.getModel().mapViewPosition.animateTo(p.getCoordinates());
        displayMarker(p.getCoordinates());
    }

    private void logUser( String str ){
        Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
    }

    private void displayMarker(final LatLong coordinates){
        Paint BLACK = Utils.createPaint(AndroidGraphicFactory.INSTANCE.createColor(Color.BLACK),0,Style.FILL);
        float circleSize = 4 * mapView.getModel().displayModel.getScaleFactor();

        FixedPixelCircle tappableCircle = new FixedPixelCircle(coordinates, circleSize, BLACK, null){
            @Override
            public void draw(BoundingBox boundingBox, byte zoomLevel, Canvas canvas, Point topLeftPoint){
                super.draw(boundingBox, zoomLevel, canvas, topLeftPoint);
            }
        };
        mapView.getLayerManager().getLayers().add(tappableCircle);
        tappableCircle.requestRedraw();
    }
}
