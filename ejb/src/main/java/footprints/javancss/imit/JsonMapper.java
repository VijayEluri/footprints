package footprints.javancss.imit;

import org.json.JSONObject;

public interface JsonMapper
{
  void create( Object id, String type, JSONObject object )
      throws org.json.JSONException;

  void update( Object id, String type, JSONObject object )
      throws org.json.JSONException;

  void delete( Object id, String type );
}
