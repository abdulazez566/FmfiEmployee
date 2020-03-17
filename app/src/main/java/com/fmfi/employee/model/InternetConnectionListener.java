package com.fmfi.employee.model;

public interface InternetConnectionListener
{
    boolean isInternetAvailable();
    void onInternetUnavailable();
}
