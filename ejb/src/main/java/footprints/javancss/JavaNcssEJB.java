package footprints.javancss;

import footprints.javancss.model.Collection;
import footprints.javancss.model.MethodMetric;
import footprints.javancss.model.dao.CollectionDAO;
import footprints.javancss.model.dao.MethodMetricDAO;
import footprints.javancss.parse.MethodEntry;
import footprints.javancss.parse.OutputParser;
import footprints.javancss.service.FormatErrorException;
import footprints.javancss.service.JavaNcss;
import java.io.StringReader;
import java.sql.Timestamp;
import java.util.LinkedList;
import javax.annotation.Nonnull;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jws.WebService;
import org.xml.sax.InputSource;

@Stateless( name = JavaNcss.EJB_NAME )
@WebService
@TransactionAttribute( TransactionAttributeType.REQUIRED )
public class JavaNcssEJB
    implements JavaNcss
{
  @EJB
  private CollectionDAO _collectionDAO;
  @EJB
  private MethodMetricDAO _methodMetricDAO;

  public void uploadJavaNcssOutput( @Nonnull final String project,
                                    @Nonnull final String branch,
                                    @Nonnull final String version,
                                    @Nonnull final String output )
      throws FormatErrorException
  {
    saveStatistics( parseOutput( output ) );
  }

  private LinkedList<MethodEntry> parseOutput( final String output )
      throws FormatErrorException
  {
    try
    {
      return new OutputParser().parse( new InputSource( new StringReader( output ) ) );
    }
    catch( final Exception e )
    {
      throw new FormatErrorException( "Error parsing the output supplied", e );
    }
  }

  private void saveStatistics( final LinkedList<MethodEntry> entries )
  {

    final Collection collection = new Collection( new Timestamp( System.currentTimeMillis() ) );
    _collectionDAO.persist( collection );

    for( final MethodEntry entry : entries )
    {
      final MethodMetric metric =
          new MethodMetric( collection,
                            entry.getPackageName(),
                            entry.getClassName(),
                            entry.getMethodName(),
                            entry.getNcss(),
                            entry.getCcn(),
                            entry.getJvdc() );
      _methodMetricDAO.persist( metric );
    }
  }
}

