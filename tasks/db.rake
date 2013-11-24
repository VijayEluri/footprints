def workspace_dir
  File.expand_path(File.dirname(__FILE__) + '/..')
end

def is_postgres?
  ENV['DB_TYPE'].nil? || ENV['DB_TYPE'] == 'postgres'
end

def is_mssql?
  ENV['DB_TYPE'] == 'mssql'
end

$LOAD_PATH.insert(0, "#{workspace_dir}/../dbt/lib")

require 'dbt'

Dbt::Config.environment = ENV['DB_ENV'] if ENV['DB_ENV']

if is_postgres?
  #Dbt::Config.driver = 'Postgres'
  Dbt::Config.driver = 'postgres'
  Dbt::Config.config_filename = File.expand_path("#{workspace_dir}/config/pg_database.yml")
elsif is_mssql?
  Dbt::Config.driver = 'sql_server'
  Dbt::Config.config_filename = File.expand_path("#{workspace_dir}/config/mssql_database.yml")
else
  raise "Unknown DB_TYPE = #{ENV['DB_TYPE']}"
end

def define_dbt_tasks(project)
  Dbt.database_for_key(:default).version = project.version
end

Dbt.add_database(:default,
                 :imports => {:default => {:modules => [:CodeMetrics]}}) do |database|
  database.search_dirs = ["#{workspace_dir}/database/generated", "#{workspace_dir}/database"]
  database.enable_domgen(:Footprints, 'domgen:load', 'domgen:sql')
end
