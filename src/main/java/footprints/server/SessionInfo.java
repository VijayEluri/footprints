package footprints.server;

import javax.annotation.Nonnull;

public class SessionInfo
{
  private final String _sessionID;
  private final String _username;

  public SessionInfo( @Nonnull final String sessionID, @Nonnull final String username )
  {
    _sessionID = sessionID;
    _username = username;
  }

  @Nonnull
  public String getSessionID()
  {
    return _sessionID;
  }

  @Nonnull
  public String getUsername()
  {
    return _username;
  }
}
