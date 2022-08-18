package org.acme.graphql;

import io.smallrye.graphql.api.Context;
import io.smallrye.graphql.spi.EventingService;
import org.eclipse.microprofile.config.ConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Service implements EventingService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public String getConfigKey() {
        return "EventingService.enabled";
    }

    @Override
    public void beforeExecute(Context context) {
        String value = ConfigProvider.getConfig().getValue("eventing.config", String.class);
        log.info("value {}", value);
        EventingService.super.beforeExecute(context);
    }

}
