package footprints.client.ioc;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.name.Names;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import footprints.client.GlobalAsyncCallback;
import footprints.client.SimpleUI;
import footprints.client.services.FootprintsEntityChangeBroker;
import org.realityforge.replicant.client.EntityChangeBroker;
import org.realityforge.replicant.client.EntityRepository;
import org.realityforge.replicant.client.EntityRepositoryImpl;

public class BasicModule
  extends AbstractGinModule
{
  @Override
  protected void configure()
  {
    bindNamedService( "GLOBAL", AsyncCallback.class, GlobalAsyncCallback.class );
    bind( EntityRepository.class ).to( EntityRepositoryImpl.class ).asEagerSingleton();
    bind( EntityChangeBroker.class ).to( FootprintsEntityChangeBroker.class ).asEagerSingleton();
    bind( SimpleUI.class ).asEagerSingleton();
    bind( EventBus.class ).to( SimpleEventBus.class ).asEagerSingleton();
  }

  private <T> void bindNamedService( final String name,
                                     final Class<T> service,
                                     final Class<? extends T> implementation )
  {
    bind( service ).annotatedWith( Names.named( name ) ).to( implementation ).asEagerSingleton();
  }
}
