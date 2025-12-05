package com.ttt.one.gateway.predicate;

import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.GatewayPredicate;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ServerWebExchange;

import javax.validation.constraints.NotEmpty;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
@Component
public class VipRoutePredicateFactory extends AbstractRoutePredicateFactory<VipRoutePredicateFactory.Config> {
    public VipRoutePredicateFactory() {
        super(Config.class);
    }
    @Override
    public Predicate<ServerWebExchange> apply(Config config) {
        return new GatewayPredicate() {
            @Override
            public boolean test(ServerWebExchange exchange) {
                List<String> values = (List)exchange.getRequest().getHeaders().getOrDefault(config.header, Collections.emptyList());
                System.out.println("values: " + values);
                for (String value : values) {
                    if (value.contains(config.value)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }
    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("header", "value");
    }


    @Validated
    public static class Config {
        private @NotEmpty String header;
        private @NotEmpty String value;

        public String getHeader() {
            return header;
        }

        public void setHeader(String header) {
            this.header = header;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
