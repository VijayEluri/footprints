package footprints.server;

import footprints.server.data_type.code_metrics.CollectionDTO;
import footprints.server.service.code_metrics.FormatErrorException;
import footprints.server.service.code_metrics.JavaNcss;
import footprints.server.service.code_metrics.JavaNcssRestService;
import java.util.List;
import javax.annotation.Nonnull;
import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class RestServiceImpl
  implements JavaNcssRestService
{
  @EJB
  private JavaNcss _service;

  @Override
  public void uploadJavaNcssOutput( @Nonnull final String content )
    throws FormatErrorException
  {
    _service.uploadJavaNcssOutput( content );
  }

  @Nonnull
  @Override
  public List<CollectionDTO> getCollections()
  {
    return _service.getCollections();
  }

  @Nonnull
  @Override
  public CollectionDTO getCollection( final int id )
  {
    return _service.getCollection( id );
  }
}
