workspace_dir = File.expand_path(File.dirname(__FILE__) + '/..')
iris_dir = File.expand_path("#{workspace_dir}/../iris")

# TODO: Remove this cruft once dbt is AR free
$LOAD_PATH.insert(0, "#{iris_dir}/vendor/rails-2.2.2/activesupport/lib")
$LOAD_PATH.insert(0, "#{iris_dir}/vendor/rails-2.2.2/activerecord/lib")
$LOAD_PATH.insert(0, "#{iris_dir}/vendor/gems/activerecord-jdbc-adapter-1.0.0/lib")

$LOAD_PATH.insert(0, "#{workspace_dir}/vendor/plugins/dbt/lib")

require 'db_tasks'

DbTasks::Config.environment = ENV['DB_ENV'] if ENV['DB_ENV']
DbTasks::Config.config_filename = File.expand_path("#{workspace_dir}/config/database.yml")
DbTasks::Config.log_filename = "#{workspace_dir}/target/dbt/logs/db.log"
DbTasks::Config.search_dirs = ["#{workspace_dir}/databases/generated", "#{workspace_dir}/databases" ]
DbTasks.add_database_driver_hook { db_driver_setup }
DbTasks.add_database :default, [:JavaNCSS]
DbTasks.define_table_order_resolver do |schema_key|
  Domgen.schema_set_by_name(:footprints).schema_by_name(schema_key.to_s).
    object_types.select{|object_type| !object_type.abstract?}.collect do |object_type|
    sql_schema = object_type.schema.sql.default_schema? ? '' : "#{object_type.schema.sql.schema}."
    "#{sql_schema}#{object_type.sql.table_name}"
  end
end

def db_driver_setup
  DbTasks::Config.app_version = Buildr.project("footprints").version
  load_jtds
  db_date_setup
end

def load_jtds
  require File.expand_path("#{File.dirname(__FILE__)}/../../iris/vendor/jars/repository/net/sourceforge/jtds/jtds/1.2.4/jtds-1.2.4.jar")
end

def db_date_setup
  ::ActiveSupport::CoreExtensions::Time::Conversions::DATE_FORMATS[:default] = '%d %b %Y'
  ::ActiveSupport::CoreExtensions::Date::Conversions::DATE_FORMATS[:default] = '%d %b %Y'

  # Futz with date formats to ensure that we don't get month/day mixup
  ::ActiveSupport::CoreExtensions::Date::Conversions::DATE_FORMATS.merge!(:db => '%d %b %Y')
  ::ActiveSupport::CoreExtensions::Time::Conversions::DATE_FORMATS.merge!(:db => '%d %b %Y %H:%M:%S')
end

task 'dbt:pre_build' => 'domgen:sql'
