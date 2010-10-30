package footprints.javancss;

import footprints.javancss.model.MethodMetric;
import footprints.javancss.model.SchemaEntityManager;
import java.io.File;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Properties;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Main
{
  public static void main( String[] args )
    throws Exception
  {
    final long start = System.currentTimeMillis();
    final File inputFile = new File( args[ 0 ] );
    final Collection<MethodEntry> entries = new OuputParser().parse( inputFile );
    System.out.println( "ParseTime: " + ( System.currentTimeMillis() - start ) + "ms" );
    for ( final MethodEntry entry : entries )
    {
      //System.out.println( entry );
    }

    final EntityManagerFactory factory = Persistence.createEntityManagerFactory( "footprints", createOverrides() );

    final EntityManager em = factory.createEntityManager();
    System.exit( 0 );
    SchemaEntityManager.bind( em );
    try
    {
      em.getTransaction().begin();
      final footprints.javancss.model.Collection collection = new footprints.javancss.model.Collection();
      collection.setCollectedAt( new Timestamp( System.currentTimeMillis() ) );
      em.persist( collection );

      for ( final MethodEntry entry : entries )
      {
        final MethodMetric metric = new MethodMetric();
        metric.setCollection( collection );
        metric.setCCN( entry.ccn );
        metric.setJVDC( entry.jvdc );
        metric.setNCSS( entry.ncss );
        metric.setPackageName( entry.packageName );
        metric.setClassName( entry.className );
        metric.setMethodName( entry.methodName );
        em.persist( metric );
      }

      em.getTransaction().commit();
    }
    finally
    {
      SchemaEntityManager.unbind( em );
      em.close();
    }
  }

  private static Properties createOverrides()
  {
    final Properties overrides = new Properties();
    overrides.setProperty( "javax.persistence.jdbc.driver", "net.sourceforge.jtds.jdbc.Driver" );
    overrides.setProperty( "javax.persistence.jdbc.url", "jdbc:jtds:sqlserver://PETER-PC/PD42_FOOTPRINTS_DEV;instance=SQLEXPRESS" );

    overrides.setProperty( "eclipselink.logging.level", "FINEST" );
    //overrides.setProperty( "eclipselink.jpa.uppercase-column-names", "false" );
    //overrides.setProperty( "eclipselink.target-database", "SQLServer" );

    return overrides;
  }
}
