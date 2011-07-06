package footprints.javancss;

import footprints.javancss.model.MethodMetric;
import java.io.StringReader;
import java.sql.Timestamp;
import java.util.Collection;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.xml.sax.InputSource;

@Stateless
@WebService
@TransactionAttribute( TransactionAttributeType.REQUIRED )
public class JavaNcssEJB
{
  @PersistenceContext( unitName = "footprints" )
  private EntityManager em;

  public void uploadJavaNcssOutput( final String project,
                                    final String branch,
                                    final String version,
                                    final String output )
    throws Exception
  {
    final Collection<MethodEntry> entries;
    try
    {
      entries = new OutputParser().parse( new InputSource( new StringReader( output ) ) );
    }
    catch ( final Exception e )
    {
      throw new Exception( "Error parsing the output supplied", e );
    }
    saveStatistics( project, branch, version, entries );
  }

  private void saveStatistics( final String project,
                               final String branch,
                               final String version,
                               final Collection<MethodEntry> entries )
  {

    final footprints.javancss.model.Collection collection =
      new footprints.javancss.model.Collection( new Timestamp( System.currentTimeMillis() ) );
    em.persist( collection );

    for ( final MethodEntry entry : entries )
    {
      final MethodMetric metric =
        new MethodMetric( collection,
                          entry.packageName,
                          entry.className,
                          entry.methodName,
                          entry.ncss,
                          entry.ccn,
                          entry.jvdc );
      try
      {
        em.persist( metric );
      }
      catch ( final Throwable e )
      {
        throw new IllegalStateException( "Attempting to persist " + metric );
      }
    }
  }
}

