package footprints.server;

import com.googlecode.mgwt.linker.linker.PermutationMapLinker;
import com.googlecode.mgwt.linker.linker.XMLPermutationProvider;
import com.googlecode.mgwt.linker.server.AbstractManifestServlet;
import com.googlecode.mgwt.linker.server.BindingProperty;
import com.googlecode.mgwt.linker.server.propertyprovider.UserAgentPropertyProvider;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletException;

public class ManifestServlet
  extends AbstractManifestServlet
{
  public ManifestServlet()
  {
    addPropertyProvider( new UserAgentPropertyProvider() );
  }

  public String getPermutationStrongName( final String baseUrl,
                                          final String moduleName,
                                          final Set<BindingProperty> computedBindings )
    throws ServletException
  {
    final String strongName = super.getPermutationStrongName( baseUrl, moduleName, computedBindings );
    if ( null != strongName )
    {
      return strongName;
    }
    else
    {
      final String realPath =
        getServletContext().getRealPath( baseUrl + moduleName + "/" + PermutationMapLinker.MANIFEST_MAP_FILE_NAME );
      try
      {
        final FileInputStream fileInputStream = new FileInputStream( realPath );
        final Map<String, List<BindingProperty>> map = XMLPermutationProvider.deserialize( fileInputStream );
        if ( 1 == map.size() )
        {
          return map.entrySet().iterator().next().getKey();
        }
        return null;
      }
      catch ( final Exception e )
      {
        log( "can not read xml file", e );
        throw new ServletException( "can not read permutation information", e );
      }
    }
  }
}
