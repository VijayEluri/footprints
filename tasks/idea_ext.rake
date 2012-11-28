module Buildr::IntellijIdea

  class IdeaModule

    def add_web_facet(options = {})
      name = options[:name] || "Web"
      url_base = options[:url_base] || "/"
      default_webroots = [buildr_project._(:source, :main, :webapp)]
      webroots = options[:webroots] || default_webroots
      default_web_xml = "#{buildr_project._(:source, :main, :webapp)}/WEB-INF/web.xml"
      web_xml = options[:web_xml] || "#{buildr_project._(:source, :main, :webapp)}/WEB-INF/web.xml"
      version = options[:version] || "3.0"

      add_facet(name, "web") do |f|
        f.configuration do |c|
          c.descriptors do |d|
            if File.exist?(web_xml) || default_web_xml != web_xml
              d.deploymentDescriptor :name => 'web.xml', :url => file_path(web_xml), :optional => "true", :version => version
            end
          end
          c.webroots do |w|
            webroots.each do |webroot|
              w.root :url => file_path(webroot), :relative => url_base
            end
          end
        end
      end
    end

  end
end
