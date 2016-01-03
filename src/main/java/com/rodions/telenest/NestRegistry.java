package com.rodions.telenest;

/**
 * Created by rodion on 26.12.15.
 */
public interface NestRegistry {

    NestUserService getUserService(Integer telegramUserId);

}
