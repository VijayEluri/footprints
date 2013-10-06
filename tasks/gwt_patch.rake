# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with this
# work for additional information regarding copyright ownership.  The ASF
# licenses this file to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
# WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
# License for the specific language governing permissions and limitations under
# the License.

raise "Patch applied in the lastest version of buildr" unless Buildr::VERSION == '1.4.13'

require 'buildr/gwt'

module Buildr
  module GWT
    module ProjectExtension
      def gwt(module_names, options = {})
        output_key = options[:output_key] || project.id
        output_dir = project._(:target, :generated, :gwt, output_key)
        artifacts = (project.compile.sources + project.resources.sources).collect do |a|
          a.is_a?(String) ? file(a) : a
        end
        # HACKE Start
        dependencies = options[:dependencies] ? artifacts(options[:dependencies]) : project.compile.dependencies
        # HACKE End

        unit_cache_dir = project._(:target, :gwt, :unit_cache_dir, output_key)

        task = project.file(output_dir) do
          Buildr::GWT.gwtc_main(module_names, dependencies + artifacts, output_dir, unit_cache_dir, options.dup)
        end
        task.enhance(dependencies)
        task.enhance([project.compile])
        project.assets.paths << task
        task
      end
    end
  end
end

class Buildr::Project
  include Buildr::GWT::ProjectExtension
end
