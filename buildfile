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
  def initialize(key, top_level, use_subdir)
    super()
    prefix = top_level ? '' : '../'
    subdir = use_subdir ? "/#{key}" : ''
    self[:target] = "#{prefix}target#{subdir}"
    self[:target, :main] = "#{prefix}target#{subdir}"
    self[:reports] = "#{prefix}reports#{subdir}"
  end
end

def define_with_central_layout(name, top_level = false, use_subdir = true, & block)
  define(name, :layout => CentralLayout.new(name, top_level, use_subdir), & block)
end

desc "Footprints: See whos been walking all over our code."
define_with_central_layout('footprints', true, false) do
  project.version = '0.9-SNAPSHOT'
  project.group = 'footprints'

  compile.options.source = '1.6'
  compile.options.target = '1.6'
  compile.options.lint = 'all'

  project.no_ipr

  define_with_central_layout('ejb',false) do

    define_persistence_unit(project, :footprints, 'footprints/javancss/model/Collection.class')

    compile.with :javancss, :jhbasic, :ccl, :intellij_annotations, :javaee_api, :javax_validation

    task :clean do
      rm_rf _('generated')
    end

    package(:jar, :file => _(:target, :main, "#{project.id}.jar"))
  end


  define_with_central_layout('web',false) do
    compile.with projects('ejb')

    iml.add_facet("Web","web") do |facet|
      facet.configuration do |conf|
        conf.descriptors do |desc|
          desc.deploymentDescriptor :name => 'web.xml',
            :url => "file://$MODULE_DIR$/src/main/webapp/WEB-INF/web.xml", :optional => "false", :version => "3.0"
        end
        conf.webroots do |webroots|
          webroots.root :url => "file://$MODULE_DIR$/src/main/webapp", :relative => "/"
        end
      end
    end

    package(:war, :file => _(:target, :main, "#{project.id}.war"))
  end

  define_with_central_layout("ear",false) do
    project.no_iml
    
    package(:ear, :file => _(:target, :main, "footprints.ear")).tap do |ear|
      ear.display_name = "Footprints Code Dashboard"
      ear.add project("web").package(:war), :context_root => "footprints"
      ear.add :ejb => project('ejb'), :display_name => "Footprints Backend"
    end
  end

    # Remove generated database directories
  project.clean { rm_rf "#{File.dirname(__FILE__)}/databases/generated" }

  # Remove all generated directories
  project.clean { rm_rf "#{File.dirname(__FILE__)}/target" }

end

define_dbt_tasks(Buildr.project("footprints"))