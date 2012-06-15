package footprints.javancsss;


import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.vm.AutoBeanFactorySource;
import footprints.client.data_type.FootprintsFactory;
import footprints.client.data_type.code_metrics.CollectionDTO;
import footprints.client.data_type.code_metrics.MethodDTO;
import java.util.ArrayList;
import java.util.Date;
import org.testng.annotations.Test;

public class AutoProxyTest
{
  @Test
  public void x()
  {
    final FootprintsFactory factory = AutoBeanFactorySource.create( FootprintsFactory.class );
    final AutoBean<CollectionDTO> collection = factory.createCollectionDTO();
    collection.as().setCollectedAt( new Date(  ) );
    collection.as().setMethods( new ArrayList<MethodDTO>(  ) );
    collection.as().setID( 2 );
    final Splittable value = AutoBeanCodex.encode( collection );
    System.out.println( "value.asString() = " + value.getPayload() );
    final AutoBean<CollectionDTO> bean = AutoBeanCodex.decode( factory, CollectionDTO.class, value.getPayload() );
    final CollectionDTO as = bean.as();
    System.out.println( "as = " + as.getCollectedAt() );

  }
}
