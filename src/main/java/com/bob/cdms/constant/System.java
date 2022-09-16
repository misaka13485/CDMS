package com.bob.cdms.constant;

import java.util.concurrent.TimeUnit;

public class System {
    //redis过期时间
    public static final int REDIS_EXPIRE_TIME = 60*60*2;
    public static final TimeUnit REDIS_EXPIRE_TIME_UNIT = TimeUnit.SECONDS;
    public static final int CACHE_TIME = 60*60*24;
    public static final String LOGIN_KEY = "login:";
    public static final String QUERY_KEY = "cdInfo:";
}
