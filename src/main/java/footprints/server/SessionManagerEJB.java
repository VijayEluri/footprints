package footprints.server;

import java.util.UUID;
import javax.annotation.Nonnull;
import javax.ejb.Local;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import org.realityforge.ssf.InMemorySessionManager;
import org.realityforge.ssf.SessionInfo;
import org.realityforge.ssf.SessionManager;
import org.realityforge.ssf.SimpleSessionInfo;

@Singleton
@Startup
@Local( SessionManager.class )
public class SessionManagerEJB
  extends InMemorySessionManager
{
  @Nonnull
  @Override
  protected SessionInfo newSessionInfo()
  {
    return new SimpleSessionInfo( UUID.randomUUID().toString() );
  }
}
