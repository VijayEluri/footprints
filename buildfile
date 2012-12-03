require 'buildr/git_auto_version'

SLF4J = [:slf4j_api, :slf4j_jdk14, :jcl_over_slf4j]

HIBERNATE = [:hibernate_persistence,
             :hibernate_annotations,
             :javax_transaction,
             :javax_validation,
             :hibernate_validator,
             :hibernate_entitymanager,
             :hibernate_core,
             :hibernate_ehcache,
             :hibernate_c3p0,
             :dom4j,
             :hibernate_commons_annotations,
             :javassist,
             :commons_collections,
             :antlr] + SLF4J

EMF = [:eclipse_uml,
       :eclipse_common,
       :eclipse_ecore,
       :eclipse_emf_common,
       :eclipse_ecore2xml,
       :eclipse_ecore_xmi,
       :eclipse_resources]

layout = Layout::Default.new
layout[:target, :generated] = "generated"

desc "Footprints: See who has been walking all over our code."
define 'footprints', :layout => layout do
  project.group = 'org.realityforge.footprints'

  compile.options.source = '1.6'
  compile.options.target = '1.6'
  compile.options.lint = 'all'

  Domgen::GenerateTask.new(:Footprints,
                           "server",
                           [:ee],
                           _(:target, :generated, "domgen"),
                           project) do |t|
    t.description = 'Generates the Java code for the persistent objects'
    t.verbose = !!ENV['DEBUG_DOMGEN']
  end

  compile.with ::HIBERNATE,
               :ejb_api,
               :javancss,
               :jhbasic,
               :ccl,
               :javaee_api,
               :javax_validation,
               :javax_annotation,
               :json,
               :jackson_core,
               :jackson_mapper

  test.using :testng
  test.compile.with :mockito

  project.package(:war)

  check package(:war), "should contain resources and generated classes" do
    it.should contain('WEB-INF/web.xml')
    it.should contain('WEB-INF/classes/META-INF/persistence.xml')
    it.should contain('WEB-INF/classes/META-INF/orm.xml')
    it.should contain('WEB-INF/classes/footprints/server/entity/code_metrics/Collection.class')
  end

  bootstrap_path = add_bootstrap_media(project)

  # Remove generated database directories
  project.clean { rm_rf "#{File.dirname(__FILE__)}/databases/generated" }

  # Remove all generated directories
  project.clean { rm_rf _(:target, :generated) }
  project.clean { rm_rf "#{File.dirname(__FILE__)}/target" }

  project.jacoco.generate_xml = true
  project.jacoco.generate_html = true

  doc.using :javadoc,
            {:tree => false, :since => false, :deprecated => false, :index => false, :help => false}

  ipr.extra_modules << '../dbt/dbt.iml'
  ipr.extra_modules << '../domgen/domgen.iml'
  ipr.extra_modules << '../rhok-fgis/rhok-fgis2.iml'

  define 'db', :layout => layout do
    define_dbt_tasks(project)
    Dbt.define_database_package(:default, project)
  end

  iml.add_ejb_facet
  iml.add_jpa_facet
  iml.add_web_facet(:webroots => [_(:source, :main, :webapp),bootstrap_path])
  iml.add_jruby_facet
end

Buildr.project('footprints').ipr.add_exploded_war_artifact(project('footprints'),
                                                           :name => 'footprints',
                                                           :enable_ejb => true,
                                                           :enable_jpa => true,
                                                           :enable_war => true,
                                                           :output_dir => project('footprints')._(:artifacts, "footprints"),
                                                           :dependencies => [project('footprints')])
