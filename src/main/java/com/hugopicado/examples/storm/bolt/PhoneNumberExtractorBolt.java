package com.hugopicado.examples.storm.bolt;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.hugopicado.examples.storm.domain.PhoneNumber;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.Map;
import java.util.Optional;

public class PhoneNumberExtractorBolt extends BaseRichBolt {
    private transient OutputCollector collector;
    private transient PhoneNumberUtil phoneNumberUtil;

    public void prepare(Map config, TopologyContext context,
                        OutputCollector collector) {
        this.collector = collector;
        this.phoneNumberUtil = PhoneNumberUtil.getInstance();
    }

    public void execute(Tuple tuple) {
        String number = tuple.getStringByField("number");

        parsePhoneNumber(number).ifPresent(pn -> this.collector.emit(new Values(pn)));
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("number"));
    }

    private Optional<PhoneNumber> parsePhoneNumber(String source) {

        try {
            Phonenumber.PhoneNumber input = phoneNumberUtil.parse(source, null);

            PhoneNumber number = new PhoneNumber();
            number.setCountryCallingCode(input.getCountryCode());
            number.setNationalNumber(input.getNationalNumber());
            number.setCountryCode(phoneNumberUtil.getRegionCodeForNumber(input));
            number.setNumberType(phoneNumberUtil.getNumberType(input).name());
            return Optional.of(number);

        } catch (NumberParseException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}