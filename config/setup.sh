#!/bin/sh

#
# You can configure the local database using environment variables. It is typical
# to add a script in config/local.sh that defines the environment variables similar
# to the following;
#
# export DB_SERVER_HOST=127.0.0.1
# export DB_SERVER_PORT=1500
# export DB_SERVER_INSTANCE=SQLEXPRESS
#

if [ "$DB_TYPE" == '' ] || [ "$DB_TYPE" == 'postgres' ]; then
  DB_SERVER_HOST=${DB_SERVER_HOST:-127.0.0.1}
  DB_SERVER_PORT=${DB_SERVER_PORT:-5432}
  DB_PROPS_PREFIX="ServerName=${DB_SERVER_HOST}:User=${USER}:Password=letmein:PortNumber=${DB_SERVER_PORT}:DatabaseName="
  DB_DRIVER=org.postgresql.ds.PGSimpleDataSource
fi

if [ "$DB_TYPE" == 'mssql' ]; then
  DB_SERVER_HOST=${DB_SERVER_HOST:-DEVsql02.fire.dse.vic.gov.au}
  DB_SERVER_PORT=${DB_SERVER_PORT:-2222}
  DB_SERVER_INSTANCE=${DB_SERVER_INSTANCE:-mssql01}
  DB_PROPS_PREFIX="Instance=${DB_SERVER_INSTANCE}:ServerName=${DB_SERVER_HOST}:User=stock-dev:Password=letmein:PortNumber=${DB_SERVER_PORT}:DatabaseName="
  DB_DRIVER=net.sourceforge.jtds.jdbcx.JtdsDataSource
fi

CREATED_DOMAIN=false
STOP_DOMAIN=false

R=`(asadmin list-domains | grep -q 'footprints ') && echo yes`
if [ "$R" != 'yes' ]; then
  asadmin create-domain --user admin --nopassword footprints
  CREATED_DOMAIN=true
fi

R=`(asadmin list-domains | grep -q 'footprints running') && echo yes`
if [ "$R" != 'yes' ]; then
  STOP_DOMAIN=true
  asadmin start-domain footprints
  if [ "$CREATED_DOMAIN" == 'true' ]; then
    asadmin delete-jvm-options -XX\\:MaxPermSize=192m
    asadmin delete-jvm-options -Xmx512m
    asadmin create-jvm-options -XX\\:MaxPermSize=400m
    asadmin create-jvm-options -Xmx1500m
    asadmin create-jvm-options -Dcom.sun.enterprise.tools.admingui.NO_NETWORK=true
    asadmin restart-domain footprints
  fi
fi

if [ "$DB_TYPE" == 'mssql' ]; then
  R=`(asadmin list-libraries | grep -q jtds-1.3.1.jar) && echo yes`
  if [ "$R" != 'yes' ]; then
    asadmin add-library ~/.m2/repository/net/sourceforge/jtds/jtds/1.3.1/jtds-1.3.1.jar
    asadmin restart-domain footprints
  fi
fi

if [ "$DB_TYPE" == '' ] || [ "$DB_TYPE" == 'postgres' ]; then
  R=`(asadmin list-libraries | grep -q postgresql-9.1-901.jdbc4.jar) && echo yes`
  if [ "$R" != 'yes' ]; then
    asadmin add-library ~/.m2/repository/postgresql/postgresql/9.1-901.jdbc4/postgresql-9.1-901.jdbc4.jar
    asadmin restart-domain footprints
  fi
fi

asadmin delete-jdbc-resource jdbc/Footprints
asadmin delete-jdbc-connection-pool FootprintsConnectionPool

asadmin create-jdbc-connection-pool --datasourceclassname ${DB_DRIVER} --restype javax.sql.DataSource --isconnectvalidatereq=true --validationmethod auto-commit --ping true --description "Footprints Connection Pool" --property "${DB_PROPS_PREFIX}${USER}_CALENDAR_DEV" FootprintsConnectionPool

if [ "$DB_TYPE" == '' ] || [ "$DB_TYPE" == 'postgres' ]; then
  asadmin set domain.resources.jdbc-connection-pool.FootprintsPool.property.JDBC30DataSource=true
fi

asadmin set-log-levels javax.enterprise.resource.resourceadapter.com.sun.gjc.spi=WARNING

if [ "$STOP_DOMAIN" == 'true' ]; then
  asadmin stop-domain footprints
fi
