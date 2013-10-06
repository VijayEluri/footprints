package footprints.client.ioc;

import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import footprints.shared.service.code_metrics.GwtJavaNcssAsync;

@GinModules( { BasicModule.class/*, FootprintsRestyGwtServicesModule.class*/, FootprintsGwtRpcServicesModule.class, FootprintsGwtServicesModule.class } )
public interface FootprintsGinjector
  extends Ginjector
{
  //RestGwtJavaNcss getRestGwtJavaNcss();

  GwtJavaNcssAsync getGwtJavaNcss();
}
