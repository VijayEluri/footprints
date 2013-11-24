package footprints.client.ioc;

import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import footprints.client.service.code_metrics.GwtRpcJavaNcss;
import footprints.client.service.code_metrics.JavaNcss;
import footprints.shared.service.code_metrics.GwtJavaNcssAsync;

@GinModules( { BasicModule.class, FootprintsImitServicesModule.class, FootprintsGwtRpcServicesModule.class, FootprintsGwtServicesModule.class } )
public interface FootprintsGinjector
  extends Ginjector
{
  JavaNcss getJavaNcss();
}
