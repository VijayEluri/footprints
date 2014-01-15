package footprints.server;

import java.io.IOException;
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
      Cookie cookie = HttpUtil.findCookie( request, _sessionManager.getSessionKey() );
      if ( !( null != cookie && null != _sessionManager.getSession( cookie.getValue() ) ) )
      {
        if( null != cookie )
        {
          cookie.setMaxAge( 0 );
          response.addCookie( cookie );
        }
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

  protected abstract boolean isResourceProtected( final String method, final String requestURI );

  private void removeContainerSession( final HttpServletRequest request )
  {
    final HttpSession session = request.getSession();
    if ( null != session )
    {
      session.invalidate();
    }
  }
}
