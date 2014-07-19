require 'buildr/git_auto_version'
require 'buildr/top_level_generate_dir'
require 'buildr/jacoco'
require 'buildr/gwt'
require 'buildr/single_intermediate_layout'

VALIDATOR_JARS = [
  'org.hibernate:hibernate-validator:jar:5.0.1.Final',
  'org.jboss.logging:jboss-logging:jar:3.1.3.GA',
  'com.fasterxml:classmate:jar:0.9.0'
]

PROVIDED_DEPS = [:javaee_api, :javax_annotation, :jackson_mapper, :jackson_core, :jersey_mvc, :jersey_mvc_jsp, :metro_webservices_rt] + VALIDATOR_JARS
GWT_DEPS = [:gwt_datatypes,
            :google_guice,
            :google_guice_assistedinject,
            :aopalliance,
            :gwt_user,
            :gwt_dev,
            :gwt_gin,
            :javax_validation_sources]
COMPILE_DEPS = [:gwt_user, :simple_session_filter]
PACKAGE_DEPS = [:gwt_cache_filter] + COMPILE_DEPS

desc 'Footprints: See who has been walking all over our code.'
define 'footprints' do
  project.group = 'org.realityforge.footprints'

  compile.options.source = '1.7'
  compile.options.target = '1.7'
  compile.options.lint = 'all'

  Domgen::GenerateTask.new(:Footprints, 'server', [:ee, :gwt, :gwt_rpc], _(:target, :generated, 'domgen'))

  compile.with PROVIDED_DEPS,
               COMPILE_DEPS,
               GWT_DEPS

  test.using :testng
  test.with :mockito

  gwt_superdev_runner('footprints.FootprintsDev',
                      :java_args => ['-Xms512M', '-Xmx1024M', '-XX:PermSize=128M', '-XX:MaxPermSize=256M'],
                      :draft_compile => (ENV['FAST_GWT'] == 'true'))
  gwt_dir = gwt(['footprints.Footprints'],
                :java_args => ['-Xms512M', '-Xmx1024M', '-XX:PermSize=128M', '-XX:MaxPermSize=256M'],
                :draft_compile => (ENV['FAST_GWT'] == 'true'))

  package(:war).tap do |war|
    war.libs.clear
    war.libs.concat Buildr.artifacts(PACKAGE_DEPS)
  end

  check package(:war), 'should contain resources and generated classes' do
    it.should contain('WEB-INF/web.xml')
    it.should contain('WEB-INF/classes/META-INF/persistence.xml')
    it.should contain('WEB-INF/classes/META-INF/orm.xml')
    it.should contain('WEB-INF/classes/footprints/server/entity/code_metrics/Collection.class')
  end

  desc 'DB Archive'
  define 'db' do
    project.no_iml
    Dbt.define_database_package(:default, nil, :include_code => false)
  end

  # Remove generated database directories
  clean { rm_rf "#{File.dirname(__FILE__)}/artifacts" }
  clean { rm_rf "#{File.dirname(__FILE__)}/database/generated" }

  jacoco.generate_xml = true
  jacoco.generate_html = true

  doc.using :javadoc,
            {:tree => false, :since => false, :deprecated => false, :index => false, :help => false}

  iml.add_gwt_facet({'footprints.FootprintsDev' => true,
                     'footprints.Footprints' => false},
                    :settings => {:compilerMaxHeapSize => '1024'},
                    :gwt_dev_artifact => :gwt_dev)
  iml.add_ejb_facet
  iml.add_jpa_facet

  # Hacke to remove GWT from path
  webroots = {}
  webroots[_(:source, :main, :webapp)] = '/' if File.exist?(_(:source, :main, :webapp))
  assets.paths.each { |path| webroots[path.to_s] = '/' if path.to_s != gwt_dir.to_s }
  iml.add_web_facet(:webroots => webroots)

  iml.add_jruby_facet

  ipr.add_exploded_war_artifact(project,
                                :build_on_make => true,
                                :enable_ejb => true,
                                :enable_jpa => true,
                                :enable_gwt => true,
                                :enable_war => true,
                                :dependencies => [project, PACKAGE_DEPS])
end
