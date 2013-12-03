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

public abstract class AbstractManifestServlet
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
    final Date now = new Date();
    // set create date to current timestamp
    resp.setDateHeader( "Date", now.getTime() );
    // set modify date to current timestamp
    resp.setDateHeader( "Last-Modified", now.getTime() );
    // set expiry to back in the past (makes us a bad candidate for caching)
    resp.setDateHeader( "Expires", 0 );
    // HTTP 1.1 (disable caching of any kind)
    // HTTP 1.1 'pre-check=0, post-check=0' => (Internet Explorer should always check)
    resp.setHeader( "Cache-control", "no-cache, no-store, must-revalidate, pre-check=0, post-check=0" );
    resp.setHeader( "Pragma", "no-cache" );

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

  protected String getPermutationStrongName( final String baseUrl,
                                             final String moduleName,
                                             final Set<BindingProperty> computedBindings )
    throws ServletException
  {
    try
    {
      String selectedPermutation = null;
      int selectedMatchStrength = 0;
      final Map<String, List<BindingProperty>> map = getBindingMap( baseUrl, moduleName, computedBindings );
      for ( final Entry<String, List<BindingProperty>> permutationEntry : map.entrySet() )
      {
        int matchStrength = 0;
        boolean matched = true;
        final List<BindingProperty> requiredBindings = permutationEntry.getValue();
        for ( final BindingProperty requirement : requiredBindings )
        {
          boolean propertyMatched = false;
          for ( final BindingProperty candidate : computedBindings )
          {
            if ( requirement.getName().equals( candidate.getName() ) )
            {
              final String[] values = requirement.getValue().split( "," );
              for ( final String value : values )
              {
                if ( value.equals( candidate.getValue() ) )
                {
                  propertyMatched = true;
                  break;
                }
              }
            }
          }
          if ( !propertyMatched )
          {
            matched = false;
            break;
          }
          else
          {
            matchStrength = requiredBindings.size();
          }
        }
        if ( matched && matchStrength > selectedMatchStrength )
        {
          selectedPermutation = permutationEntry.getKey();
          selectedMatchStrength = matchStrength;
        }
      }
      return selectedPermutation;
    }
    catch ( final Exception e )
    {
      throw new ServletException( "can not read permutation information", e );
    }
  }

  private Map<String, List<BindingProperty>> getBindingMap( final String baseUrl,
                                                            final String moduleName,
                                                            final Set<BindingProperty> computedBindings )
    throws Exception
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

    return XMLPermutationProvider.deserialize( new FileInputStream( realPath ) );
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
      throw new ServletException( "can not calculate module base from url: '" + request.getServletPath() + "'" );
    }

    return matcher.group( 1 );
  }
}
