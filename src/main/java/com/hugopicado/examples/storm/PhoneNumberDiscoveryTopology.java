package com.hugopicado.examples.storm;

import com.hugopicado.examples.storm.bolt.CassandraSinkBolt;
import com.hugopicado.examples.storm.bolt.PhoneNumberExtractorBolt;
import com.hugopicado.examples.storm.bolt.SplitMessageBolt;
import com.hugopicado.examples.storm.spout.SocketSpout;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.TopologyBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PhoneNumberDiscoveryTopology {

    public static void main(String[] args) throws Exception {
        TopologyBuilder builder = new TopologyBuilder();

        builder.setSpout("socket-spout", new SocketSpout(9999));

        builder.setBolt("split-message", new SplitMessageBolt())
                .shuffleGrouping("socket-spout");

        builder.setBolt("phone-number-extractor", new PhoneNumberExtractorBolt())
                .shuffleGrouping("split-message");

        builder.setBolt("cassandra-sink", new CassandraSinkBolt())
                .globalGrouping("phone-number-extractor");

        Config config = new Config();

        String topologyName = buildTopologyName();

        if (args.length > 0 && args[0].equals("submit")) {
            submitToCluster(topologyName, config, builder.createTopology());
        } else {
            submitToLocal(topologyName, config, builder.createTopology());
        }
    }

    private static void submitToCluster(String topologyName, Config config, StormTopology topology) throws Exception {
        StormSubmitter.submitTopologyWithProgressBar(topologyName, config, topology);
    }

    private static void submitToLocal(String topologyName, Config config, StormTopology topology) throws Exception {
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology(topologyName,
                config,
                topology);

        Thread.sleep(Integer.MAX_VALUE);

        cluster.killTopology(topologyName);
        cluster.shutdown();
    }

    private static String buildTopologyName() {
        SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMdd-HHmm");
        String suffix = sdf.format(new Date());
        return PhoneNumberDiscoveryTopology.class.getSimpleName() + "_" + suffix;
    }

}
