package org.infinispan.inmemory;

import org.apache.commons.io.IOUtils;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.commons.configuration.StringConfiguration;
import org.infinispan.commons.configuration.XMLStringConfiguration;
import org.infinispan.inmemory.config.InmemoryCatalogueConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Component
public class ConfigureSQLStore {

   private static final Logger LOGGER = LoggerFactory.getLogger(ConfigureSQLStore.class);

   private final RemoteCacheManager remoteCacheManager;
   private final InmemoryCatalogueConfig inmemoryCatalogueConfig;

   public ConfigureSQLStore(RemoteCacheManager remoteCacheManager,
                            InmemoryCatalogueConfig inmemoryCatalogueConfig) {
      this.remoteCacheManager = remoteCacheManager;
      this.inmemoryCatalogueConfig = inmemoryCatalogueConfig;
   }

   @EventListener(ApplicationReadyEvent.class)
   public void onStart() {
      LOGGER.info("Infinispan SQL Store is starting Powered by Spring Boot");
      LOGGER.info("  _   _   _   _   _   _   _   _");
      LOGGER.info(" / \\ / \\ / \\ / \\ / \\ / \\ / \\ / \\");
      LOGGER.info("( S | q | l | S | t | o | r | e )");
      LOGGER.info(" \\_/ \\_/ \\_/ \\_/ \\_/ \\_/ \\_/ \\_/");

      try {
         LOGGER.info("Creating caches...");
         URL tableStoreCacheConfig = getClass().getClassLoader().getResource("tableStore.xml");
         URL queryStoreCacheConfig = getClass().getClassLoader().getResource("queryStore.xml");

         String configTable = replaceDBConnectionConfiguration(tableStoreCacheConfig);
         String configQuery = replaceDBConnectionConfiguration(queryStoreCacheConfig);

         remoteCacheManager.administration()
                 .getOrCreateCache(inmemoryCatalogueConfig.getCatalogCacheName(), new StringConfiguration(configTable));
         remoteCacheManager.administration()
                 .getOrCreateCache(inmemoryCatalogueConfig.getSoldProductsName(), new StringConfiguration(configQuery));

      } catch (Exception e) {
         LOGGER.error("Something went wrong creating caches", e);
      }
   }

   private String replaceDBConnectionConfiguration(URL cacheConfig) throws IOException {
      String config = IOUtils.toString(cacheConfig, StandardCharsets.UTF_8)
              .replace("CONNECTION_URL", inmemoryCatalogueConfig.getConnectionUrl())
              .replace("USER_NAME", inmemoryCatalogueConfig.getUsername())
              .replace("PASSWORD", inmemoryCatalogueConfig.getPassword())
              .replace("DIALECT", inmemoryCatalogueConfig.getDialect())
              .replace("DRIVER", inmemoryCatalogueConfig.getDriver());

      return config;
   }
}
