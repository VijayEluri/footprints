workspace_dir = File.expand_path(File.dirname(__FILE__) + "/..")

$LOAD_PATH.unshift(File.expand_path("#{workspace_dir}/../domgen/lib"))

require 'domgen'

Domgen::LoadSchema.new("#{workspace_dir}/architecture.rb")

generators = nil
if is_postgres?
  generators = [:pgsql]
  Domgen::Sql.dialect = Domgen::Sql::PgDialect
else
  generators = [:mssql]
  Domgen::Sql.dialect = Domgen::Sql::MssqlDialect
end

Domgen::GenerateTask.new(:Footprints, "sql", generators, "#{workspace_dir}/database/generated") do |t|
  t.verbose = !!ENV['DEBUG_DOMGEN']
end
