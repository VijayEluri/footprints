require 'buildr/git_auto_version'
require 'buildr/top_level_generate_dir'

download(artifact(:postgis_jdbc) => 'https://github.com/realityforge/repository/raw/master/org/postgis/postgis-jdbc/2.0.2SVN/postgis-jdbc-2.0.2SVN.jar')

desc "Footprints: See who has been walking all over our code."
define 'footprints' do
  project.group = 'org.realityforge.footprints'

  compile.options.source = '1.6'
  compile.options.target = '1.6'
  compile.options.lint = 'all'

  Domgen::GenerateTask.new(:Footprints,
                           "server",
                           [:ee, :auto_bean],
                           _(:target, :generated, "domgen"),
                           project) do |t|
    t.description = 'Generates the Java code for the persistent objects'
    t.verbose = !!ENV['DEBUG_DOMGEN']
  end

  compile.with :javax_persistence,
               :javax_transaction,
               :eclipselink,
               :postgresql,
               :postgis_jdbc,
               :jts,
               :geolatte_geom,
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
               :infomas_annotation_detector,
               :restygwt,
               :gwt_user,
               :gwt_dev,
               :gwt_gin,
               :javax_validation,
               :javax_validation_sources

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

  desc "Database scripts package"
  define 'db', :layout => layout do
    define_dbt_tasks(project)
    Dbt.define_database_package(:default, project)
  end if false

  iml.add_gwt_facet({'footprints.FootprintsDev' => true,
                     'footprints.Footprints' => false},
                    :settings => {:compilerMaxHeapSize => "1024",
                                  :additionalCompilerParameters => '-Dgwt.usearchives=false -Dgwt.persistentunitcache=false'})

  iml.add_ejb_facet
  iml.add_jpa_facet
  iml.add_web_facet(:webroots => [_(:source, :main, :webapp), bootstrap_path, js_path])
  iml.add_jruby_facet

  ipr.add_exploded_war_artifact(project,
                                :name => 'footprints',
                                :build_on_make => true,
                                :enable_ejb => true,
                                :enable_jpa => true,
                                :enable_gwt => true,
                                :enable_war => true,
                                :output_dir => _(:artifacts, "footprints"),
                                :dependencies => [project,
                                                  :atmosphere_annotations,
                                                  :atmosphere_jquery,
                                                  :atmosphere_runtime,
                                                  :jts,
                                                  :geolatte_geom,
                                                  :restygwt,
                                                  :json,
                                                  :slf4j_api,
                                                  :slf4j_jdk14,
                                                  :infomas_annotation_detector,

                                                  # This is horrible. Requires compat libraries
                                                  :atmosphere_compat_tomcat,
                                                  :atmosphere_compat_tomcat7,
                                                  :atmosphere_compat_jbossweb
                                ])
end
