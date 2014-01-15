package footprints.server;

import java.io.IOException;
import javax.annotation.Nullable;
import javax.ejb.EJB;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public abstract class AbstractSessionFilter
  extends AbstractHttpFilter
{
  @EJB
  private SessionManager _sessionManager;

  protected final void doFilter( final HttpServletRequest request,
                                 final HttpServletResponse response,
                                 final FilterChain chain )
    throws IOException, ServletException
  {
    final String requestURI = request.getRequestURI();
    final String contextPath = request.getServletContext().getContextPath();
    final String localPath = requestURI.substring( contextPath.length() );
    if ( isResourceProtected( request.getMethod(), localPath ) )
    {
      Cookie cookie = findCookie( request, _sessionManager.getSessionKey() );
      if ( !( null != cookie && null != _sessionManager.getSession( cookie.getValue() ) ) )
      {
        handleInvalid( request, response, chain );
        return;
      }
    }

    removeContainerSession( request );

    chain.doFilter( request, response );
  }

  protected abstract void handleInvalid( final HttpServletRequest request,
                                         final HttpServletResponse response,
                                         final FilterChain chain )
    throws IOException, ServletException;

  protected final StringBuffer getContextURL( final HttpServletRequest request )
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

  protected abstract boolean isResourceProtected( final String method, final String requestURI );

  private void removeContainerSession( final HttpServletRequest request )
  {
    final HttpSession session = request.getSession();
    if ( null != session )
    {
      session.invalidate();
    }
  }

  @Nullable
  protected final Cookie findCookie( final HttpServletRequest request, final String cookieName )
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
