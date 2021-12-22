package com.efasttask.api.google.drive;

import com.google.api.client.util.store.AbstractDataStoreFactory;
import com.google.api.client.util.store.AbstractMemoryDataStore;
import com.google.api.client.util.store.DataStore;

import java.io.Serializable;
import java.util.HashMap;

public class GeneralDataSourceFactory extends AbstractDataStoreFactory {

    private final GeneralAction generalAction;

    public GeneralDataSourceFactory(GeneralAction generalAction) {
        this.generalAction = generalAction;
    }

    protected <V extends Serializable> DataStore<V> createDataStore(String id) {
        return new GeneralDataSourceFactory.GeneralDataSource<>(this, id, generalAction);
    }

    static class GeneralDataSource<V extends Serializable> extends AbstractMemoryDataStore<V> {
        private final GeneralAction generalAction;

        GeneralDataSource(GeneralDataSourceFactory dataStore, String id, GeneralAction generalAction) {
            super(dataStore, id);
            this.generalAction = generalAction;
            if (!generalAction.exists()) {
                this.save();
            } else {
                this.keyValueMap = generalAction.fetch();
            }
        }

        public void save() {
            this.generalAction.save(keyValueMap);
        }

        public GeneralDataSourceFactory getDataStoreFactory() {
            return (GeneralDataSourceFactory) super.getDataStoreFactory();
        }
    }

    public interface GeneralAction {
        void save(HashMap<String, byte[]> keyValueMap);

        HashMap<String, byte[]> fetch();

        boolean exists();
    }
}
