package footprints.server;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ejb.Local;

@Local
public interface SessionManager
{
  @Nonnull
  String getSessionKey();

  @Nullable
  SessionInfo getSession( @Nonnull String sessionID );

  @Nonnull
  SessionInfo createSession( @Nonnull String username );
}
