package org.apereo.cas.config;

import org.apereo.cas.adaptors.yubikey.YubiKeyAccountRegistry;
import org.apereo.cas.adaptors.yubikey.YubiKeyAccountValidator;
import org.apereo.cas.adaptors.yubikey.dao.MongoDbYubiKeyAccountRegistry;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.model.support.mfa.YubiKeyMultifactorProperties;
import org.apereo.cas.mongo.MongoDbObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * This is {@link MongoDbYubiKeyConfiguration}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@Configuration("mongoDbYubiKeyConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class MongoDbYubiKeyConfiguration {

    @Autowired
    @Qualifier("yubiKeyAccountValidator")
    private YubiKeyAccountValidator yubiKeyAccountValidator;

    @Autowired
    private CasConfigurationProperties casProperties;

    @RefreshScope
    @Bean
    public PersistenceExceptionTranslationPostProcessor persistenceExceptionTranslationPostProcessor() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    @RefreshScope
    @Bean
    public MongoTemplate mongoYubiKeyTemplate() {
        final YubiKeyMultifactorProperties yubi = casProperties.getAuthn().getMfa().getYubikey();
        final MongoDbObjectFactory factory = new MongoDbObjectFactory();
        return factory.buildMongoTemplate(yubi.getMongodb());
    }
    
    @RefreshScope
    @Bean
    public YubiKeyAccountRegistry yubiKeyAccountRegistry() {
        final YubiKeyMultifactorProperties yubi = casProperties.getAuthn().getMfa().getYubikey();
        return new MongoDbYubiKeyAccountRegistry(yubiKeyAccountValidator,
                mongoYubiKeyTemplate(),
                yubi.getMongodb().getCollection(),
                yubi.getMongodb().isDropCollection());
    }
}
