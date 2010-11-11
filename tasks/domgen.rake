basedir = File.expand_path(File.dirname(__FILE__) + "/..")

$LOAD_PATH.unshift(File.expand_path("#{basedir}/../domgen/lib"))

require 'domgen'

Domgen::LoadSchema.new("#{basedir}/databases/schema_set.rb")
Domgen::GenerateTask.new(:footprints, :jpa, [:jpa_model, :jpa_ejb], "#{basedir}/ejb/generated/main/domgen") do |t|
  t.description = 'Generates the Java code for the persistent objects'
end
Domgen::GenerateTask.new(:footprints, :sql, [:sql], "#{basedir}/databases/generated") do |t|
  t.description = 'Generates the SQL for for the persistent objects'
end
