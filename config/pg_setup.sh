#!/bin/bash

R=`(asadmin list-libraries | grep -q postgis-jdbc-2.0.2SVN.jar) && echo yes`
if [ "$R" != 'yes' ]; then
  asadmin add-library ~/.m2/repository/org/postgis/postgis-jdbc/2.0.2SVN/postgis-jdbc-2.0.2SVN.jar
fi

R=`(asadmin list-libraries | grep postgresql-9.1-901.jdbc4.jar) && echo yes`
if [ "$R" != 'yes' ]; then
  asadmin add-library ~/.m2/repository/postgresql/postgresql/9.1-901.jdbc4/postgresql-9.1-901.jdbc4.jar
fi

asadmin delete-jdbc-resource jdbc/FootprintsDS
asadmin delete-jdbc-connection-pool FootprintsSQL

asadmin create-jdbc-connection-pool\
  --datasourceclassname org.postgresql.ds.PGSimpleDataSource\
  --restype javax.sql.DataSource\
  --isconnectvalidatereq=true\
  --validationmethod auto-commit\
  --ping true\
  --description "Footprints Connection Pool"\
  --property "ServerName=127.0.0.1:User=${USER}:Password=letmein:PortNumber=5432:DatabaseName=${USER}_Footprints_DEV" FootprintsSQL
asadmin create-jdbc-resource --connectionpoolid FootprintsSQL jdbc/FootprintsDS

asadmin set domain.resources.jdbc-connection-pool.FootprintsSQL.property.JDBC30DataSource=true