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

class CentralLayout < Layout::Default
  # +key+ is the name of the project
  # +prefix+ is the path to the parent directory for the target and dist directories
  def initialize(key, prefix)
    super()
    self[:target] = "#{prefix}target/#{key}"
    self[:target, :main] = "#{prefix}target/#{key}"
    self[:reports] = "#{prefix}reports/#{key}"
    self[:target, :generated] = "generated"
  end
end

# +key+ is the name of the project
# +top_level+ if the base directory for project is the top level
def define_with_central_layout(name, top_level = false, options = {}, &block)
  options = options.dup
  if !top_level
    options[:layout] = CentralLayout.new(name, '../')
  else
    options[:layout] = CentralLayout.new(name, '')
  end
  define(name, options) do
    project.instance_eval &block
    project.iml.local_repository_env_override = nil if project.iml?
    project.clean { rm_rf _(:target, :generated) }
    project
  end
end


desc "Footprints: See whos been walking all over our code."
define_with_central_layout('footprints', true) do
  project.version = '0.9-SNAPSHOT'
  project.group = 'footprints'

  compile.options.source = '1.6'
  compile.options.target = '1.6'
  compile.options.lint = 'all'

  project.no_ipr

  define_with_central_layout('ejb') do

    define_persistence_unit(project, :footprints, 'footprints/javancss/model/Collection.class')

    compile.with :javancss, :jhbasic, :ccl, :intellij_annotations, :javaee_api, :javax_validation

    task :clean do
      rm_rf _('generated')
    end

    iml.add_facet("EJB", "ejb") do |facet|
      facet.configuration do |c|
        c.descriptors do |d|
          d.deploymentDescriptor :name => 'ejb-jar.xml',
                                    :url => "file://$MODULE_DIR$/src/main/resources/META-INF/ejb-jar.xml"
        end
        c.ejbRoots do |e|
          e.root :url => "file://$MODULE_DIR$/generated/main/domgen/java"
          e.root :url => "file://$MODULE_DIR$/generated/main/domgen/resources"
          e.root :url => "file://$MODULE_DIR$/src/main/java"
          e.root :url => "file://$MODULE_DIR$/src/main/resources"
        end
      end
    end

    iml.add_facet("JPA", "jpa") do |f|
      f.configuration do |c|
        c.setting :name => "validation-enabled", :value => true
        c.setting :name => "provider-name", :value => 'Hibernate'
        c.tag!('datasource-mapping') do |ds|
          ds.tag!('factory-entry', :name => "footprints")
        end
        c.descriptors do |d|
          d.deploymentDescriptor :name => 'persistence.xml',
                                    :url => "file://$MODULE_DIR$/src/main/resources/META-INF/persistence.xml"
        end
      end
    end

    package(:jar, :file => _(:target, :main, "#{project.id}.jar"))
  end


  define_with_central_layout('web') do
    compile.with projects('ejb')

    iml.add_facet("Web","web") do |f|
      f.configuration do |c|
        c.descriptors do |d|
          d.deploymentDescriptor :name => 'web.xml',
            :url => "file://$MODULE_DIR$/src/main/webapp/WEB-INF/web.xml", :optional => "false", :version => "3.0"
        end
        c.webroots do |w|
          w.root :url => "file://$MODULE_DIR$/src/main/webapp", :relative => "/"
        end
      end
    end

    package(:war, :file => _(:target, :main, "#{project.id}.war"))
  end

  define_with_central_layout('ear') do
    project.no_iml
    
    package(:ear, :file => _(:target, :main, "footprints.ear")).tap do |ear|
      ear.display_name = "Footprints Code Dashboard"
      ear.add project("web").package(:war), :context_root => "footprints"
      ear.add :ejb => project('ejb'), :display_name => "Footprints Backend"
    end
  end

  iml.add_facet("JRuby", "JRUBY") do |f|
    f.configuration(:number => 0) do |c|
      c.JRUBY_FACET_CONFIG_ID :NAME => "JRUBY_SDK_NAME", :VALUE => "jruby-1.5.2-p249"
    end
  end

    # Remove generated database directories
  project.clean { rm_rf "#{File.dirname(__FILE__)}/databases/generated" }

  # Remove all generated directories
  project.clean { rm_rf "#{File.dirname(__FILE__)}/target" }

    # Exclude intermediate dirs from IDEA projects
  project.iml.excluded_directories << "#{File.dirname(__FILE__)}/target"
  project.iml.excluded_directories << "#{File.dirname(__FILE__)}/reports"

  doc.using :javadoc,
            {:tree => false, :since => false, :deprecated => false, :index => false, :help => false}
  doc.from projects('web', 'ejb')

end

define_dbt_tasks(Buildr.project("footprints"))