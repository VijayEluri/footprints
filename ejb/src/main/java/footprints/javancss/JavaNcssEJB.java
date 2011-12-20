package footprints.javancss;

import code_metrics.CollectionDTO;
import code_metrics.MethodDTO;
import footprints.javancss.model.Collection;
import footprints.javancss.model.MethodMetric;
import footprints.javancss.model.dao.CollectionDAO;
import footprints.javancss.model.dao.MethodMetricDAO;
import footprints.javancss.parse.MethodEntry;
import footprints.javancss.parse.OutputParser;
import footprints.javancss.service.FormatErrorException;
import footprints.javancss.service.JavaNcss;
import footprints.javancss.service.JavaNcssWS;
import java.io.StringReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.xml.sax.InputSource;

@Stateless( name = JavaNcss.EJB_NAME )
@TransactionAttribute( TransactionAttributeType.REQUIRED )
@WebService( endpointInterface = JavaNcssWS.SERVICE_NAME )
public class JavaNcssEJB
  implements JavaNcss, JavaNcssWS
{
  @EJB
  private CollectionDAO _collectionDAO;
  @EJB
  private MethodMetricDAO _methodMetricDAO;

  public void uploadJavaNcssOutput( @Nonnull final String output )
    throws FormatErrorException
  {
    saveStatistics( parseOutput( output ) );
  }

  @Override
  @Nonnull
  public List<CollectionDTO> getCollections()
  {
    final ArrayList<CollectionDTO> collections = new ArrayList<CollectionDTO>();
    for( final Collection collection : _collectionDAO.findAll() )
    {
      collections.add( toCollection( collection ) );
    }
    return collections;
  }

  @Override
  @Nonnull
  public CollectionDTO getCollection( final int id )
  {
    final Collection collection = _collectionDAO.getByID( id );
    return toCollection( collection );
  }

  private CollectionDTO toCollection( final Collection collection )
  {
    final ArrayList<MethodDTO> methods = new ArrayList<MethodDTO>();
    for( final MethodMetric methodMetric : collection.getMethodMetrics() )
    {
      final MethodDTO dto =
        new MethodDTO( methodMetric.getPackageName(),
                       methodMetric.getClassName(),
                       methodMetric.getMethodName(),
                       methodMetric.getNCSS(),
                       methodMetric.getCCN(),
                       methodMetric.getJVDC() );
      methods.add( dto );
    }

    return new CollectionDTO( collection.getID(), collection.getCollectedAt(), methods );
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

