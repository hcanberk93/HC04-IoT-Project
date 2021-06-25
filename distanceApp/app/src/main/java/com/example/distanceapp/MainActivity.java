package com.example.distanceapp;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import helpers.MqttHelper;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    MqttHelper mqttHelper;
    GraphView graph;
    String dataReceived;
    double receivedVal;
    private LineGraphSeries<DataPoint> series;
    int timeStart = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        graph = findViewById(R.id.graph);
        series = new LineGraphSeries<DataPoint>();
        graph.addSeries(series);

        Viewport viewPort = graph.getViewport();
        viewPort.setYAxisBoundsManual(true);
        viewPort.setMinY(0);
        viewPort.setMaxY(200);
        viewPort.setMinX(0);
        viewPort.setMaxX(200);
        viewPort.setScrollable(true);

        startMqtt();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(new Runnable(){
            @Override
            public void run(){
                while(true){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                                addValToGraph();}
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


            }
        }).start();
    }
    private void addValToGraph(){
        series.appendData(new DataPoint(timeStart++, receivedVal), false, 100);
        graph.addSeries(series);
    }

    private boolean isConvertedToDouble(String x){
        try{
            Double.parseDouble(x);
            return true;
        }
        catch(Exception e){
            return false;
        }
    }

    private void startMqtt(){
        mqttHelper = new MqttHelper(getApplicationContext());
        mqttHelper.mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                Log.w("Debug","Connected");
            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.w("Debug",mqttMessage.toString());
                dataReceived = mqttMessage.toString();
                //System.out.println(dataReceived);
                if(isConvertedToDouble(dataReceived)) { //Ensuring the data is actual numerical data
                    receivedVal = Double.parseDouble(dataReceived.toString());
                    System.out.println(receivedVal);
                }

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }


        });
    }
}