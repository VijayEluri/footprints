package footprints.server;

import footprints.server.service.FootprintsJaxRsApplication;
import java.util.Set;
import javax.ws.rs.ApplicationPath;

@ApplicationPath( FootprintsJaxRsApplication.APPLICATION_PATH )
public class JaxRsActivator
  extends FootprintsJaxRsApplication
{
  @Override
  public Set<Class<?>> getClasses()
  {
    final Set<Class<?>> classes = super.getClasses();
    classes.add( NoResultExceptionMapper.class );
    classes.add( SimpleRestGeoService.class );
    classes.add( FormatErrorExceptionMapper.class );
    return classes;
  }
}
