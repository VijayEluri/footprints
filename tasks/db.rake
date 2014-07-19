def workspace_dir
  File.expand_path(File.dirname(__FILE__) + '/..')
end

def is_postgres?
  ENV['DB_TYPE'].nil? || ENV['DB_TYPE'] == 'postgres'
end

def is_mssql?
  ENV['DB_TYPE'] == 'mssql'
end

$LOAD_PATH.unshift(File.expand_path("#{workspace_dir}/vendor/plugins/dbt/lib"))
$LOAD_PATH.unshift(File.expand_path("#{workspace_dir}/vendor/plugins/domgen/lib"))

require 'dbt'
require 'domgen'

Domgen::LoadSchema.new("#{workspace_dir}/architecture.rb")
Domgen::GenerateTask.new(:Footprints, 'sql', (is_postgres? ? [:pgsql] : [:mssql]), "#{workspace_dir}/database/generated")

Dbt::Config.environment = ENV['DB_ENV'] if ENV['DB_ENV']

if is_postgres?
  Domgen::Sql.dialect = Domgen::Sql::PgDialect
  Dbt::Config.driver = 'postgres'
  Dbt::Config.config_filename = File.expand_path("#{workspace_dir}/config/pg_database.yml")
elsif is_mssql?
  Domgen::Sql.dialect = Domgen::Sql::MssqlDialect
  Dbt::Config.driver = 'sql_server'
  Dbt::Config.config_filename = File.expand_path("#{workspace_dir}/config/mssql_database.yml")
else
  raise "Unknown DB_TYPE = #{ENV['DB_TYPE']}"
end

Dbt.add_database(:default,
                 :imports => {:default => {:modules => [:CodeMetrics]}}) do |database|
  database.search_dirs = ["#{workspace_dir}/database/generated", "#{workspace_dir}/database"]
  database.enable_domgen(:Footprints, 'domgen:load', 'domgen:sql')
  database.version = '1'
end
