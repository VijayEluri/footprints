package footprints.server;

import com.googlecode.mgwt.linker.server.AbstractManifestServlet;
import com.googlecode.mgwt.linker.server.propertyprovider.UserAgentPropertyProvider;

public class ManifestServlet
  extends AbstractManifestServlet
{
  public ManifestServlet()
  {
    addPropertyProvider( new UserAgentPropertyProvider() );
  }
}
