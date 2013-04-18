package footprints.server;

import footprints.server.parse.MethodEntry;
import footprints.server.parse.OutputParser;
import footprints.server.data_type.code_metrics.CollectionDTO;
import footprints.server.data_type.code_metrics.MethodDTO;
import footprints.server.entity.code_metrics.Collection;
import footprints.server.entity.code_metrics.MethodMetric;
import footprints.server.entity.code_metrics.dao.CollectionRepository;
import footprints.server.entity.code_metrics.dao.MethodMetricRepository;
import footprints.server.service.code_metrics.FormatErrorException;
import footprints.server.service.code_metrics.JavaNcss;
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
import org.xml.sax.InputSource;

@Stateless( name = JavaNcss.EJB_NAME )
@TransactionAttribute( TransactionAttributeType.REQUIRED )
public class JavaNcssEJB
  implements JavaNcss
{
  @EJB
  private CollectionRepository _collectionDAO;
  @EJB
  private MethodMetricRepository _methodMetricDAO;

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
      throw new FormatErrorException( "MyFile.xml", 1, "Error parsing the output supplied", e );
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

