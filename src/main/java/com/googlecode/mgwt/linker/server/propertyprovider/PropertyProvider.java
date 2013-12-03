package com.googlecode.mgwt.linker.server.propertyprovider;

import javax.servlet.http.HttpServletRequest;

public interface PropertyProvider
{
  String getPropertyName();

  String getPropertyValue( HttpServletRequest request )
    throws Exception;
}
