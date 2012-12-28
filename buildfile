require 'buildr/git_auto_version'

desc "Footprints: See who has been walking all over our code."
define 'footprints' do
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

  compile.with :javax_persistence,
               :javax_transaction,
               :eclipselink,
               :ejb_api,
               :javaee_api,
               :javax_validation,
               :javax_annotation,
               :json,
               :jackson_core,
               :jackson_mapper,
               :atmosphere_annotations,
               :atmosphere_jquery,
               :atmosphere_runtime,
               :slf4j_api,
               :slf4j_jdk14,
               :infomas_annotation_detector

  test.using :testng

  package(:war)

  check package(:war), "should contain resources and generated classes" do
    it.should contain('WEB-INF/web.xml')
    it.should contain('WEB-INF/classes/META-INF/persistence.xml')
    it.should contain('WEB-INF/classes/META-INF/orm.xml')
    it.should contain('WEB-INF/classes/footprints/server/entity/code_metrics/Collection.class')
  end

  bootstrap_path = add_bootstrap_media(project)
  js_path = add_atmosphere_jquery_js(project)

  # Remove generated database directories
  clean { rm_rf "#{File.dirname(__FILE__)}/databases/generated" }

  jacoco.generate_xml = true
  jacoco.generate_html = true

  doc.using :javadoc,
            {:tree => false, :since => false, :deprecated => false, :index => false, :help => false}

  ipr.extra_modules << '../dbt/dbt.iml'
  ipr.extra_modules << '../domgen/domgen.iml'

  define 'db', :layout => layout do
    define_dbt_tasks(project)
    Dbt.define_database_package(:default, project)
  end if false

  iml.add_ejb_facet
  iml.add_jpa_facet
  iml.add_web_facet(:webroots => [_(:source, :main, :webapp), bootstrap_path, js_path])
  iml.add_jruby_facet
end

Buildr.project('footprints').ipr.add_exploded_war_artifact(project('footprints'),
                                                           :name => 'footprints',
                                                           :build_on_make => true,
                                                           :enable_ejb => true,
                                                           :enable_jpa => true,
                                                           :enable_war => true,
                                                           :output_dir => project('footprints')._(:artifacts, "footprints"),
                                                           :dependencies => [project('footprints'),
                                                                             :atmosphere_annotations,
                                                                             :atmosphere_jquery,
                                                                             :atmosphere_runtime,
                                                                             :slf4j_api,
                                                                             :slf4j_jdk14,
                                                                             :infomas_annotation_detector,

                                                                             # This is horrible. Requires compat libraries
                                                                             :atmosphere_compat_tomcat,
                                                                             :atmosphere_compat_tomcat7,
                                                                             :atmosphere_compat_jbossweb
                                                           ])
