package footprints.javancss.imit;

public interface EntityRepository
{
  <T> T registerEntity( Class<T> type, Object id, Object entity );

  <T> T findByID( Class<T> type, Object id );
}
