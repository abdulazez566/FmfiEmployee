package com.fmfi.employee.setting;

import com.fmfi.employee.rest.ApiClientDnn;
import com.fmfi.employee.rest.ApiInterfaceDnn;

public class Global {
    public final static ApiInterfaceDnn clientDnn = ApiClientDnn.getClientDnn(null).create(ApiInterfaceDnn.class);
    public  final static  String ServerURL ="http://h2817272.stratoserver.net/FmfiPs/DesktopModules/FmfiPsModuleFolder/";
}
