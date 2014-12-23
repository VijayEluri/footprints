workspace_dir = File.expand_path(File.dirname(__FILE__) + '/..')
$LOAD_PATH.unshift(File.expand_path("#{workspace_dir}/vendor/plugins/domgen/lib"))
$LOAD_PATH.unshift(File.expand_path("#{workspace_dir}/vendor/plugins/dbt/lib"))

require 'dbt'
require 'domgen'

Dbt::Config.environment = ENV['DB_ENV'] if ENV['DB_ENV']
Dbt::Config.driver = 'postgres'
Dbt::Config.config_filename = 'config/pg_database.yml'

Domgen::Build.define_load_task

Domgen::Build.define_generate_task([:pgsql], :key => :sql, :target_dir => 'database/generated')

Dbt.add_database(:default,
                 :imports => {:default => {:modules => [:CodeMetrics]}}) do |database|
  database.search_dirs = %w(database/generated database)
  database.enable_domgen
  database.version = '1'
end
