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

raise "Patch applied in the lastest version of buildr" unless Buildr::VERSION == '1.4.11'

module Buildr #:nodoc:
  module IntellijIdea
    class IdeaProject
      protected

      def vcs_component
        project_directories = buildr_project.projects.select { |p| p.iml? }.collect{|p| p.base_dir}
        project_directories << buildr_project.base_dir
        # Guess the iml file is in the same dir as base dir
        project_directories += self.extra_modules.collect{|p| File.dirname(p)}

        project_directories = project_directories.sort.uniq

        mappings = {}

        project_directories.each do |dir|
          if File.directory?("#{dir}/.git")
            mappings[dir] = "Git"
          elsif File.directory?("#{dir}/.svn")
            mappings[dir] = "svn"
          end
        end

        if mappings.size > 1
          create_component("VcsDirectoryMappings") do |xml|
            mappings.each_pair do |dir, vcs_type|
              resolved_dir = resolve_path(dir)
              mapped_dir = resolved_dir == '$PROJECT_DIR$/.' ? buildr_project.base_dir : resolved_dir
              xml.mapping :directory => mapped_dir, :vcs => vcs_type
            end
          end
        end
      end
    end
  end
end
