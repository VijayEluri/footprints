package footprints.server;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Stateless;

@Stateless
public class SessionManagerImpl
  implements SessionManager, Serializable
{
  private final Map<String, SessionInfo> _sessions =
    Collections.synchronizedMap( new HashMap<String, SessionInfo>() );

  @Override
  @Nonnull
  public String getSessionKey()
  {
    return "fpskey";
  }

  @Override
  public boolean invalidateSession( @Nonnull final String sessionID )
  {
    return null != _sessions.remove( sessionID );
  }

  @Override
  @Nullable
  public SessionInfo getSession( @Nonnull final String sessionID )
  {
    return _sessions.get( sessionID );
  }

  @Override
  @Nonnull
  public SessionInfo createSession( @Nonnull final String username )
  {
    final String sessionID = UUID.randomUUID().toString();
    final SessionInfo sessionInfo = new SessionInfo( sessionID, username );
    _sessions.put( sessionID, sessionInfo );
    return sessionInfo;
  }
}
