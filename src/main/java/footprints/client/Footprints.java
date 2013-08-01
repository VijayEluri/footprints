package footprints.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;
import footprints.client.data_type.code_metrics.CollectionDTO;
import footprints.client.service.code_metrics.JavaNcssRestService;
import java.util.List;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.Resource;
import org.fusesource.restygwt.client.RestServiceProxy;

public final class Footprints
  implements EntryPoint
{
  public void onModuleLoad()
  {

    final Button button = new Button( "Place Pizza Order" );
    button.addClickHandler( new ClickHandler()
    {
      public void onClick( ClickEvent event )
      {
        Window.alert( "Order Placed" );
        //        JavaNcssRestService service = GWT.create( JavaNcssRestService.class );
        //Resource resource = new Resource( GWT.getModuleBaseURL() + "/api" + JavaNcssRestService.PATH );
        //((RestServiceProxy) service).setResource(resource);
        //service.getCollections( new MethodCallback<List<CollectionDTO>>()
        //{
        //  @Override
        //  public void onFailure( final Method method, final Throwable throwable )
        //  {
        //    Window.alert( "Failed" );
        //  }
        //
        //  @Override
        //  public void onSuccess( final Method method, final List<CollectionDTO> collectionDTOs )
        //  {
        //    Window.alert( "Success" );
        //    final MethodDTO dto = AutoBeanFactorySource.create( FootprintsFactory.class ).createMethodDTO().as();
        //    dto.setMethodName( "" );
        //
        //  }
        //} );
      }

    } );
    RootPanel.get().add( button );
  }
}
