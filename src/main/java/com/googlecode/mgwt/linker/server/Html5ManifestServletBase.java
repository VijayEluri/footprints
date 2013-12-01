package com.googlecode.mgwt.linker.server;

import com.googlecode.mgwt.linker.linker.PermutationMapLinker;
import com.googlecode.mgwt.linker.linker.XMLPermutationProvider;
import com.googlecode.mgwt.linker.server.propertyprovider.PropertyProvider;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Html5ManifestServletBase
  extends HttpServlet
{

  private static final long serialVersionUID = -2540671294104865306L;

  private Map<String, PropertyProvider> propertyProviders = new HashMap<String, PropertyProvider>();


  protected void addPropertyProvider( PropertyProvider propertyProvider )
  {
    propertyProviders.put( propertyProvider.getPropertyName(), propertyProvider );
  }

  @Override
  protected void doGet( final HttpServletRequest req, final HttpServletResponse resp )
    throws ServletException, IOException
  {
    final String moduleName = getModuleName( req );
    final String baseUrl = getBaseUrl( req );
    final Set<BindingProperty> computedBindings = calculateBindingPropertiesForClient( req );
    final String strongName = getPermutationStrongName( baseUrl, moduleName, computedBindings );
    if ( null != strongName )
    {
      final String manifest =
        readManifest( baseUrl + moduleName + "/" + strongName + PermutationMapLinker.PERMUTATION_MANIFEST_FILE_ENDING );
      serveStringManifest( resp, manifest );
    }
    else
    {
      throw new ServletException( "unknown device" );
    }
  }

  protected String getBaseUrl( final HttpServletRequest req )
  {
    final String base = req.getServletPath();
    // cut off module
    return base.substring( 0, base.lastIndexOf( "/" ) + 1 );
  }

  public String readManifest( final String filePath )
    throws ServletException
  {
    final File manifestFile = new File( getServletContext().getRealPath( filePath ) );

    final StringWriter manifestWriter = new StringWriter();
    BufferedReader br = null;
    try
    {
      br = new BufferedReader( new FileReader( manifestFile ) );
      String line;
      while ( ( line = br.readLine() ) != null )
      {
        manifestWriter.append( line ).append( "\n" );
      }

      return manifestWriter.toString();
    }
    catch ( final Exception e )
    {
      log( "error while reading manifest file", e );
      throw new ServletException( "error while reading manifest file", e );
    }
    finally
    {
      if ( br != null )
      {
        try
        {
          br.close();
        }
        catch ( IOException e )
        {

        }
      }
    }
  }

  public Set<BindingProperty> calculateBindingPropertiesForClient( final HttpServletRequest req )
    throws ServletException
  {

    try
    {
      final Set<BindingProperty> computedBindings = new HashSet<BindingProperty>();

      final Set<Entry<String, PropertyProvider>> set = propertyProviders.entrySet();
      for ( final Entry<String, PropertyProvider> entry : set )
      {
        final String varValue = entry.getValue().getPropertyValue( req );
        computedBindings.add( new BindingProperty( entry.getKey(), varValue ) );
      }
      return computedBindings;
    }
    catch ( final Exception e )
    {
      log( "can not calculate properties for client", e );
      throw new ServletException( "can not calculate properties for client", e );
    }
  }

  public void serveStringManifest( final HttpServletResponse resp, final String manifest )
    throws ServletException
  {
    resp.setHeader( "Cache-Control", "no-cache" );
    resp.setHeader( "Pragma", "no-cache" );
    resp.setDateHeader( "Expires", new Date().getTime() );

    resp.setContentType( "text/cache-manifest" );

    try
    {
      InputStream is = new ByteArrayInputStream( manifest.getBytes( "UTF-8" ) );
      ServletOutputStream os = resp.getOutputStream();
      byte[] buffer = new byte[ 1024 ];
      int bytesRead;

      while ( ( bytesRead = is.read( buffer ) ) != -1 )
      {
        os.write( buffer, 0, bytesRead );
      }
    }
    catch ( UnsupportedEncodingException e )
    {
      log( "can not write manifest to output stream", e );
      throw new ServletException( "can not write manifest to output stream", e );
    }
    catch ( IOException e )
    {
      log( "can not write manifest to output stream", e );
      throw new ServletException( "can not write manifest to output stream", e );
    }
  }

  public String getPermutationStrongName( String baseUrl, String moduleName, Set<BindingProperty> computedBindings )
    throws ServletException
  {
    if ( null == moduleName )
    {
      throw new IllegalArgumentException( "moduleName can not be null" );
    }
    else if ( null == computedBindings )
    {
      throw new IllegalArgumentException( "computedBindings can not be null" );
    }

    final String realPath =
      getServletContext().getRealPath( baseUrl + moduleName + "/" + PermutationMapLinker.MANIFEST_MAP_FILE_NAME );

    try
    {
      final FileInputStream fileInputStream = new FileInputStream( realPath );
      final Map<String, List<BindingProperty>> map = XMLPermutationProvider.getBindingProperties( fileInputStream );
      for ( Entry<String, List<BindingProperty>> entry : map.entrySet() )
      {
        final List<BindingProperty> value = entry.getValue();
        if ( value.containsAll( computedBindings ) && value.size() == computedBindings.size() )
        {
          return entry.getKey();
        }
      }
      return null;
    }
    catch ( final Exception e )
    {
      log( "can not read xml file", e );
      throw new ServletException( "can not read permutation information", e );
    }

  }

  public String getModuleName( final HttpServletRequest request )
    throws ServletException
  {
    if ( null == request )
    {
      throw new IllegalArgumentException( "request can not be null" );
    }

    // request url should be something like .../modulename.manifest" within
    // the same folder of your host page...
    final Pattern pattern = Pattern.compile( "/([a-zA-Z0-9]+)\\.manifest$" );
    final Matcher matcher = pattern.matcher( request.getServletPath() );
    if ( !matcher.find() )
    {
      log( "can not calculate module base from url: '" + request.getServletPath() + "'" );
      throw new ServletException( "can not calculate module base from url: '" + request.getServletPath() + "'" );
    }

    return matcher.group( 1 );
  }
}
