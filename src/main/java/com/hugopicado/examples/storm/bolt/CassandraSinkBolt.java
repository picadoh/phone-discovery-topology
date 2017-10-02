package com.hugopicado.examples.storm.bolt;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.hugopicado.examples.storm.domain.PhoneNumber;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;

import java.util.*;

import static com.datastax.driver.core.querybuilder.QueryBuilder.insertInto;

public class CassandraSinkBolt extends BaseRichBolt {
    private transient Session session;

    public void prepare(Map config, TopologyContext context,
                        OutputCollector collector) {
        Cluster cluster = Cluster.builder()
                .addContactPoints("cassandra")
                .build();

        session = cluster.connect();

        initializeSchema(session);
    }

    public void execute(Tuple tuple) {
        PhoneNumber number = (PhoneNumber) tuple.getValueByField("number");

        session.execute(insertInto("storm_sample_ks","phones").values(
                new String[]{
                        "country_calling_code",
                        "national_number",
                        "country_code",
                        "number_type"
                },
                new Object[]{
                        number.getCountryCallingCode(),
                        number.getNationalNumber(),
                        number.getCountryCode(),
                        number.getNumberType()
                }
        ));
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        // no further bolts
    }

    private void initializeSchema(Session session) {
        session.execute("CREATE KEYSPACE IF NOT EXISTS storm_sample_ks " +
                "WITH replication={'class':'SimpleStrategy', 'replication_factor': '1'}");

        session.execute("CREATE TABLE IF NOT EXISTS storm_sample_ks.phones" +
                "(country_calling_code int, national_number bigint, country_code text, number_type text, " +
                "primary key ((country_calling_code, national_number), number_type))");
    }
}