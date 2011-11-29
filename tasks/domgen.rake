workspace_dir = File.expand_path(File.dirname(__FILE__) + "/..")

$LOAD_PATH.unshift(File.expand_path("#{workspace_dir}/../domgen/lib"))

require 'domgen'

Domgen::LoadSchema.new("#{workspace_dir}/databases/schema_set.rb")

generators = nil
if is_postgres?
  generators = [:pgsql]
  Domgen::Sql.dialect = Domgen::Sql::PgDialect
else
  generators = [:mssql]
  Domgen::Sql.dialect = Domgen::Sql::MssqlDialect
end

Domgen::GenerateTask.new(:Footprints, "sql", generators, "#{workspace_dir}/databases/generated")
Domgen::Xmi::GenerateXMITask.new(:Footprints, "xmi", "#{workspace_dir}/target/xmi/footprints.xmi")
Domgen::GenerateTask.new(:Footprints, "active_record", [:active_record], "#{workspace_dir}/target/generated/ruby") do |t|
  t.description = 'Generates the ActiveRecord code for the persistent objects'
end

def define_persistence_unit(project, repository_key, classfile = nil)
  base_generated_dir = project._(:target, :generated, "main/domgen")

  task = Domgen::GenerateTask.new(repository_key, "server", [:jaxb_model, :jpa_model, :jpa_ejb, :ejb, :ejb_facades, :imit, :imit_json, :imit_jpa, :gwt_client_service, :gwt_client_service_test, :imit_gwt_proxy, :imit_gwt_proxy_service_test, :gwt_shared_service, :gwt_server_service], base_generated_dir) do |t|
    t.description = 'Generates the Java code for the persistent objects'
  end

  project.compile.enhance [task.task_name]
  project.compile.from "#{base_generated_dir}/java"
  project.iml.main_source_directories << "#{base_generated_dir}/resources"

  project.compile.with ::HIBERNATE, :intellij_annotations, :ejb_api
  project.package(:jar)

  check package(:jar), "should contain resources and generated classes" do
    it.should contain("META-INF/persistence.xml")
    it.should contain(classfile) if classfile
  end
end
