package footprints.javancss;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

class TestServer
{

  public static void main( String[] args )
  {
    Connection conn;

    String dburl = "jdbc:postgresql://127.0.0.1:5432/fgis_db";
    String dbuser = "fgis";
    String dbpass = "secret";

    final String dropSQL =
      "drop table jdbc_test";
    final String createSQL =
      "create table jdbc_test (geom geometry, id int4)";
    final String insertPointSQL =
      "insert into jdbc_test values ('POINT (10 10 10)',1)";
    final String insertPolygonSQL =
      "insert into jdbc_test values ('POLYGON ((0 0 0,0 10 0,10 10 0,10 0 0,0 0 0))',2)";

    try
    {

      System.out.println( "Creating JDBC connection..." );
      Class.forName( "org.postgresql.Driver" );
      conn = DriverManager.getConnection( dburl, dbuser, dbpass );
      System.out.println( "Adding geometric type entries..." );

      ( (org.postgresql.PGConnection) conn ).addDataType( "geometry",
                                                          org.postgis.PGgeometry.class );
      ( (org.postgresql.PGConnection) conn ).addDataType( "box3d",
                                                          org.postgis.PGbox3d.class );

      Statement s = conn.createStatement();
      System.out.println( "Creating table with geometric types..." );
      // table might not yet exist
      try
      {
        s.execute( dropSQL );
      }
      catch ( Exception e )
      {
        System.out.println( "Error dropping old table: "
                            + e.getMessage() );
      }
      s.execute( createSQL );
      System.out.println( "Inserting point..." );
      s.execute( insertPointSQL );
      System.out.println( "Inserting polygon..." );
      s.execute( insertPolygonSQL );
      System.out.println( "Done." );
      s = conn.createStatement();
      System.out.println( "Querying table..." );
      ResultSet r = s.executeQuery( "select st_asText(geom),id from jdbc_test" );
      while ( r.next() )
      {
        Object obj = r.getObject( 1 );
        int id = r.getInt( 2 );
        System.out.println( "Row " + id + ":" );
        System.out.println( obj.toString() );
      }
      s.close();
      conn.close();
    }
    catch ( Exception e )
    {
      System.err.println( "Aborted due to error:" );
      e.printStackTrace();
      System.exit( 1 );
    }
  }
}
