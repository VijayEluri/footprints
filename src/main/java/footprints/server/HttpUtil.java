package footprints.server;

import javax.annotation.Nullable;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public final class HttpUtil
{
  private HttpUtil()
  {
  }

  public static StringBuffer getContextURL( final HttpServletRequest request )
  {
    final StringBuffer url = new StringBuffer();
    final String scheme = request.getScheme();
    final int port = request.getServerPort();

    url.append( scheme ); // http, https
    url.append( "://" );
    url.append( request.getServerName() );
    if ( ( "http".equals( scheme ) && 80 != port ) || ( "https".equals( scheme ) && 443 != port ) )
    {
      url.append( ':' );
      url.append( request.getServerPort() );
    }
    url.append( request.getContextPath() );

    return url;
  }

  @Nullable
  public static Cookie findCookie( final HttpServletRequest request, final String cookieName )
  {
    final Cookie[] cookies = request.getCookies();
    if ( null != cookies )
    {
      for ( final Cookie cookie : cookies )
      {
        if ( cookie.getName().equals( cookieName ) )
        {
          return cookie;
        }
      }
    }
    return null;
  }
}
