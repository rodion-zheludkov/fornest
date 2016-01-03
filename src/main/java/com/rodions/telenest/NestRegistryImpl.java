package com.rodions.telenest;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by rodion on 26.12.15.
 */
public class NestRegistryImpl implements NestRegistry {

    private NestUserService service;

    public NestRegistryImpl(Properties props) {
        service = new NestUserService(props);
    }

    @Override
    public NestUserService getUserService(Integer userTeleId) {
        return service;
    }
}
