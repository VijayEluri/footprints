package footprints.server;

import footprints.server.data_type.code_metrics.CollectionDTO;
import footprints.server.service.code_metrics.FormatErrorException;
import footprints.server.service.code_metrics.JavaNcss;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class RestServiceImpl
  implements RestService
{
  @EJB
  private JavaNcss _service;

  @Override
  public void uploadCollection( final String content )
    throws FormatErrorException
  {
    _service.uploadJavaNcssOutput( content );
  }

  @Override
  public List<CollectionDTO> getCollections()
  {
    return _service.getCollections();
  }

  @Override
  public CollectionDTO getCollection( final int id )
  {
    return _service.getCollection( id );
  }
}
