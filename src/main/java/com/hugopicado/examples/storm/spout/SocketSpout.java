package com.hugopicado.examples.storm.spout;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

public class SocketSpout extends BaseRichSpout {
    private transient SpoutOutputCollector collector;
    private transient ServerSocket serverSocket;
    private int port;

    public SocketSpout(int port) {
        this.port = port;
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("message"));
    }

    public void open(Map map, TopologyContext ctx, SpoutOutputCollector collector) {
        this.collector = collector;
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void nextTuple() {
        try (Socket socket = serverSocket.accept();
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            collector.emit(new Values(reader.readLine()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}