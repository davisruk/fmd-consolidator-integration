package org.davisr.spring.camel.routes;

import java.util.ArrayList;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.processor.aggregate.AggregationStrategy;

public class BaggregationStrategy implements AggregationStrategy {

	@Override
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        ArrayList list = null;
        Message newIn = newExchange.getIn();
		Object newBody = newIn.getBody();
        if (oldExchange == null) {
        	list = new ArrayList();
        	list.add(newBody);
        	newIn.setBody(list);
            return newExchange;
        }
        Message in = oldExchange.getIn();
        list = in.getBody(ArrayList.class);
        return oldExchange;
    }

}
