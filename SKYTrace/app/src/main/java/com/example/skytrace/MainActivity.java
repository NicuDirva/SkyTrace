package com.example.skytrace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap gMap;
    private final List<Marker> airportMarkers = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.id_map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;

        try {
            InputStream inputStream = getAssets().open("airport_coordinates.xlsx");
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || row.getCell(0) == null) continue;

                String name = row.getCell(0).getStringCellValue();
                double latitude = row.getCell(1).getNumericCellValue();
                double longitude = row.getCell(2).getNumericCellValue();
                String elevation = row.getCell(3) != null ? row.getCell(3).toString() : "";
                String municipality = row.getCell(4) != null ? row.getCell(4).toString() : "";
                String country = row.getCell(5) != null ? row.getCell(5).toString() : "";
                String icao = row.getCell(6) != null ? row.getCell(6).toString() : "";
                String iata = row.getCell(7) != null ? row.getCell(7).toString() : "";

                Airport airport = new Airport(name, latitude, longitude, elevation, municipality, country, icao, iata);

                Marker marker = gMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latitude, longitude))
                        .title(name)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.airport_marker))
                        .visible(false)); // inițial invizibil

                marker.setTag(airport);
                airportMarkers.add(marker); // ✅ adaugă în listă
            }

            gMap.setOnCameraIdleListener(() -> {
                float zoom = gMap.getCameraPosition().zoom;
                boolean showMarkers = zoom > 6;

                for (Marker marker : airportMarkers) {
                    marker.setVisible(showMarkers);
                }
            });

            gMap.setOnMarkerClickListener(marker -> {
                Airport airport = (Airport) marker.getTag();
                if (airport != null) {
                    String info = "ICAO: " + airport.icao + "\n"
                            + "IATA: " + airport.iata + "\n"
                            + "Municipality: " + airport.municipality + "\n"
                            + "Country: " + airport.country + "\n"
                            + "Elevation: " + airport.elevation + " ft";
                    marker.setSnippet(info);
                    marker.showInfoWindow();
                }
                return true;
            });

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Eroare la citirea fișierului", Toast.LENGTH_LONG).show();
        }
    }
}