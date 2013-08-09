require 'buildr/git_auto_version'
require 'buildr/top_level_generate_dir'
require 'buildr/jacoco'

download(artifact(:postgis_jdbc) => 'https://github.com/realityforge/repository/raw/master/org/postgis/postgis-jdbc/2.0.2SVN/postgis-jdbc-2.0.2SVN.jar')

desc "Footprints: See who has been walking all over our code."
define 'footprints' do
  project.group = 'org.realityforge.footprints'

  compile.options.source = '1.6'
  compile.options.target = '1.6'
  compile.options.lint = 'all'

  Domgen::GenerateTask.new(:Footprints,
                           "server",
                           [:ee, :gwt, :gwt_rpc, :restygwt, :auto_bean_enumeration],
                           _(:target, :generated, "domgen"),
                           project) do |t|
    t.description = 'Generates the Java code for the persistent objects'
    t.verbose = !!ENV['DEBUG_DOMGEN']
  end

  compile.with :javax_persistence,
               :javax_transaction,
               :eclipselink,
               :replicant,
               :replicant_sources,
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
               :google_guice,
               :google_guice_assistedinject,
               :aopalliance,
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
               :geolatte_geom_eclipselink,
               :javax_validation,
               :javax_validation_sources

  test.using :testng
  test.with :mockito

  gwt(["footprints.Footprints"],
      :java_args => ["-Xms512M", "-Xmx1024M", "-XX:PermSize=128M", "-XX:MaxPermSize=256M"],
      :draft_compile => (ENV["FAST_GWT"] == 'true'),
      # Closure compiler seems to result in an error in GWT/GIN code. Unknown reason
      :enable_closure_compiler => false)

  package(:war).tap do |war|
    project.assets.paths.each do |asset|
      war.enhance([asset])
      war.include asset, :as => '.'
    end
  end


  check package(:war), "should contain resources and generated classes" do
    it.should contain('WEB-INF/web.xml')
    it.should contain('WEB-INF/classes/META-INF/persistence.xml')
    it.should contain('WEB-INF/classes/META-INF/orm.xml')
    it.should contain('WEB-INF/classes/footprints/server/entity/code_metrics/Collection.class')
  end

  add_bootstrap_media(project)
  add_atmosphere_jquery_js(project)

  # Remove generated database directories
  clean { rm_rf "#{File.dirname(__FILE__)}/artifacts" }
  clean { rm_rf "#{File.dirname(__FILE__)}/databases/generated" }

  jacoco.generate_xml = true
  jacoco.generate_html = true

  doc.using :javadoc,
            {:tree => false, :since => false, :deprecated => false, :index => false, :help => false}

  ipr.extra_modules << '../dbt/dbt.iml'
  ipr.extra_modules << '../dbt_doc/dbt_doc.iml'
  ipr.extra_modules << '../domgen/domgen.iml'
  ipr.extra_modules << '../resty-gwt/restygwt/restygwt.iml'
  ipr.extra_modules << '../resty-gwt/restygwt-project.iml'
  ipr.extra_modules << '../resty-gwt/restygwt-website/restygwt-website.iml'

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
  iml.add_web_facet(:webroots => [_(:source, :main, :webapp)] + project.assets.paths)
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
                                                  :geolatte_geom_eclipselink,
                                                  :restygwt,
                                                  :json,
                                                  :slf4j_api,
                                                  :slf4j_jdk14,
                                                  :gwt_user,
                                                  :infomas_annotation_detector,

                                                  # This is horrible. Requires compat libraries
                                                  :atmosphere_compat_tomcat,
                                                  :atmosphere_compat_tomcat7,
                                                  :atmosphere_compat_jbossweb
                                ])
end
