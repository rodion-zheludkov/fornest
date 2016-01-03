package com.rodions.telenest;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by rodion on 26.12.15.
 */
public class TelenetMain {

    public static void main(String[] args) throws IOException {
        //Read properties
        Properties props = new Properties();
        props.load(TelenetMain.class.getClassLoader().getResourceAsStream("system.properties"));

        //Start telegram service
        TelegramService service = new TelegramService(props, new NestRegistryImpl(props));
        service.start();

    }

}
