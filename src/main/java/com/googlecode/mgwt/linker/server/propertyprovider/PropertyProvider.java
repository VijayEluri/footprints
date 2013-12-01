package com.googlecode.mgwt.linker.server.propertyprovider;

import java.io.Serializable;
import javax.servlet.http.HttpServletRequest;

public interface PropertyProvider
  extends Serializable
{
  String getPropertyName();

  String getPropertyValue( HttpServletRequest request )
    throws Exception;
}
