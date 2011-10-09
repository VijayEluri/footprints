package footprints.javancss.imit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface EntityRepository
{
  <T> void registerEntity( Class<T> type, Object id, T entity );

  @Nonnull
  <T> T deregisterEntity( Class<T> type, Object id );

  @Nonnull
  <T> T getByID( Class<T> type, Object id );

  @Nullable
  <T> T findByID( Class<T> type, Object id );
}
