package net.seocraft.api.shared;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.AbstractModule;

import java.util.concurrent.Executors;

public class SharedModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ListeningExecutorService.class).toInstance(MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(8)));
    }
}