require 'buildr/git_auto_version'
require 'buildr/top_level_generate_dir'
require 'buildr/jacoco'
require 'buildr/gwt'

VALIDATOR_JARS = [
  'org.hibernate:hibernate-validator:jar:5.0.1.Final',
  'org.jboss.logging:jboss-logging:jar:3.1.3.GA',
  'com.fasterxml:classmate:jar:0.9.0'
]

download(artifact(:postgis_jdbc) => 'https://github.com/realityforge/repository/raw/master/org/postgis/postgis-jdbc/2.0.2SVN/postgis-jdbc-2.0.2SVN.jar')

desc "Footprints: See who has been walking all over our code."
define 'footprints' do
  project.group = 'org.realityforge.footprints'

  compile.options.source = '1.7'
  compile.options.target = '1.7'
  compile.options.lint = 'all'

  Domgen::GenerateTask.new(:Footprints,
                           "server",
                           [:ee, :gwt, :gwt_rpc, :auto_bean_enumeration],
                           _(:target, :generated, "domgen")) do |t|
    t.description = 'Generates the Java code for the persistent objects'
    t.verbose = !!ENV['DEBUG_DOMGEN']
  end

  compile.with :javaee_api,
               VALIDATOR_JARS,
               :javax_el,
               :eclipselink,
               :replicant,
               :replicant_sources,
               :postgresql,
               :postgis_jdbc,
               :jts,
               :geolatte_geom,
               :javax_annotation,
               :json,
               :jackson_core,
               :google_guice,
               :google_guice_assistedinject,
               :aopalliance,
               :jackson_mapper,
               :gwt_user,
               :gwt_dev,
               :gwt_gin,
               :geolatte_geom_eclipselink,
               :javax_validation_sources

  gwt(["footprints.Footprints"],
      :java_args => ["-Xms512M", "-Xmx1024M", "-XX:PermSize=128M", "-XX:MaxPermSize=256M"],
      :draft_compile => (ENV["FAST_GWT"] == 'true'))

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
  webroots = {_(:source, :main, :webapp) => "/"}
  project.assets.paths.each {|p| webroots[p] = "/"}
  iml.add_web_facet(:webroots => webroots)
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
                                                  :jts,
                                                  :geolatte_geom,
                                                  :geolatte_geom_eclipselink,
                                                  :json,
                                                  :gwt_user
                                ])
end
