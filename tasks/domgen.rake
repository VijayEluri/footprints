#Domgen::GenerateTask.new(:footprints, :jpa, [:jpa_model, :jpa_ejb], "#{workspace_dir}/ejb/generated/main/domgen") do |t|
#  t.description = 'Generates the Java code for the persistent objects'
#end

workspace_dir = File.expand_path(File.dirname(__FILE__) + "/..")

$LOAD_PATH.unshift(File.expand_path("#{workspace_dir}/../domgen/lib"))

require 'domgen'

Domgen::LoadSchema.new("#{workspace_dir}/databases/schema_set.rb")
Domgen::GenerateTask.new(:footprints, "sql", [:sql], "#{workspace_dir}/databases/generated")
Domgen::Xmi::GenerateXMITask.new(:footprints, "xmi", "#{workspace_dir}/target/xmi/footprints.xmi")

def define_persistence_unit(project, repository_key, classfile = nil)
  task = Domgen::GenerateTask.new(repository_key, "jpa", [:jpa_model, :jpa_jta_persistence, :jpa_ejb], project._("generated/main/domgen")) do |t|
    t.description = 'Generates the Java code for the persistent objects'
  end

  project.compile.enhance [task.task_name]
  project.compile.from project._("generated/main/domgen/java")
  project.iml.main_source_directories << project._("generated/main/domgen/resources")

  project.compile.with ::HIBERNATE, :intellij_annotations, :ejb_api
  generated_resources_dir = project._("generated/main/domgen/resources/META-INF")
  generated_classes_dir = File.expand_path(project._(:target, :classes))
  project.resources.enhance [task.task_name]
  project.package(:jar).tap do |jar|
    jar.include(generated_resources_dir)
    jar.include(generated_classes_dir, :as => '.')
  end

  check package(:jar), "should contain resources and generated classes" do
    it.should contain("META-INF/persistence.xml")
    it.should contain(classfile) if classfile
  end
end
