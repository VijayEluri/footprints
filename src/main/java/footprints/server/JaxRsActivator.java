package footprints.server;

import footprints.server.service.FootprintsJaxRsApplication;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.mvc.MvcFeature;
import org.glassfish.jersey.server.mvc.jsp.JspMvcFeature;

@ApplicationPath(FootprintsJaxRsApplication.APPLICATION_PATH)
public class JaxRsActivator
  extends FootprintsJaxRsApplication
{
  @Override
  public Map<String, Object> getProperties()
  {
    final HashMap<String, Object> properties = new HashMap<>();
    properties.putAll( super.getProperties() );
    properties.put( JspMvcFeature.TEMPLATES_BASE_PATH, "/WEB-INF/jsp/" );
    return properties;
  }

  @Override
  public Set<Class<?>> getClasses()
  {
    final Set<Class<?>> classes = new HashSet<Class<?>>();
    classes.addAll( super.getClasses() );
    classes.add( AuthenticationService.class );
    classes.add( MvcFeature.class );
    classes.add( JspMvcFeature.class );
    return classes;
  }
}
