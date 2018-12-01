package org.davisr.spring.camel.routes;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.spi.InterceptStrategy;

public class NMVSEndpointInterceptStrategy implements InterceptStrategy {

	@Override
	public Processor wrapProcessorInInterceptors(CamelContext context, ProcessorDefinition<?> definition,
			Processor target, Processor nextTarget) throws Exception {
        return new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                System.out.println(definition);
                target.process(exchange);

            }
        };
	}

}
