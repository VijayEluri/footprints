require 'buildr_bnd'
require 'buildr_iidea'

basedir = File.expand_path(File.dirname(__FILE__))

$LOAD_PATH.unshift(File.expand_path("#{basedir}/../domgen/lib"))

require 'domgen'

Domgen::LoadSchema.new("#{basedir}/databases/schema_set.rb")
Domgen::GenerateTask.new(:iris, :jpa, [:jpa], "#{basedir}/generated/main/domgen") do |t|
  t.description = 'Generates the Java code for the persistent objects'
end

desc "The Footprints project"
define "footprints" do
  project.version = '0.9-SNAPSHOT'
  project.group = 'footprints'

  compile.options.source = '1.6'
  compile.options.target = '1.6'
  compile.options.lint = 'all'
  compile.enhance ["domgen:jpa"]
  compile.from _("generated/main/domgen/java")

  compile.with :javancss, :jhbasic, :ccl, :core, :jpa, :asm, :antlr, :persistence, :validation, :transaction, :intellij_annotations

  resources.enhance ["domgen:jpa"]
  mkdir_p _("generated/main/domgen/java")
  resources.from _("generated/main/domgen/java")
  resources { mkdir_p _(:target, :main, :resources); cp_r Dir["#{basedir}/generated/main/domgen/resources/*"], _(:target, :main, :resources) }

  test.using :testng
  test.with :derbytools, :derby

  task :clean do
    rm_rf _('generated')
  end

  package(:bundle).tap do |bnd|
    bnd['Export-Package'] = "iris.*;version=#{version}"
    bnd['-removeheaders'] = "Include-Resource,Bnd-LastModified,Created-By,Implementation-Title,Tool"
  end
  package(:sources)
end
