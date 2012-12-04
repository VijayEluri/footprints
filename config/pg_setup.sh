asadmin delete-jdbc-resource jdbc/FootprintsDS
asadmin delete-jdbc-connection-pool FootprintsSQL

asadmin create-jdbc-connection-pool\
  --datasourceclassname org.postgresql.ds.PGSimpleDataSource\
  --restype javax.sql.DataSource\
  --isconnectvalidatereq=true\
  --validationmethod auto-commit\
  --ping true\
  --description "Footprints Connection Pool"\
  --property "ServerName=127.0.0.1:User=stock-dev:Password=letmein:PortNumber=5432:DatabaseName=${USER}_Footprints_DEV" FootprintsSQL
asadmin create-jdbc-resource --connectionpoolid FootprintsSQL jdbc/FootprintsDS

asadmin set domain.resources.jdbc-connection-pool.FootprintsSQL.property.JDBC30DataSource=true