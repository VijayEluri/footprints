def workspace_dir
  File.expand_path(File.dirname(__FILE__) + '/..')
end

def is_postgres?
  ENV['DB_TYPE'] == 'postgres'
end

def is_mssql?
  ENV['DB_TYPE'].nil? || ENV['DB_TYPE'] == 'mssql'
end

$LOAD_PATH.insert(0, "#{workspace_dir}/../dbt/lib")

require 'dbt'

Dbt::Config.environment = ENV['DB_ENV'] if ENV['DB_ENV']

if is_postgres?
  Dbt::Config.driver = 'Postgres'
  Dbt::Config.config_filename = File.expand_path("#{workspace_dir}/config/pg_database.yml")
elsif is_mssql?
  Dbt::Config.driver = 'Mssql'
  Dbt::Config.config_filename = File.expand_path("#{workspace_dir}/config/mssql_database.yml")
else
  raise "Unknown DB_TYPE = #{ENV['DB_TYPE']}"
end

def define_dbt_tasks(project)
  Dbt.database_for_key(:default).version = project.version
end

Dbt.add_database(:default,
                 :imports => {:default => {}},
                 :backup => true,
                 :restore => true) do |database|
  database.search_dirs = ["#{workspace_dir}/databases/generated", "#{workspace_dir}/databases"]
  database.enable_domgen(:Footprints, 'domgen:load', 'domgen:sql')
  database.add_import_assert_filters
  database.enable_import_task_as_part_of_create = false
  database.enable_separate_import_task = false
end
