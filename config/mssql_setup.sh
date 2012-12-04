asadmin delete-jdbc-resource jdbc/FootprintsDS
asadmin delete-jdbc-connection-pool FootprintsSQL

asadmin create-jdbc-connection-pool\
  --datasourceclassname net.sourceforge.jtds.jdbcx.JtdsDataSource\
  --restype javax.sql.DataSource\
  --isconnectvalidatereq=true\
  --validationmethod auto-commit\
  --ping true\
  --description "Footprints Connection Pool"\
  --property "Instance=mssql01:ServerName=DEVsql02.fire.dse.vic.gov.au:User=stock-dev:Password=letmein:PortNumber=1433:DatabaseName=${USER}_Footprints_DEV" FootprintsSQL
asadmin create-jdbc-resource --connectionpoolid FootprintsSQL jdbc/FootprintsDS
