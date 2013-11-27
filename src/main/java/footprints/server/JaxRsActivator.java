package footprints.server;

import footprints.server.service.FootprintsJaxRsApplication;
import javax.ws.rs.ApplicationPath;

@ApplicationPath( FootprintsJaxRsApplication.APPLICATION_PATH )
public class JaxRsActivator
  extends FootprintsJaxRsApplication
{
}
