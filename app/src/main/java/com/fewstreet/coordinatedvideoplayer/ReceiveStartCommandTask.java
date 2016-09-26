package com.fewstreet.coordinatedvideoplayer;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.Date;

/**
 * Created by peter on 8/17/16.
 */
public class ReceiveStartCommandTask extends AsyncTask<DatagramSocket,Void,Long> {
    private final String TAG = "ReceiveStartCommandTask";
    private VideoPlaybackActivity videoPlayer;

    public ReceiveStartCommandTask(VideoPlaybackActivity player) {
        super();
        videoPlayer = player;
    }

    @Override
    protected Long doInBackground(DatagramSocket... datagramSockets) {
        if(datagramSockets.length == 1){
            byte[] recvBuf = new byte[1500];
            DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
            try {
                datagramSockets[0].receive(packet);
                String json = new String(packet.getData(), 0, packet.getLength());
                Gson gson = new Gson();
                Long startTime = gson.fromJson(json, Long.class);
                return startTime;
            } catch (IOException e) {
                //e.printStackTrace();
                Log.d(TAG, "Datagram socket receive was interrupted ");
                return null;
            }
        }
        return null;
    }

    protected void onPostExecute(Long result) {
        if(result != null) {
            Log.d(TAG, "Got a time to play video: " + result.toString());
            videoPlayer.setVideoPlaybackTime(result);
        } else {
            Log.d(TAG, "Result from UDP socket was null");
        }
        videoPlayer.startSocketListenerTask(); //restart a new socket listener to receive any further packets
    }
}
