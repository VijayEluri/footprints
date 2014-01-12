package footprints.client.ioc;

import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import footprints.client.SimpleUI;

@GinModules( { BasicModule.class, FootprintsImitServicesModule.class, FootprintsGwtRpcServicesModule.class } )
public interface FootprintsGinjector
  extends Ginjector
{
  SimpleUI getSimpleUI();
}
